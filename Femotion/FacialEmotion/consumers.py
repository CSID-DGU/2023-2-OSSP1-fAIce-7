import json
import cv2
import numpy as np
from channels.generic.websocket import AsyncWebsocketConsumer
from tensorflow.keras.models import model_from_json
from tensorflow.keras.preprocessing.image import img_to_array
import os
import base64
import binascii
import logging

# JSON 파일에서 모델 로드
json_file_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'top_models/fer.json')
json_file = open(json_file_path, 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)

# 학습된 모델의 가중치 로드
model_weights_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'top_models/fer.h5')
model.load_weights(model_weights_path)

# 감정 목록 정의
emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']

# 감정에 대한 색상 정의
emotion_colors_bgr = {
    'neutral': (230, 216, 173),
    'happiness': (0, 165, 255),
    'surprise': (0, 252, 124),
    'sadness': (180, 130, 70),
    'anger': (0, 0, 255),
    'disgust': (19, 69, 139),
    'fear': (128, 0, 128)
    }
    
def render_emotion_info(image, faces, emotion_labels):
    for (x, y, w, h), emotion in zip(faces, emotions):
        cv2.rectangle(image, (x, y), (x+w, y+h), emotion_colors_bgr[emotion], 2)
        cv2.putText(image, emotion, (x, y-10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, emotion_colors_bgr[emotion], 2)
    return image

def repair_base64(data):
    missing_padding = len(data) % 4
    if missing_padding:
        data += '=' * (4 - missing_padding)
    return data
    
class JSONEncoderWithNumpy(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, (np.int32, np.int64)):
            return int(obj)
        elif isinstance(obj, (np.float32, np.float64)):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        return super().default(obj)

class EmotionConsumer(AsyncWebsocketConsumer):
    temp_screenshot = None
    should_save_screenshot = False

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.face_cascade_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'haarcascade_frontalface_default.xml')
        self.face_cascade = cv2.CascadeClassifier(self.face_cascade_path)
        self.selected_emotion = None
        self.min_people = 0
        self.emotion_threshold = 0.0
    
    async def connect(self):
        await self.channel_layer.group_add("emotion_group", self.channel_name)
        global selected_emotion, min_people_global, emotion_threshold_global
        print(f"Selected Emotion: {selected_emotion}")
        print(f"min_people_global: {min_people_global}")
        print(f"emotion_threshold_global: {emotion_threshold_global}")
        print("웹소켓 연결완료")
        await self.accept()
        
    async def update_emotion(self, event):
        self.selected_emotion = event['selected_emotion']
        self.min_people = event['min_people']
        self.emotion_threshold = event['emotion_threshold']
        print(f"Selected Emotion: {self.selected_emotion}")
        print(f"Min People: {self.min_people}")
        print(f"Emotion Threshold: {self.emotion_threshold}")
        
    async def disconnect(self, close_code):
        await self.channel_layer.group_discard("emotion_group", self.channel_name)

    async def start_stream(self, text_data_json):
        print("메세지를 받는 중")
        text_data_json = json.loads(text_data)
        min_people = text_data_json.get('min_people', 0)
        emotion_threshold = text_data_json.get('emotion_threshold', 0.0)
        print(text_data_json)
        global should_save_screenshot
        should_save_screenshot = False
        self.emotion_threshold = float(text_data_json.get('emotion_threshold'))
        print(f"Selected Emotion: {self.selected_emotion}")
        print(f"Min People: {self.min_people}")
        print(f"Emotion Threshold: {self.emotion_threshold}")
        
        # Extract frame data from the received message
        frame_data = text_data_json.get('frameData').replace("data:image/jpeg;base64,", "")
        frame_decoded_data = base64.b64decode(frame_data)
        
        decoded_data = None

        try:
            decoded_data = base64.b64decode(repair_base64(frame_data))
        except binascii.Error:
            pass

        nparr = np.frombuffer(decoded_data, np.uint8)
        frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        
        if frame is None or frame.size == 0:
            print("이미지 디코딩 에러")
            detected_emotions = []
            valid_people_count = 0
            emotion_probability = 0.0
            should_save_screenshot = False
        else:
            print(frame.shape)
            detected_emotions, emotion_probability, valid_people_count = self.detect_emotions(frame, self.emotion_threshold,  self.selected_emotion)

        # Analyze the frame and send it back to the client
        while not should_save_screenshot:
            detected_emotions, emotion_probability, valid_people_count = self.detect_emotions(frame, self.emotion_threshold, self.selected_emotion)
            faces = [emotion['coordinates'] for emotion in detected_emotions]
            emotion_labels = [emotion['emotion_label'] for emotion in detected_emotions]
            frame = render_emotion_info(frame, faces, emotion_labels)
            _, buffer = cv2.imencode('.jpg', frame)
            frame_base64 = base64.b64encode(buffer).decode('utf-8')
            await self.send(text_data=json.dumps({
                'frame': frame_base64,
                'detected_emotions': detected_emotions,
                'emotion_probability': emotion_probability,
                'valid_people_count': valid_people_count,
                'should_save_screenshot': should_save_screenshot,
            }))
        
    async def save_screenshot(self):
        global temp_screenshot
        if temp_screenshot is not None:
            await self.send(text_data_json=json.dumps({
                'message_type': 'screenshot',
                'image': temp_screenshot
            }))

    async def restart_stream(self):
        global should_save_screenshot, temp_screenshot
        should_save_screenshot = False
        temp_screenshot = None
        await self.start_stream(text_data_json)

    async def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message_type = text_data_json.get('type')

        if message_type == 'start_stream':
            await self.start_stream(text_data_json)
        elif message_type == 'save_button':
            await self.save_screenshot()
        elif message_type == 'restart_stream':
            await self.restart_stream()

    def detect_emotions(self, frame_data, emotion_threshold, selected_emotion):
        gray = cv2.cvtColor(frame_data, cv2.COLOR_BGR2GRAY)
        faces = self.face_cascade.detectMultiScale(gray, scaleFactor=1.3, minNeighbors=5, minSize=(30, 30), flags=cv2.CASCADE_SCALE_IMAGE)

        emotion_probability = 0.0
        detected_emotions = []
        valid_people_count = 0
        for i, (x, y, w, h) in enumerate(faces):  # Using enumerate to get index
            face_roi = gray[y:y + h, x:x + w]
            face_roi = cv2.resize(face_roi, (48, 48))
            face_roi = face_roi.astype("float") / 255.0
            face_roi = img_to_array(face_roi)
            face_roi = np.expand_dims(face_roi, axis=0)
            
            index = emotions.index(selected_emotion) if selected_emotion is not None else 0
            emotion_threshold = self.emotion_threshold if self.emotion_threshold is not None else 0

            emotion_prediction = model.predict(face_roi)[0]
            emotion_probability = round(emotion_prediction[index] * 100, 2)

            if emotion_probability >= emotion_threshold:
                valid_people_count += 1

            emotion_label = f"Person {i+1}"

            detected_emotions.append({
                'emotion_label': emotion_label,
                'emotion_probability': emotion_probability,
                'coordinates': {'x': x, 'y': y, 'w': w, 'h': h},
                'color': emotion_colors_bgr[selected_emotion]
            })
            
            # 디버깅 코드
            print("----- Detected Emotion {} -----".format(i+1))
            print("Emotion Label:", emotion_label)
            print("Emotion Probability:", emotion_probability)
            print("Coordinates:", {'x': x, 'y': y, 'w': w, 'h': h})
            print("Color:", emotion_colors_bgr[selected_emotion])
            print("-------------------------------")
            
        return detected_emotions, emotion_probability, valid_people_count

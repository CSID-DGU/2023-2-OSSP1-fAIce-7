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
        # 좌표 유효성 검사
        if not all(isinstance(coord, int) for coord in [x, y, w, h]):
            print(f"Invalid coordinates: {(x, y, w, h)}")
            continue  # Skip this iteration if coordinates are invalid

        # 색상 유효성 검사
        if emotion not in emotion_colors_bgr:
            print(f"Invalid emotion: {emotion}")
            continue  # Skip this iteration if emotion is invalid
    
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

    def detect_faces_dnn(self, image):
        h, w = image.shape[:2]
        blob = cv2.dnn.blobFromImage(cv2.resize(image, (300, 300)), 1.0, (300, 300), (104.0, 177.0, 123.0))
        self.dnn_net.setInput(blob)
        detections = self.dnn_net.forward()
        faces = []
        for i in range(detections.shape[2]):
            confidence = detections[0, 0, i, 2]
            if confidence > 0.5:  # You can adjust this threshold
                box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
                (startX, startY, endX, endY) = box.astype("int")
                faces.append((startX, startY, endX-startX, endY-startY))
        return faces
        temp_screenshot = None
        should_save_screenshot = False

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.face_cascade_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'haarcascade_frontalface_default.xml')
        self.face_cascade = cv2.CascadeClassifier(self.face_cascade_path)

        # Load DNN based face detection model
        self.dnn_net = cv2.dnn.readNetFromCaffe(os.path.join(os.path.dirname(os.path.abspath(__file__)), "deploy.prototxt"), os.path.join(os.path.dirname(os.path.abspath(__file__)), "res10_300x300_ssd_iter_140000_fp16.caffemodel"))
        self.frame_data = None
        self.selected_emotion = None
        self.min_people = 0
        self.emotion_threshold = 0.0
    
    async def connect(self):
        await self.channel_layer.group_add("emotion_group", self.channel_name)
        print("웹소켓 연결완료")
        await self.accept()
        
    async def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message_type = text_data_json.get('type')
        print(f"Available keys: {text_data_json.keys()}")

        if message_type == 'start_stream':
            await self.start_stream(text_data_json)
        elif message_type == 'save_button':
            await self.save_screenshot()
        elif message_type == 'restart_stream':
            await self.restart_stream()
        
    async def update_emotion(self, event):
        # print(f"update_emotion 데이터: {event}")
        self.frame_data = event['frameData'].replace("data:image/jpeg;base64,", "")
        self.selected_emotion = event['selected_emotion']
        self.min_people = int(event['min_people'])
        self.emotion_threshold = float(event['emotion_threshold'])
        # print(f"Selected Emotion: {self.selected_emotion}")
        # print(f"Min People: {self.min_people}")
        #  print(f"Emotion Threshold: {self.emotion_threshold}")
        print("emotion_updated!")
        
    async def disconnect(self, close_code):
        await self.channel_layer.group_discard("emotion_group", self.channel_name)

    async def start_stream(self, text_data_json):
        print("메세지를 받는 중")
        # text_data_json = json.loads(text_data)
        await self.update_emotion(text_data_json)
        # selected_emotion = text_data_json.get('selected_emotion')
        # min_people = int(text_data_json.get('min_people'))
        # emotion_threshold = float(text_data_json.get('emotion_threshold'))
        # print(text_data_json)
        global should_save_screenshot
        should_save_screenshot = False
        print(f"Selected Emotion: {self.selected_emotion}")
        print(f"Min People: {self.min_people}")
        print(f"Emotion Threshold: {self.emotion_threshold}")
        
        # Extract frame data from the received message
        # frame_data = text_data_json.get('frameData').replace("data:image/jpeg;base64,", "")
        print(f"Received frame data (last 100 chars): {self.frame_data[-100:]}")
        frame_decoded_data = base64.b64decode(self.frame_data)
        
        decoded_data = None

        try:
            decoded_data = base64.b64decode(repair_base64(self.frame_data))
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

        # 프레임 분석 후 클라이언트에 데이터 전송
        _, buffer = cv2.imencode('.jpg', frame)
        frame_base64 = base64.b64encode(buffer).decode('utf-8')
        await self.send(text_data=json.dumps({
            'frame': frame_base64,
            'detected_emotions': detected_emotions,
            'emotion_probability': emotion_probability,
            'valid_people_count': valid_people_count,
            'should_save_screenshot': should_save_screenshot,
        }, cls=JSONEncoderWithNumpy))
            # print(f"Send to Client frame data (last 100 chars): {frame_data[-100:]}")
            # print(f"detected_emotions: {detected_emotions}")
            # print(f"emotion_probability: {emotion_probability}")
            # print(f"valid_people_count: {valid_people_count}")
            # print(f"should_save_screenshot: {should_save_screenshot}")
            
        # if should_save_screenshot:
            
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

    def detect_emotions(self, frame_data, emotion_threshold, selected_emotion):
        gray2 = cv2.cvtColor(frame_data, cv2.COLOR_BGR2GRAY)
        gray = cv2.equalizeHist(gray2)
        faces = self.detect_faces_dnn(frame_data)
        print(f"Detected {len(faces)} faces")

        emotion_probability = 0.0
        detected_emotions = []
        valid_people_count = 0
        for i, (x, y, w, h) in enumerate(faces):  # Using enumerate to get index
            face_roi = gray[y:y + h, x:x + w]
            face_roi = cv2.resize(face_roi, (48, 48))
            face_roi = face_roi.astype("float") / 255.0
            face_roi = img_to_array(face_roi)
            face_roi = np.expand_dims(face_roi, axis=0)
            
            index = emotions.index(self.selected_emotion) if self.selected_emotion is not None else 0
            emotion_threshold = self.emotion_threshold if self.emotion_threshold is not None else 0

            emotion_prediction = model.predict(face_roi)[0]
            emotion_probability = round(emotion_prediction[index] * 100, 2)
            print(f"Emotion predictions: {emotion_prediction}, Selected emotion index: {index}, Probability: {emotion_probability}%")

            if emotion_probability >= emotion_threshold:
                valid_people_count += 1
            print(f"Valid people count: {valid_people_count}")
            
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
        
        # faces_info = [emotion['coordinates'] for emotion in detected_emotions]
        # emotion_labels = [emotion['emotion_label'] for emotion in detected_emotions]
        # frame_data_info = render_emotion_info(frame_data, faces_info, emotion_labels)
            
        return detected_emotions, emotion_probability, valid_people_count

import cv2
import numpy as np
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.image import Image
from kivy.clock import Clock
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.widget import Widget
from kivy.uix.popup import Popup
from kivy.uix.filechooser import FileChooserListView
from kivy.core.text import LabelBase
from kivy.graphics.texture import Texture
from keras.models import model_from_json
from keras.utils import img_to_array
import matplotlib.pyplot as plt
import mplcursors
import time

# 한글 폰트 설정
LabelBase.register(name='NanumGothic', fn_regular='./NanumGothicCoding.ttf')

# Kivy 설정 변경
from kivy.config import Config
Config.set('graphics', 'fullscreen', '0')  # 전체 화면 모드 비활성화
Config.set('graphics', 'width', '800')     # 화면 너비 설정
Config.set('graphics', 'height', '600')    # 화면 높이 설정

# Kivy 앱 클래스 정의
class EmotionRecognitionApp(App):
    def build(self):
        # 화면 레이아웃 및 위젯 생성
        self.layout = BoxLayout(orientation='vertical')
        self.image = Image()
        self.layout.add_widget(self.image)
        self.load_button = Button(text='load video')
        self.layout.add_widget(self.load_button)
        self.layout.add_widget(Label(text='감정 분석 결과:', font_name='NanumGothic', font_size=20))
        self.emotion_label = Label(font_name='NanumGothic', font_size=20)
        self.layout.add_widget(self.emotion_label)
        self.load_button.bind(on_press=self.load_video)
        self.figure, self.ax = plt.subplots(figsize=(10, 3))
        self.ax.set_ylim(0, 100)
        self.ax.set_xlabel('시간 (초)')
        self.ax.set_ylabel('감정 값')
        self.lines = {}
        self.text_annotation = self.ax.text(0.5, 0.95, "", transform=self.ax.transAxes, va='top', ha='center', fontsize=10, bbox=dict(boxstyle='round,pad=0.5', facecolor='white', alpha=0.8))
        return self.layout

    def load_video(self, instance):
        # 파일 선택 다이얼로그 표시
        file_chooser = FileChooserListView()
        file_chooser.bind(on_submit=self.load_video_file)
        popup = Popup(title='비디오 선택', content=file_chooser, size_hint=(0.9, 0.9))
        popup.open()

    def load_video_file(self, instance, selected_file, *args):
        # 비디오 파일을 선택하면 이 함수가 호출됨
        self.video_file_path = selected_file[0]
        self.load_emotion_model()
        self.capture = cv2.VideoCapture(self.video_file_path)
        self.update_video_frame()
        self.timestamps = []
        self.emotion_history = {emotion: [] for emotion in emotions}
        Clock.schedule_interval(self.update_video_frame, 1.0 / 30)  # 30 FPS

    def load_emotion_model(self):
        # 감정 인식 모델을 로드
        json_file = open('top_models/fer.json', 'r')
        loaded_model_json = json_file.read()
        json_file.close()
        self.model = model_from_json(loaded_model_json)
        self.model.load_weights('top_models/fer.h5')

    def update_video_frame(self, dt):
        ret, frame = self.capture.read()
        if ret:
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            self.display_frame(frame)

    def display_frame(self, frame):
        emotion_labels, emotion_probabilities = self.detect_emotion(frame)
        frame = self.draw_emotion_labels(frame, emotion_labels, emotion_probabilities)
        self.image.texture = self.frame_to_texture(frame)

    def detect_emotion(self, frame):
        gray_img = cv2.cvtColor(frame, cv2.COLOR_RGB2GRAY)
        faces_detected = self.face_detection(gray_img)

        if faces_detected:
            roi_gray = gray_img[faces_detected[0]['y']:faces_detected[0]['y'] + faces_detected[0]['h'],
                                faces_detected[0]['x']:faces_detected[0]['x'] + faces_detected[0]['w']]
            roi_gray = cv2.resize(roi_gray, (48, 48))
            img_pixels = img_to_array(roi_gray)
            img_pixels = np.expand_dims(img_pixels, axis=0)
            img_pixels /= 255.0

            predictions = self.model.predict(img_pixels)
            emotion_labels = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']
            emotion_probabilities = predictions[0] * 100

            # Update emotion history and timestamps
            current_time = time.time()
            self.timestamps.append(current_time - self.start_time)
            for i, emotion in enumerate(emotion_labels):
                self.emotion_history[emotion].append(emotion_probabilities[i])

            # Update emotion graph
            self.update_emotion_graph()

            return emotion_labels, emotion_probabilities
        else:
            return ['No Face Detected'], [0]

    def face_detection(self, gray_img):
        face_haar_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.2, 6)
        if len(faces_detected) > 0:
            x, y, w, h = faces_detected[0]
            return [{'x': x, 'y': y, 'w': w, 'h': h}]
        else:
            return []

    def draw_emotion_labels(self, frame, emotion_labels, emotion_probabilities):
        for i, label in enumerate(emotion_labels):
            text = f'{label}: {emotion_probabilities[i]:.2f}%'
            position = (10, 30 + i * 30)
            color = (255, 255, 255)
            font = cv2.FONT_HERSHEY_SIMPLEX
            font_scale = 0.8
            thickness = 2
            frame = cv2.putText(frame, text, position, font, font_scale, color, thickness, cv2.LINE_AA)
        return frame

    def frame_to_texture(self, frame):
        buf1 = cv2.flip(frame, 0)
        buf = buf1.tostring()
        texture = Texture.create(size=(frame.shape[1], frame.shape[0]), colorfmt='rgb')
        texture.blit_buffer(buf, colorfmt='rgb', bufferfmt='ubyte')
        return texture

    def update_emotion_graph(self):
        self.ax.cla()
        self.ax.set_ylim(0, 100)
        for emotion, history in self.emotion_history.items():
            self.lines[emotion], = self.ax.plot(self.timestamps, history, label=emotion)
        self.ax.set_xlabel('시간 (초)')
        self.ax.set_ylabel('감정 값')
        self.ax.legend()
        self.ax.set_title('감정 분석 결과 그래프')
        self.text_annotation.set_text("")  # Clear text annotation
        self.text_annotation.set_visible(False)
        self.figure.canvas.draw()

    def on_start(self):
        self.start_time = time.time()
        self.timestamps = []
        self.emotion_history = {emotion: [] for emotion in emotions}
        self.lines = {}
        self.text_annotation = self.ax.text(0.5, 0.95, "", transform=self.ax.transAxes, va='top', ha='center', fontsize=10, bbox=dict(boxstyle='round,pad=0.5', facecolor='white', alpha=0.8))

if __name__ == '__main__':
    emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']
    EmotionRecognitionApp().run()

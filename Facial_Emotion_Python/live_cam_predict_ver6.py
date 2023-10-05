import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import img_to_array
import time
import tkinter as tk
from tkinter import ttk
import os
from PIL import Image
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.image import Image as KivyImage
from kivy.uix.label import Label
from kivy.uix.popup import Popup
from kivy.uix.filechooser import FileChooserListView
from kivy.uix.button import Button
import requests
from kivy.core.text import LabelBase

# 웹에서 폰트 파일 다운로드
font_url = "https://hangeul.pstatic.net/hangeul_static/webfont/NanumGothic/NanumGothic.ttf"
response = requests.get(font_url)
if response.status_code == 200:
    # 다운로드한 폰트 파일을 로컬에 저장
    with open("downloaded_font.ttf", "wb") as font_file:
        font_file.write(response.content)

    # Kivy 폰트로 등록
    LabelBase.register(name="DownloadedFont", fn_regular="downloaded_font.ttf")

    # Kivy 앱에서 폰트 사용
    label = Label(text="안녕하세요", font_name="DownloadedFont")
else:
    print("폰트 다운로드 실패")

# JSON 파일에서 모델 로드
json_file = open('top_models/fer.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)

# 학습된 모델의 가중치를 로드
model.load_weights('top_models/fer.h5')

# 얼굴을 감지하기 위한 Haar Cascade 분류기 로드
face_haar_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
cap = cv2.VideoCapture(0)

# 웃음, 놀람 등의 감정 목록 정의
emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']

# Tkinter 루트 창 생성
root = tk.Tk()
root.withdraw()

# 감정에 대한 BGR 형식의 색상 정의
emotion_colors_bgr = {
    'neutral': (230, 216, 173),
    'happiness': (0, 165, 255),
    'surprise': (0, 252, 124),
    'sadness': (180, 130, 70),
    'anger': (0, 0, 255),
    'disgust': (19, 69, 139),
    'fear': (128, 0, 128)
}

# 감정을 영문으로 변환하는 딕셔너리 정의
emotion_translation = {
    '웃을': 'happiness',
    '놀랄': 'surprise',
    '슬플': 'sadness',
    '화날': 'anger',
    '역겨울': 'disgust',
    '무서울': 'fear'
}

# 여러 입력을 받기 위한 MultiInputDialog 클래스 정의
class MultiInputDialog(tk.Toplevel):
    def __init__(self, parent, title, prompts, combo_values):
        super().__init__(parent)
        self.title(title)
        self.results = []
        self.prompts = prompts
        self.combo_values = combo_values
        self.create_widgets()
    
    def create_widgets(self):
        for i, prompt in enumerate(self.prompts):
            label = tk.Label(self, text=prompt)
            label.pack()
            if self.combo_values[i]:
                # Combobox와 레이블을 포함하는 프레임 생성
                combo_frame = tk.Frame(self)
                combo_frame.pack()
                
                # Combobox 생성
                combo = ttk.Combobox(combo_frame, values=self.combo_values[i], width=5)  # 필요에 따라 너비 조절
                combo.pack(side=tk.LEFT)
                combo.set(self.combo_values[i][0])  # 기본값 설정
                
                # Combobox 오른쪽에 '명' 또는 '때' 레이블 추가
                unit_label = tk.Label(combo_frame, text='명' if i == 0 else '때')
                unit_label.pack(side=tk.LEFT)
                
                self.results.append(combo)
            else:
                entry = tk.Entry(self)
                entry.pack()
                self.results.append(entry)
        
        button = tk.Button(self, text="OK", command=self.ok)
        button.pack()
    
    def ok(self):
        self.withdraw()
        self.update_idletasks()
        self.results = [entry.get() if isinstance(entry, tk.Entry) else entry.get() for entry in self.results]
        self.destroy()

# MultiInputDialog를 사용하여 여러 입력 받기 위한 함수 정의
def get_multiple_inputs(title, prompts, combo_values):
    root = tk.Tk()
    root.withdraw()
    dialog = MultiInputDialog(root, title, prompts, combo_values)
    root.wait_window(dialog)
    return dialog.results

# 감정에 대한 Combobox 값 정의
combo_values_emotion = ['웃을', '놀랄', '슬플', '화날', '역겨울', '무서울']
# 최소 인원 수 (1-10)에 대한 Combobox 값 정의
combo_values_min_people = [str(i) for i in range(1, 11)]

# Kivy 애플리케이션 클래스 정의
class EmotionRecognitionApp(App):
    def build(self):
        self.layout = BoxLayout(orientation='vertical')
        self.image = KivyImage()
        self.layout.add_widget(self.image)
        self.label = Label(text='screen capture')
        self.layout.add_widget(self.label)
        self.process_frames = True
        self.capture = cv2.VideoCapture(0)
        self.screenshot_mode = False
        self.screenshot_saved = False
        self.time_threshold = 0
        self.selected_dir = None  # 선택한 디렉토리 경로를 저장하는 변수 추가
        return self.layout

    def get_emotion_color_bgr(self, emotion):
        return emotion_colors_bgr.get(emotion, (255, 255, 255))

    def opencv_image_to_texture(self, frame):
        frame = cv2.flip(frame, 0)
        texture = Texture.create(size=(frame.shape[1], frame.shape[0]), colorfmt='bgr')
        texture.blit_buffer(frame.tostring(), colorfmt='bgr', bufferfmt='ubyte')
        return texture

    def process_frame(self, frame, selected_emotion, min_people):
        gray_img = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.1, 6, minSize=(150, 150))
        valid_people_count = 0

        for i, (x, y, w, h) in enumerate(faces_detected):
            roi_gray = gray_img[y:y + h, x:x + w]
            roi_gray = cv2.resize(roi_gray, (48, 48))
            img_pixels = img_to_array(roi_gray)
            img_pixels = np.expand_dims(img_pixels, axis=0)
            img_pixels /= 255.0
            predictions = model.predict(img_pixels)
            person_label = f'person {i + 1}'

            selected_emotion_english = emotion_translation.get(selected_emotion)
            if selected_emotion_english is None:
                print("선택한 감정을 영문으로 변환할 수 없습니다.")
                continue

            emotion_thresholds = {
                'happiness': 50,
                'surprise': 50,
                'sadness': 30,
                'anger': 10,
                'disgust': 5,
                'fear': 5
            }

            cv2.rectangle(frame, (x, y), (x + w, y + h), emotion_colors_bgr[selected_emotion_english], thickness=2)
            emotion_value = predictions[0][emotions.index(selected_emotion_english)]
            text = f'{selected_emotion_english}: {emotion_value * 100:.2f}%'
            text_color = emotion_colors_bgr[selected_emotion_english]
            cv2.putText(frame, text, (x, y - 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, text_color, 2)
            
            if predictions[0][emotions.index(selected_emotion_english)] >= emotion_thresholds.get(selected_emotion_english, 0):
                valid_people_count += 1

        if valid_people_count >= min_people:
            if not self.screenshot_mode:
                self.time_threshold = time.time()
                self.screenshot_mode = True
        else:
            self.time_threshold = 0

        if self.screenshot_mode and not self.screenshot_saved:
            if time.time() >= self.time_threshold + 2:  # 스크린샷 모드 활성화 2초 후
                self.capture_screenshot()  # 스크린샷 캡처 메서드 호출
                self.screenshot_saved = True

        if self.screenshot_saved:
            self.screenshot_mode = False

        if not self.screenshot_mode:
            self.image.texture = self.opencv_image_to_texture(frame)

    def capture_screenshot(self):
        if self.selected_dir:
            ret, frame = self.capture.read()
            if ret:
                frame_filename = f"감정카메라_캡처_{time.strftime('%Y%m%d%H%M%S')}.jpg"
                frame_save_path = os.path.join(self.selected_dir, frame_filename)
                pil_image = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
                if os.access(self.selected_dir, os.W_OK):
                    pil_image.save(frame_save_path)
                    print(f"스크린샷이 {frame_save_path}에 저장되었습니다.")
                else:
                    print("선택한 디렉토리에 쓰기 권한이 없습니다. 다른 디렉토리를 선택하십시오.")
        else:
            print("스크린샷 저장 디렉토리가 선택되지 않았습니다.")

    def on_start(self):
        prompts = ["최소 몇 명 이상이", "다음과 같은 표정을 지웠을 때"]
        combo_values = [combo_values_min_people, combo_values_emotion]
        results = get_multiple_inputs("화면 캡처 설정", prompts, combo_values)
        if results:
            self.min_people = int(results[0])
            self.selected_emotion = results[1]
            print(f"최소 인원 수: {self.min_people}명")
            print(f"선택한 감정: {self.selected_emotion} 때")
            self.open_filechooser()  # 파일 대화 상자 열기
        else:
            print("사용자가 입력을 취소하거나 유효한 입력을 제공하지 않았습니다.")
            App.get_running_app().stop()

    def open_filechooser(self):
        filechooser = FileChooserListView()
        filechooser.path = os.path.expanduser("~")  # 초기 경로 설정 (원하는 경로로 변경)
        popup_content = BoxLayout(orientation="vertical")
        
        # "선택" 버튼 추가
        select_button = Button(text="선택")
        select_button.bind(on_release=lambda btn: self.file_selected(filechooser))
        popup_content.add_widget(filechooser)
        popup_content.add_widget(select_button)
        
        popup = Popup(title="스크린샷 저장 디렉토리 선택", content=popup_content, size_hint=(None, None), size=(600, 400))
        popup.open()

    def file_selected(self, filechooser):
        selected_path = filechooser.selection and filechooser.selection[0]  # 선택된 경로 가져오기
        if selected_path:
            print(f"선택된 경로: {selected_path}")
            # 여기에 선택된 경로를 저장하거나 스크린샷을 해당 경로에 저장하는 코드를 추가하세요.
        else:
            print("디렉토리를 선택하지 않았습니다.")

# Kivy 애플리케이션 실행
if __name__ == "__main__":
    EmotionRecognitionApp().run()

import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import img_to_array
import time
import tkinter as tk
from tkinter import ttk
from tkinter import simpledialog
from tkinter import filedialog
import os
from PIL import Image

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

start_time = time.time()

# 각 감정에 대한 BGR 형식의 색상 정의
emotion_colors_bgr = {
    'neutral': (230, 216, 173),
    'happiness': (0, 165, 255),
    'surprise': (0, 252, 124),
    'sadness': (180, 130, 70),
    'anger': (0, 0, 255),
    'disgust': (19, 69, 139),
    'fear': (128, 0, 128)
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

# Combobox를 사용하여 최소 인원 수와 선택한 감정을 사용자에게 묻습니다.
prompts = ["최소 몇 명 이상이", "다음과 같은 표정을 지었을 때"]
combo_values = [combo_values_min_people, combo_values_emotion]
results = get_multiple_inputs("화면 캡처 설정", prompts, combo_values)

if results:
    min_people = int(results[0])
    selected_emotion = results[1]
    print(f"최소 인원 수: {min_people}명")
    print(f"선택한 감정: {selected_emotion} 때")
else:
    print("사용자가 입력을 취소했습니다.")

# 감정 수치 기준 시간 초기화
time_threshold = 0

# 스크린샷 모드 및 저장된 스크린샷 플래그 초기화
screenshot_mode = False
screenshot_saved = False

while True:
    ret, img = cap.read()
    if not ret:
        break

    # 흑백 이미지로 변환
    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    # 얼굴 감지
    faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.1, 6, minSize=(150, 150))

    # 유효한 감정을 가진 사람 수를 추적하기 위한 카운트 초기화
    valid_people_count = 0

    for i, (x, y, w, h) in enumerate(faces_detected):
        roi_gray = gray_img[y:y + h, x:x + w]
        roi_gray = cv2.resize(roi_gray, (48, 48))
        img_pixels = img_to_array(roi_gray)
        img_pixels = np.expand_dims(img_pixels, axis=0)

        # 픽셀 값을 0과 1 사이로 정규화
        img_pixels /= 255.0

        predictions = model.predict(img_pixels)

        person_label = f'person {i + 1}'

        # 스크린샷 모드가 아닌 경우 감지된 얼굴 주위에 사각형 그리고 감정 값을 표시
        if not screenshot_mode:
            cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), thickness=2)

            # 각 프레임에 대한 감정 값을 계산
            emotion_values = [predictions[0][j] * 100 for j in range(len(emotions))]
            
            # 사각형 프레임 하단에 몇 번 인물인지 표시
            cv2.putText(img, person_label, (x, y + h + 25), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)

            # 값을 내림차순으로 정렬하여 감정을 그림
            sorted_emotions = [x for _, x in sorted(zip(emotion_values, emotions), reverse=True)]
            sorted_values = sorted(emotion_values, reverse=True)

            # 값을 백분율로 표시하여 얼굴 주위에 감정 값을 그림
            for j, emotion in enumerate(sorted_emotions):
                text = f'{emotion}: {sorted_values[j]:.2f}%'  # 백분율로 표시
                text_color = emotion_colors_bgr[emotion]
                cv2.putText(img, text, (x, y - 30 - j * 25), cv2.FONT_HERSHEY_SIMPLEX, 0.7, text_color, 2)

        # 해당 사람에 대한 선택한 감정 조건이 충족되는지 확인
        if selected_emotion == '웃을' and predictions[0][emotions.index('happiness')] >= 0.5:
            valid_people_count += 1
        elif selected_emotion == '놀랄' and predictions[0][emotions.index('surprise')] >= 0.5:
            valid_people_count += 1
        elif selected_emotion == '슬플' and predictions[0][emotions.index('sadness')] >= 0.3:
            valid_people_count += 1
        elif selected_emotion == '화날' and predictions[0][emotions.index('anger')] >= 0.1:
            valid_people_count += 1
        elif selected_emotion == '역겨울' and predictions[0][emotions.index('disgust')] >= 0.05:
            valid_people_count += 1
        elif selected_emotion == '무서울' and predictions[0][emotions.index('fear')] >= 0.05:
            valid_people_count += 1

        # 유효한 감정을 가진 사람 수가 최소 필요 인원 이상이면 스크린샷 모드로 진입
        if valid_people_count >= min_people:
            if time_threshold == 0:
                time_threshold = time.time() + 1  # 1초 후의 시간을 기준으로 설정
                screenshot_mode = True;
        else:
            time_threshold = 0 # 기준에 도달하지 않으면 기준 시간 초기화

        # 스크린샷 모드가 활성화되고 현재 시간이 기준 시간 이후라면 스크린샷 캡처
        if screenshot_mode and not screenshot_saved and time.time() >= time_threshold:
            save_dir = filedialog.askdirectory(title="스크린샷 저장 디렉토리 선택")
            if save_dir:
                frame_filename = f"감정카메라_캡처_{time.strftime('%Y%m%d%H%M%S')}.jpg"
                frame_save_path = os.path.join(save_dir, frame_filename)
                pil_image = Image.fromarray(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
                pil_image.save(frame_save_path)
                print(f"스크린샷이 {frame_save_path}에 저장되었습니다.")
                screenshot_saved = True

    # 스크린샷이 저장되었으면 루프를 종료하고 일반 모드로 돌아감
    if screenshot_saved:
        cv2.destroyAllWindows()
        break

    # 스크린샷 모드가 아닌 경우 프레임을 표시
    if not screenshot_mode:
        cv2.imshow('얼굴 감정 인식', img)

    # 'q' 키를 누르면 루프 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 비디오 캡처 해제 및 창 닫기
cap.release()

from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
import cv2
import numpy as np
import time
from keras.models import model_from_json
from keras.utils import img_to_array
from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.views import View
from PyQt5.QtWidgets import QApplication, QMainWindow, QComboBox, QLabel, QLineEdit, QFileDialog
from PyQt5.QtCore import QTimer
from PyQt5.QtGui import QImage, QPixmap
import os
import sys

# from FacialEmotion import consumers

# 프로젝트 경로 추가
sys.path.append("/path/to/EmotionDetectionUI")

# 감정 한글을 영문으로 변환하는 딕셔너리 정의
emotion_translation = {
    '웃을': 'happiness',
    '놀랄': 'surprise',
    '슬플': 'sadness',
    '화날': 'anger',
    '역겨울': 'disgust',
    '무서울': 'fear'
}

# 감정 목록 정의
emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']

class EmotionDetectionView(View):
    def get(self, request):
        print("get() 메서드에 들어왔습니다.")
        html_content = render(request, 'emotion_detection.html')
        print("템플릿을 렌더링했습니다.")
        return html_content
    
    def post(self, request):
        print("post() 메서드에 들어왔습니다.")
        data = request.POST
        selected_emotion = data.get('selected_emotion')
        min_people = int(data.get('min_people'))
        emotion_threshold = float(data.get('emotion_threshold'))

        # 필요한 작업을 여기서 수행하고 결과 반환
        result = {
            'selected_emotion': selected_emotion,
            'min_people': min_people,
            'emotion_threshold': emotion_threshold,
            'message': 'POST 요청을 처리했습니다.'
        }
        print(result)

        print("JsonResponse를 반환합니다.")
        
        # 웹소켓으로 데이터 전송
        
        channel_layer = get_channel_layer()
        async_to_sync(channel_layer.group_send)('emotion_group', {
            'type': 'update_emotion',
            'selected_emotion': selected_emotion,
            'min_people': min_people,
            'emotion_threshold': emotion_threshold,
        })
        print("'emotion_group' 채널 그룹에 메세지를 성공적으로 전송했습니다.")

        return JsonResponse(result)

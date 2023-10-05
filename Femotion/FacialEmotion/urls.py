# Femotion/FacialEmotion/urls.py

from django.urls import path
from django.conf import settings
from django.conf.urls.static import static
from . import views
from .views import EmotionDetectionView

urlpatterns = [
    # 다른 URL 패턴들...
    path('', EmotionDetectionView.as_view(), name='emotion_detection'),  # 루트 URL
]

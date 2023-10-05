# Femotion/urls.py

from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from FacialEmotion.views import EmotionDetectionView

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('FacialEmotion.urls')),  # FacialEmotion 앱의 URL 패턴 포함
]

# 개발 환경에서 미디어 파일을 서빙하기 위한 설정
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

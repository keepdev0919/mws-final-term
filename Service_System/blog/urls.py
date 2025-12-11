from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views

# REST API 라우터 설정
router = DefaultRouter()
router.register(r'logs', views.AccessLogViewSet, basename='accesslog')

urlpatterns = [
    path('api/', include(router.urls)),  # /api/logs/ 엔드포인트
    path('blog/', views.post_list, name='post_list'),  # 웹 브라우저용 페이지
]

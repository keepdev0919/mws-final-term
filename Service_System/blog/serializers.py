from rest_framework import serializers
from .models import AccessLog

class AccessLogSerializer(serializers.ModelSerializer):
    """
    AccessLog 모델을 위한 시리얼라이저
    이미지 URL을 포함하여 JSON 응답 생성
    """
    image = serializers.ImageField(use_url=True, required=False)

    class Meta:
        model = AccessLog
        fields = ['id', 'image', 'created_at', 'log_type', 'description']

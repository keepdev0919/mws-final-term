from django.db import models
from django.utils import timezone
from django.conf import settings
from django.core.mail import send_mail

# 로그 타입 선택지 정의
LOG_TYPE_CHOICES = [
    ('VISITOR', '방문자'),
    ('PACKAGE', '택배'),
]

class AccessLog(models.Model):
    """
    1인 가구를 위한 방문자 및 택배 로그 모델
    YOLO가 감지한 객체를 분류하여 저장
    """
    image = models.ImageField(upload_to='access_log/%Y/%m/%d', blank=True, null=True)
    created_at = models.DateTimeField(default=timezone.now, verbose_name='감지 시간')
    log_type = models.CharField(
        max_length=10, 
        choices=LOG_TYPE_CHOICES, 
        verbose_name='로그 타입',
        help_text='VISITOR: 방문자, PACKAGE: 택배'
    )
    description = models.TextField(
        verbose_name='설명',
        help_text='YOLO가 감지한 원본 객체명 (예: person, suitcase, backpack)'
    )

    def save(self, *args, **kwargs):
        """로그 저장 시 이메일 알림 전송 (새로 생성되는 경우에만)"""
        is_new = self.pk is None
        super().save(*args, **kwargs)

        if is_new and settings.INTRUDER_ALERT_ENABLED:
            self.send_alert()

    def send_alert(self):
        """방문자/택배 감지 시 이메일 알림 전송"""
        try:
            type_name = '방문자' if self.log_type == 'VISITOR' else '택배'
            subject = f'🚨 {type_name} 감지 알림'
            message = f'''
1인 가구 보안 시스템에서 새로운 {type_name}이 감지되었습니다.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 감지 정보
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔍 감지 타입: {type_name}
📝 감지된 객체: {self.description}
⏰ 감지 시각: {self.created_at.strftime('%Y년 %m월 %d일 %H:%M:%S')}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📸 캡처된 이미지를 확인하려면 앱을 확인하세요.
🔗 http://127.0.0.1:8000/

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🤖 1인 가구 보안 시스템 (Django + YOLOv5)
            '''

            from_email = settings.EMAIL_HOST_USER if hasattr(settings, 'EMAIL_HOST_USER') else 'noreply@security-system.com'
            recipient_list = [settings.INTRUDER_ALERT_EMAIL]

            send_mail(
                subject,
                message,
                from_email,
                recipient_list,
                fail_silently=False,
            )

            print(f'✅ [이메일 알림] {type_name} 감지 알림 전송 완료 → {settings.INTRUDER_ALERT_EMAIL}')

        except Exception as e:
            print(f'❌ [이메일 알림 오류] {str(e)}')

    def __str__(self):
        return f'{self.get_log_type_display()} - {self.description} ({self.created_at.strftime("%Y-%m-%d %H:%M")})'

    class Meta:
        ordering = ['-created_at']
        verbose_name = '접근 로그'
        verbose_name_plural = '접근 로그'

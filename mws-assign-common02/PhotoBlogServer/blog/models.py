from django.db import models
from django.utils import timezone
from django.conf import settings
from django.core.mail import send_mail

class Post(models.Model):
    title = models.CharField(max_length=200)
    text = models.TextField()
    created_date = models.DateTimeField(default=timezone.now)
    published_date = models.DateTimeField(blank=True, null=True)
    image = models.ImageField(upload_to='intruder_image/%Y/%m/%d', blank=True, null=True)

    def publish(self):
        self.published_date = timezone.now()
        self.save()

    def save(self, *args, **kwargs):
        # 새로 생성되는 경우에만 이메일 전송 (업데이트 시에는 전송 안 함)
        is_new = self.pk is None
        super().save(*args, **kwargs)

        if is_new and settings.INTRUDER_ALERT_ENABLED:
            self.send_intruder_alert()

    def send_intruder_alert(self):
        """침입자 감지 시 이메일 알림 전송"""
        try:
            subject = f'🚨 침입자 감지 알림: {self.title}'
            message = f'''
침입자 감지 시스템에서 새로운 침입이 감지되었습니다.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 침입 정보
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔍 감지된 객체: {self.title}
📝 상세 정보: {self.text}
⏰ 감지 시각: {self.created_date.strftime('%Y년 %m월 %d일 %H:%M:%S')}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📸 캡처된 이미지를 확인하려면 웹 블로그를 방문하세요.
🔗 http://127.0.0.1:8000/

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🤖 침입자 감지 시스템 (Django + YOLOv5)
            '''

            from_email = settings.EMAIL_HOST_USER if hasattr(settings, 'EMAIL_HOST_USER') else 'noreply@intruder-detection.com'
            recipient_list = [settings.INTRUDER_ALERT_EMAIL]

            send_mail(
                subject,
                message,
                from_email,
                recipient_list,
                fail_silently=False,
            )

            print(f'✅ [이메일 알림] {self.title} 침입 알림 전송 완료 → {settings.INTRUDER_ALERT_EMAIL}')

        except Exception as e:
            print(f'❌ [이메일 알림 오류] {str(e)}')

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-published_date']

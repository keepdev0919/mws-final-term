from django.shortcuts import render
from django.conf import settings
from django.core.mail import send_mail
from rest_framework import viewsets, filters
from rest_framework.permissions import IsAuthenticated
from .models import Post
from .serializers import PostSerializer

class PostViewSet(viewsets.ModelViewSet):
    queryset = Post.objects.all()
    serializer_class = PostSerializer
    filter_backends = [filters.SearchFilter, filters.OrderingFilter]
    search_fields = ['title', 'text']
    ordering_fields = ['created_date', 'published_date']
    ordering = ['-published_date']

    def perform_create(self, serializer):
        # 게시물 저장
        post = serializer.save()

        # 이메일 알림 전송
        if settings.INTRUDER_ALERT_ENABLED:
            self.send_intruder_alert(post)

    def send_intruder_alert(self, post):
        """침입자 감지 시 이메일 알림 전송"""
        try:
            subject = f'🚨 침입자 감지 알림: {post.title}'
            message = f'''
침입자 감지 시스템에서 새로운 침입이 감지되었습니다.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 침입 정보
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔍 감지된 객체: {post.title}
📝 상세 정보: {post.text}
⏰ 감지 시각: {post.created_date.strftime('%Y년 %m월 %d일 %H:%M:%S')}

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

            print(f'✅ [이메일 알림] {post.title} 침입 알림 전송 완료 → {settings.INTRUDER_ALERT_EMAIL}')

        except Exception as e:
            print(f'❌ [이메일 알림 오류] {str(e)}')

def post_list(request):
    posts = Post.objects.order_by('-published_date')
    return render(request, 'blog/post_list.html', {'posts': posts})

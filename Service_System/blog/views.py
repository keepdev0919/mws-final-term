from django.shortcuts import render
from django.conf import settings
from rest_framework import viewsets, filters
from rest_framework.permissions import IsAuthenticated
from rest_framework.authentication import TokenAuthentication
from .models import AccessLog
from .serializers import AccessLogSerializer

class AccessLogViewSet(viewsets.ModelViewSet):
    """
    AccessLog 모델에 대한 CRUD 작업을 처리하는 ViewSet
    type 쿼리 파라미터를 통한 필터링 지원
    """
    queryset = AccessLog.objects.all()
    serializer_class = AccessLogSerializer
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]
    filter_backends = [filters.OrderingFilter]
    ordering_fields = ['created_at']
    ordering = ['-created_at']

    def get_queryset(self):
        """
        쿼리 파라미터 'type'을 받아 로그 타입별 필터링
        ?type=VISITOR: 방문자 기록만 조회
        ?type=PACKAGE: 택배 기록만 조회
        """
        queryset = AccessLog.objects.all()
        log_type = self.request.query_params.get('type', None)
        
        if log_type:
            # VISITOR 또는 PACKAGE로 필터링
            if log_type.upper() in ['VISITOR', 'PACKAGE']:
                queryset = queryset.filter(log_type=log_type.upper())
        
        return queryset

def post_list(request):
    """웹 브라우저용 로그 목록 페이지 (기존 호환성 유지)"""
    logs = AccessLog.objects.order_by('-created_at')
    return render(request, 'blog/post_list.html', {'posts': logs})

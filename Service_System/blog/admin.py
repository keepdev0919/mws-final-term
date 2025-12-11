from django.contrib import admin
from .models import AccessLog

@admin.register(AccessLog)
class AccessLogAdmin(admin.ModelAdmin):
    """AccessLog 모델을 Django Admin에서 관리"""
    list_display = ['id', 'log_type', 'description', 'created_at']
    list_filter = ['log_type', 'created_at']
    search_fields = ['description', 'log_type']
    date_hierarchy = 'created_at'
    ordering = ['-created_at']
    readonly_fields = ['created_at']

from django.contrib import admin
from .models import Post

@admin.register(Post)
class PostAdmin(admin.ModelAdmin):
    list_display = ['title', 'created_date', 'published_date']
    list_filter = ['created_date', 'published_date']
    search_fields = ['title', 'text']
    date_hierarchy = 'published_date'
    ordering = ['-published_date']

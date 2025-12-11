from rest_framework import serializers
from .models import Post

class PostSerializer(serializers.ModelSerializer):
    image = serializers.ImageField(use_url=True, required=False)

    class Meta:
        model = Post
        fields = ['id', 'title', 'text', 'created_date', 'published_date', 'image']

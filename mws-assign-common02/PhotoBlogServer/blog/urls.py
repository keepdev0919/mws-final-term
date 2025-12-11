from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views

router = DefaultRouter()
router.register(r'Post', views.PostViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('blog/', views.post_list, name='post_list'),
]

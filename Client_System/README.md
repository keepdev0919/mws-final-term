# PhotoViewer - Android Client

침입자 감지 시스템의 Android 클라이언트 애플리케이션입니다.

## 기능
- Django 서버로부터 침입자 감지 기록 조회
- RecyclerView를 이용한 목록 표시
- Glide를 이용한 이미지 로딩

## 사용 방법

### 1. Android Studio에서 프로젝트 열기
- Android Studio 실행
- File → Open
- PhotoViewer 폴더 선택

### 2. Django 서버 설정
Django 서버의 REST API 인증을 임시로 비활성화해야 합니다.

`PhotoBlogServer/intruder_detection/settings.py`:
```python
REST_FRAMEWORK = {
    'DEFAULT_PERMISSION_CLASSES': (
        'rest_framework.permissions.AllowAny',  # 임시로 변경
    ),
    ...
}
```

`PhotoBlogServer/intruder_detection/settings.py`:
```python
ALLOWED_HOSTS = ['*']  # 또는 특정 IP
```

### 3. 네트워크 설정

#### 에뮬레이터 사용 시:
`ApiClient.java`에서 `BASE_URL = "http://10.0.2.2:8000/";` 사용 (기본값)

#### 실제 기기 사용 시:
1. 컴퓨터와 스마트폰을 같은 Wi-Fi에 연결
2. 컴퓨터 IP 확인: `ifconfig` 또는 `ipconfig`
3. `ApiClient.java`에서 `BASE_URL = "http://[컴퓨터IP]:8000/";`로 변경
4. Django 서버 실행: `python manage.py runserver 0.0.0.0:8000`

### 4. 앱 실행
- Android Studio에서 Run 버튼 클릭
- 에뮬레이터 또는 실제 기기에서 실행

## 주요 파일
- `MainActivity.java`: 메인 액티비티
- `ApiClient.java`: Retrofit 클라이언트 설정
- `ApiService.java`: API 인터페이스 정의
- `Post.java`: 데이터 모델
- `PostAdapter.java`: RecyclerView 어댑터

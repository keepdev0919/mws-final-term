# 1인 가구를 위한 AI 기반 비대면 택배 및 방문자 로그 시스템

## 프로젝트 개요

1인 가구의 보안을 위해 현관 앞의 영상을 실시간으로 분석하고, 방문자(사람)와 택배(물건)를 구분하여 감지한 뒤, 해당 로그와 사진을 스마트폰 앱에서 확인하는 시스템입니다.

## 프로젝트 구조

```
학기말 프로젝트/
├── Edge_System/          # YOLOv5 기반 객체 감지 시스템
│   ├── detect.py         # 메인 실행 파일
│   ├── changedetection.py # 변화 감지 모듈
│   ├── requirements.txt  # Python 패키지 의존성
│   └── README.md         # Edge System 사용 가이드
│
├── Service_System/       # Django REST API 서버
│   ├── blog/            # API 앱
│   │   ├── models.py    # AccessLog 모델
│   │   ├── views.py     # ViewSet (필터링 로직 포함)
│   │   ├── serializers.py
│   │   └── urls.py      # /api/logs/ 엔드포인트
│   ├── intruder_detection/ # Django 프로젝트 설정
│   ├── manage.py
│   └── requirements.txt (생성 필요)
│
├── Client_System/        # Android Native App
│   └── app/             # Android Studio 프로젝트
│       └── src/main/java/com/example/photoviewer/
│           ├── MainActivity.java  # TabLayout 포함
│           ├── ApiService.java   # 필터링 파라미터 지원
│           └── Post.java         # AccessLog 모델
│
├── url.txt              # 서비스 URL, git 주소
└── README.md            # 이 파일
```

## 핵심 기능

### 1. 객체 분류 (Object Classification)
- `person` → `VISITOR` (방문자)
- `suitcase`, `backpack`, `handbag` → `PACKAGE` (택배)

### 2. 변화 감지 (Change Detection)
- 동일한 객체가 계속 서 있을 때는 무시
- 새로운 객체가 진입했을 때만 1회 서버로 전송

## 설치 및 실행 방법

### Step 1: Service System (Django 서버) 설정

```bash
cd Service_System

# 가상환경 생성 및 활성화
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 의존성 설치
pip install Django==4.2.7 djangorestframework==3.14.0 Pillow==10.1.0 django-cors-headers==4.3.1 rest-framework-simplejwt

# 데이터베이스 마이그레이션
python manage.py makemigrations
python manage.py migrate

# 관리자 계정 생성
python manage.py createsuperuser

# Token 생성 (Edge System에서 사용)
python manage.py shell
>>> from rest_framework.authtoken.models import Token
>>> from django.contrib.auth.models import User
>>> user = User.objects.first()
>>> token = Token.objects.get_or_create(user=user)[0]
>>> print(token.key)  # 이 값을 Edge_System/detect.py의 TOKEN에 설정

# 서버 실행
python manage.py runserver
```

### Step 2: Edge System (YOLO 감지) 설정

```bash
cd Edge_System

# 가상환경 생성 및 활성화
python -m venv venv
source venv/bin/activate

# 의존성 설치
pip install -r requirements.txt

# detect.py에서 TOKEN 설정
# TOKEN = 'YOUR_TOKEN_HERE'  # Step 1에서 생성한 Token으로 변경

# 실행
python detect.py
```

### Step 3: Client System (Android 앱) 설정

1. Android Studio에서 `Client_System` 폴더를 프로젝트로 열기
2. `ApiClient.java`에서 서버 URL 확인 (에뮬레이터: `http://10.0.2.2:8000/`)
3. 빌드 및 실행

## API 명세

### 1. 로그 업로드 (Edge -> Server)
- **Method:** `POST`
- **URL:** `/api/logs/`
- **Header:** `Authorization: Token <USER_TOKEN>`
- **Body (Multipart/Form-data):**
  - `image`: (파일 바이너리)
  - `log_type`: "VISITOR" or "PACKAGE"
  - `description`: "person detected"

### 2. 로그 목록 조회 (Client -> Server)
- **Method:** `GET`
- **URL:** `/api/logs/`
- **Query Params:**
  - `?type=VISITOR`: 방문자 기록만 조회
  - `?type=PACKAGE`: 택배 기록만 조회
  - 없음: 전체 조회
- **Response (JSON):**
```json
[
  {
    "id": 1,
    "image": "http://server/media/access_log/2025/12/11/img_01.jpg",
    "created_at": "2025-12-11T14:00:00",
    "log_type": "VISITOR",
    "description": "person"
  }
]
```

## 주요 수정 사항

### Service System
- ✅ 모델: `Post` → `AccessLog` (log_type, description 필드 추가)
- ✅ API 엔드포인트: `/api/logs/`
- ✅ 필터링: `?type=VISITOR` 또는 `?type=PACKAGE` 지원
- ✅ 인증: Token 인증 추가

### Edge System
- ✅ YOLOv5 기반 객체 감지
- ✅ 변화 감지 모듈 (`changedetection.py`)
- ✅ 객체 분류 로직 (person → VISITOR, suitcase/backpack → PACKAGE)
- ✅ Django 서버로 POST 전송

### Client System
- ✅ TabLayout 추가 (전체, 방문자, 택배)
- ✅ API 필터링 파라미터 연동
- ✅ 모델 필드 수정 (log_type, description, created_at)

## 개발 환경

- **Python:** 3.9+
- **Django:** 4.2.7
- **YOLOv5:** ultralytics
- **Android:** API 24+ (Android 7.0+)
- **Database:** SQLite (개발용)

## 참고사항

- Edge System 실행 전에 Django 서버가 실행 중이어야 합니다
- Token 인증을 위해 Django에서 Token을 먼저 생성해야 합니다
- Android 앱은 에뮬레이터 사용 시 `10.0.2.2`를 사용하고, 실제 기기 사용 시 컴퓨터의 IP 주소를 사용해야 합니다


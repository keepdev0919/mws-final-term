# Edge System - YOLOv5 기반 객체 감지 시스템

## 개요
USB 웹캠으로부터 실시간 영상을 받아 YOLOv5로 객체를 감지하고, 방문자와 택배를 구분하여 Django 서버로 전송하는 시스템입니다.

## 주요 기능
- **실시간 객체 감지**: YOLOv5를 사용한 실시간 영상 분석
- **객체 분류**: 
  - `person` → `VISITOR` (방문자)
  - `suitcase`, `backpack`, `handbag` → `PACKAGE` (택배)
- **변화 감지**: 동일한 객체가 계속 있을 때는 무시하고, 새로운 객체 진입 시에만 서버로 전송
- **서버 연동**: Django REST API로 이미지와 메타데이터 전송

## 설치 방법

```bash
# 가상환경 생성 (권장)
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 의존성 설치
pip install -r requirements.txt
```

## 사용 방법

1. **Django 서버 실행 확인**
   - Service_System에서 Django 서버가 실행 중이어야 합니다
   - `http://127.0.0.1:8000`에서 접근 가능해야 합니다

2. **Token 설정**
   - `detect.py` 파일의 `TOKEN` 변수를 Django에서 생성한 Token으로 변경
   - Token 생성 방법:
     ```bash
     cd Service_System
     python manage.py shell
     >>> from rest_framework.authtoken.models import Token
     >>> from django.contrib.auth.models import User
     >>> user = User.objects.first()  # 또는 특정 사용자
     >>> token = Token.objects.get_or_create(user=user)[0]
     >>> print(token.key)
     ```

3. **프로그램 실행**
   ```bash
   python detect.py
   ```

4. **종료**
   - `q` 키를 누르거나 `Ctrl+C`로 종료

## 파일 구조
- `detect.py`: 메인 실행 파일 (YOLO 감지 및 서버 전송)
- `changedetection.py`: 변화 감지 모듈
- `requirements.txt`: Python 패키지 의존성
- `captured_images/`: 캡처된 이미지 저장 디렉토리 (자동 생성)

## 설정 변경
- `DJANGO_SERVER_URL`: Django 서버 URL (기본: http://127.0.0.1:8000)
- `detection_interval`: 감지 간격 (프레임 수, 기본: 10)
- `OBJECT_MAPPING`: 객체 분류 매핑 딕셔너리


# Django 서버 설정 가이드

## 현재 설정 상태

✅ **마이그레이션 완료**: AccessLog 모델이 데이터베이스에 생성됨
✅ **서버 실행 중**: http://127.0.0.1:8000
✅ **Token 생성 완료**: Edge System에서 사용할 Token이 생성됨

## 생성된 계정 정보

- **Username**: `admin`
- **Password**: `admin123`
- **Token**: `3aaa6f4666681d72f9aeb065a6074b9c3c1613e1`

## API 엔드포인트

### 1. 로그 목록 조회
```bash
# 전체 로그 조회
curl http://127.0.0.1:8000/api/logs/ \
  -H "Authorization: Token 3aaa6f4666681d72f9aeb065a6074b9c3c1613e1"

# 방문자만 조회
curl "http://127.0.0.1:8000/api/logs/?type=VISITOR" \
  -H "Authorization: Token 3aaa6f4666681d72f9aeb065a6074b9c3c1613e1"

# 택배만 조회
curl "http://127.0.0.1:8000/api/logs/?type=PACKAGE" \
  -H "Authorization: Token 3aaa6f4666681d72f9aeb065a6074b9c3c1613e1"
```

### 2. 로그 업로드 (Edge System에서 사용)
```bash
curl -X POST http://127.0.0.1:8000/api/logs/ \
  -H "Authorization: Token 3aaa6f4666681d72f9aeb065a6074b9c3c1613e1" \
  -F "image=@/path/to/image.jpg" \
  -F "log_type=VISITOR" \
  -F "description=person"
```

## 서버 관리

### 서버 시작
```bash
cd Service_System
source venv/bin/activate
python manage.py runserver
```

### 서버 중지
- 터미널에서 `Ctrl+C` 또는 프로세스 종료

### 관리자 페이지
- URL: http://127.0.0.1:8000/admin/
- Username: `admin`
- Password: `admin123`

## 추가 Token 생성

새로운 사용자에 대한 Token을 생성하려면:

```bash
cd Service_System
source venv/bin/activate
python manage.py shell
```

Python shell에서:
```python
from django.contrib.auth.models import User
from rest_framework.authtoken.models import Token

# 사용자 생성 또는 가져오기
user, created = User.objects.get_or_create(username='your_username')
if created:
    user.set_password('your_password')
    user.save()

# Token 생성
token, _ = Token.objects.get_or_create(user=user)
print(f'Token: {token.key}')
```

## 문제 해결

### 서버가 실행되지 않는 경우
1. 가상환경이 활성화되어 있는지 확인
2. 포트 8000이 이미 사용 중인지 확인
3. `python manage.py check`로 설정 확인

### Token 인증 실패
1. Token이 올바른지 확인
2. `Authorization: Token <token>` 형식 확인
3. 사용자 계정이 활성화되어 있는지 확인


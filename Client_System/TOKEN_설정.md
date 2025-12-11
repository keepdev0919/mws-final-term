# Android 앱 Token 인증 설정

## 완료된 작업 ✅

1. **OkHttp 의존성 추가**: `build.gradle`에 `okhttp3` 추가
2. **Token 인증 Interceptor 추가**: `ApiClient.java`에 자동 Token 헤더 추가
3. **Token 값 설정**: Django 서버에서 생성한 Token 사용

## Token 정보

- **Token 값**: `3aaa6f4666681d72f9aeb065a6074b9c3c1613e1`
- **설정 위치**: `ApiClient.java`의 `TOKEN` 상수
- **자동 적용**: 모든 API 요청에 자동으로 `Authorization: Token <TOKEN>` 헤더 추가

## 동작 방식

1. Android 앱이 API를 호출할 때
2. OkHttp Interceptor가 자동으로 요청을 가로채서
3. `Authorization: Token 3aaa6f4666681d72f9aeb065a6074b9c3c1613e1` 헤더를 추가
4. Django 서버가 Token을 확인하고 인증 처리

## 다음 단계

1. **Android Studio에서 Gradle Sync**
   - 상단 메뉴: File → Sync Project with Gradle Files
   - 또는 오른쪽 상단의 "Sync Now" 클릭

2. **앱 재빌드 및 실행**
   - 이제 401 오류 없이 정상 작동해야 합니다

3. **Token 변경이 필요한 경우**
   - `ApiClient.java`의 `TOKEN` 상수 값을 변경
   - 앱 재빌드 필요

## 표 요구사항 준수

✅ **2-1. 사용자 보안 기능(보안키를 이용한 로그인, 공통)**
- Edge System: Token 사용 중 ✅
- Client System: Token 사용 중 ✅ (방금 추가됨)


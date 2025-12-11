# 🚀 Django와 YOLO를 이용한 침입자 감시 시스템



본 프로젝트는 경희대학교 모바일/웹서비스 프로젝트(Mobile/WebService Project)의 일환으로 **YOLOv5**의 실시간 객체 탐지 기능과 **Django** 웹 프레임워크를 연동하여 침입자를 감지하고 기록하는 시스템입니다.

---

## 1. 📖 프로젝트 개요

본 시스템은 카메라(USB 웹캠 등)로부터 실시간 영상 입력을 받아, **YOLOv5가 객체를 탐지**합니다.

이 과정에서 `changedetection.py` 로직을 통해 **이전에는 없던 새로운 객체(침입자)가 감지**되면, 해당 순간의 프레임을 캡처합니다.

캡처된 이미지는 **Django REST framework** 로 구축된 API 서버로 즉시 전송되어 데이터베이스(SQLite)에 기록됩니다. 사용자는 **Django 기반의 웹 블로그** 나 **안드로이드 앱** 을 통해 이 침입 기록을 언제든지 확인할 수 있습니다.

## 2. 🌟 주요 기능

* **실시간 객체 탐지:** `YOLOv5`및 `OpenCV`를 이용한 실시간 영상(USB 웹캠) 분석
* **침입자(변화) 감지:** 단순 탐지가 아닌, 이전에 없던 객체가 새로 출현하는 '변화'를 감지
* **이미지 및 로그 전송:** 침입 감지 시, `requests`를 이용해 Django 서버의 REST API로 캡처된 이미지와 탐지 로그를 전송.
* **웹 기반 로그 확인:** Django 서버에 구축된 '이미지 블로그'를 통해 캡처된 침입자 이미지 목록을 확인.
* **모바일 연동:** 안드로이드 클라이언트를 통해 서버의 데이터를 모바일 환경에서 확인.

## 3. 🏗️ 시스템 아키텍처

본 프로젝트는 PDF의 시스템 구성도를 기반으로 3개의 주요 시스템으로 구성됩니다.



1.  **Edge System (YOLOv5 Client):**
    * **역할:** 영상 입력 및 AI 분석.
    * **프로세스:** USB 웹캠 또는 RTSP 로부터 영상 스트림을 받습니다. `YOLOv5` 가 실시간으로 객체를 분석합니다.
    * `changedetection.py` 스크립트가 객체 변화를 감지하면, `REST API` 를 호출하여 `Service System`에 데이터를 전송합니다.

2.  **Service System (Django Server)**
    * **역할:** 데이터 수신, 저장 및 제공.
    * **프로세스:** `Django REST framework` 로 구현된 API가 Edge System의 요청을 받습니다.
    * 전송받은 이미지와 텍스트(탐지된 객체명)를 `SQLite` 데이터베이스에 저장합니다.
    * 저장된 데이터를 'Image blog' 형태의 웹페이지로 제공합니다.

3.  **Client (Web/Android)**
    * **역할:** 사용자 인터페이스.
    * **프로세스:** 웹 브라우저나 안드로이드 앱을 통해 `Service System`에 접속하여 저장된 침입 기록을 조회합니다.

## 4. 🛠️ 기술 스택

* **Backend:** `Python`, `Django`, `Django REST framework`
* **Database:** `SQLite`
* **AI / Edge:** `YOLOv5`, `PyTorch`, `OpenCV`
* **Frontend (Mobile):** `Android` 
* **Deployment:** `PythonAnywhere`

## 5. ⚙️ 설치 및 실행 방법

본 프로젝트는 3개의 파트로 나누어져 있습니다.

### 1. PhotoBlogServer (Django 서버)

```bash
# 1. 서버 폴더로 이동
cd PhotoBlogServer

# 2. (가상환경 생성 및 활성화 - 권장)
# python -m venv venv
# source venv/bin/activate

# 3. 의존성 설치
pip install -r requirements.txt

# 4. 데이터베이스 마이그레이션
python manage.py migrate

# 5. 관리자 계정 생성 (옵션)
python manage.py createsuperuser

# 6. 서버 실행 (기본 포트 8000)
python manage.py runserver
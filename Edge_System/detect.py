"""
YOLOv5 기반 객체 감지 및 Django 서버 전송 스크립트
1인 가구를 위한 방문자 및 택배 감지 시스템
"""

import cv2
import requests
import os
import time
from pathlib import Path
from datetime import datetime
from ultralytics import YOLO
from changedetection import ChangeDetector

# YOLOv5 모델 경로
YOLO_MODEL_PATH = 'yolov5s.pt'  # 또는 yolov5m.pt, yolov5l.pt, yolov5x.pt

# Django 서버 설정
DJANGO_SERVER_URL = 'http://127.0.0.1:8000'
API_ENDPOINT = f'{DJANGO_SERVER_URL}/api/logs/'
TOKEN = '3aaa6f4666681d72f9aeb065a6074b9c3c1613e1'  # Django에서 생성한 Token

# 객체 분류 매핑
OBJECT_MAPPING = {
    'person': 'VISITOR',
    'suitcase': 'PACKAGE',
    'backpack': 'PACKAGE',
    'handbag': 'PACKAGE',
}

def classify_object(yolo_label):
    """
    YOLO가 감지한 객체명을 로그 타입으로 분류
    
    Args:
        yolo_label: YOLO가 감지한 객체명 (예: 'person', 'suitcase')
        
    Returns:
        tuple: (log_type, description) 또는 (None, None) - 무시할 객체인 경우
    """
    yolo_label_lower = yolo_label.lower()
    
    if yolo_label_lower in OBJECT_MAPPING:
        log_type = OBJECT_MAPPING[yolo_label_lower]
        return log_type, yolo_label_lower
    
    # 매핑되지 않은 객체는 무시
    return None, None

def send_to_server(image_path, log_type, description):
    """
    감지된 이미지와 메타데이터를 Django 서버로 전송
    
    Args:
        image_path: 캡처된 이미지 파일 경로
        log_type: 로그 타입 ('VISITOR' 또는 'PACKAGE')
        description: YOLO가 감지한 원본 객체명
    """
    try:
        with open(image_path, 'rb') as img_file:
            files = {'image': img_file}
            data = {
                'log_type': log_type,
                'description': description
            }
            headers = {
                'Authorization': f'Token {TOKEN}'
            }
            
            response = requests.post(API_ENDPOINT, files=files, data=data, headers=headers)
            
            if response.status_code == 201:
                print(f'✅ [전송 성공] {log_type} - {description} 전송 완료')
            else:
                print(f'❌ [전송 실패] 상태 코드: {response.status_code}, 응답: {response.text}')
                
    except Exception as e:
        print(f'❌ [전송 오류] {str(e)}')

def main():
    """
    메인 함수: USB 웹캠에서 영상을 받아 실시간으로 객체를 감지하고 서버로 전송
    """
    # YOLOv5 모델 로드 (ultralytics 사용)
    try:
        # ultralytics의 YOLO 클래스를 사용하여 모델 로드
        # yolov5su.pt는 ultralytics가 권장하는 개선된 모델
        model = YOLO('yolov5su.pt')  # 첫 실행 시 자동으로 다운로드됨
        print('✅ YOLOv5 모델 로드 완료')
    except Exception as e:
        print(f'❌ YOLOv5 모델 로드 실패: {str(e)}')
        print('💡 ultralytics 패키지 확인: pip install ultralytics')
        return
    
    # 노트북 웹캠 초기화 (카메라 1 사용)
    cap = cv2.VideoCapture(1)
    
    if not cap.isOpened():
        print('❌ 노트북 웹캠(카메라 1)을 열 수 없습니다')
        return
    
    print('✅ 노트북 웹캠 연결 완료')
    
    # 변화 감지기 초기화
    change_detector = ChangeDetector()
    
    # 이미지 저장 디렉토리 생성
    save_dir = Path('captured_images')
    save_dir.mkdir(exist_ok=True)
    
    frame_count = 0
    detection_interval = 10  # N 프레임마다 감지 (성능 최적화)
    last_results = None  # 이전 감지 결과 저장 (캡처를 위해 계속 표시)
    last_frame = None  # 이전 프레임 저장
    
    print('🚀 실시간 객체 감지 시작... (종료: q 키)')
    
    try:
        while True:
            ret, frame = cap.read()
            if not ret:
                break
            
            frame_count += 1
            
            # 일정 간격으로만 YOLO 감지 수행 (성능 최적화)
            if frame_count % detection_interval == 0:
                # YOLO로 객체 감지 (ultralytics 방식)
                results = model(frame, verbose=False)
                last_results = results  # 결과 저장
                last_frame = frame.copy()  # 프레임 저장
                
                # 감지된 객체 추출
                detected_objects = set()
                detected_labels = []
                
                # ultralytics의 결과 형식에 맞게 처리
                for result in results:
                    for box in result.boxes:
                        cls = int(box.cls[0])
                        label = result.names[cls]
                        detected_objects.add(label)
                        detected_labels.append(label)
                
                # 객체 분류 및 변화 감지
                if detected_objects:
                    # 변화 감지 (새로운 객체 진입 확인)
                    if change_detector.detect_change(detected_objects):
                        # 감지된 객체 중에서 VISITOR 또는 PACKAGE로 분류 가능한 것만 처리
                        for label in detected_labels:
                            log_type, description = classify_object(label)
                            
                            if log_type:  # 분류 가능한 객체인 경우
                                # 이미지 저장
                                timestamp = datetime.now().strftime('%Y%m%d_%H%M%S_%f')
                                image_filename = f'{log_type}_{timestamp}.jpg'
                                image_path = save_dir / image_filename
                                
                                cv2.imwrite(str(image_path), frame)
                                print(f'📸 [이미지 저장] {image_path}')
                                
                                # 서버로 전송
                                send_to_server(str(image_path), log_type, description)
                                
                                # 한 프레임에서 하나의 객체만 처리 (중복 방지)
                                break
                
                # 화면에 결과 표시
                # ultralytics의 결과를 OpenCV 형식으로 변환
                annotated_frame = results[0].plot() if results else frame
                cv2.imshow('YOLO Detection', annotated_frame)
            
            else:
                # 감지하지 않는 프레임도 이전 결과를 계속 표시 (캡처를 위해)
                if last_results and last_frame is not None:
                    # 이전 프레임에 이전 결과를 오버레이하여 표시
                    annotated_frame = last_results[0].plot() if last_results else last_frame
                    cv2.imshow('YOLO Detection', annotated_frame)
                else:
                    # 아직 감지 결과가 없으면 원본 프레임 표시
                    cv2.imshow('YOLO Detection', frame)
            
            # 'q' 키를 누르면 종료
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
                
    except KeyboardInterrupt:
        print('\n⚠️ 사용자에 의해 중단됨')
    finally:
        cap.release()
        cv2.destroyAllWindows()
        print('✅ 프로그램 종료')

if __name__ == '__main__':
    main()


"""
변화 감지 모듈
동일한 객체가 계속 서 있을 때는 무시하고, 새로운 객체가 진입했을 때만 감지
"""

class ChangeDetector:
    """
    객체 변화 감지를 위한 클래스
    이전 프레임과 현재 프레임을 비교하여 새로운 객체 진입을 감지
    """
    
    def __init__(self):
        """변화 감지기 초기화"""
        self.previous_objects = set()  # 이전 프레임에서 감지된 객체 집합
        self.detection_threshold = 3  # 연속으로 감지되어야 하는 프레임 수
        
    def detect_change(self, current_objects):
        """
        현재 감지된 객체와 이전 객체를 비교하여 변화 감지
        
        Args:
            current_objects: 현재 프레임에서 감지된 객체의 집합 (set)
            
        Returns:
            bool: 새로운 객체가 진입했으면 True, 아니면 False
        """
        if not current_objects:
            # 현재 프레임에 객체가 없으면 이전 상태 초기화
            self.previous_objects = set()
            return False
        
        # 새로운 객체가 있는지 확인 (이전에 없던 객체)
        new_objects = current_objects - self.previous_objects
        
        if new_objects:
            # 새로운 객체가 감지됨
            self.previous_objects = current_objects.copy()
            return True
        else:
            # 동일한 객체가 계속 있음 (변화 없음)
            return False
    
    def reset(self):
        """감지 상태 초기화"""
        self.previous_objects = set()


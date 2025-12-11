package com.example.photoviewer;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Django REST API 서비스 인터페이스
 * 로그 목록 조회 및 필터링 기능 제공
 */
public interface ApiService {
    /**
     * 로그 목록 조회
     * @param type 필터링 타입 (VISITOR, PACKAGE) - null이면 전체 조회
     * @return 로그 목록
     */
    @GET("api/logs/")
    Call<List<Post>> getLogs(@Query("type") String type);
}

package com.example.photoviewer;

import com.google.gson.annotations.SerializedName;

/**
 * AccessLog 모델 클래스
 * Django 서버에서 받아온 방문자/택배 로그 데이터를 담는 클래스
 */
public class Post {
    @SerializedName("id")
    private int id;

    @SerializedName("image")
    private String image;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("log_type")
    private String logType;  // "VISITOR" or "PACKAGE"

    @SerializedName("description")
    private String description;  // YOLO가 감지한 원본 객체명

    // Getters
    public int getId() { return id; }
    public String getImage() { return image; }
    public String getCreatedAt() { return createdAt; }
    public String getLogType() { return logType; }
    public String getDescription() { return description; }
    
    /**
     * 로그 타입을 한글로 반환
     */
    public String getLogTypeDisplay() {
        if ("VISITOR".equals(logType)) {
            return "방문자";
        } else if ("PACKAGE".equals(logType)) {
            return "택배";
        }
        return logType;
    }
}

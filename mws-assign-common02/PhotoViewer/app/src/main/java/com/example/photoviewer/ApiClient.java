package com.example.photoviewer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // 에뮬레이터 사용 시: http://10.0.2.2:8000/
    // 실제 기기 사용 시: http://[컴퓨터IP]:8000/
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}

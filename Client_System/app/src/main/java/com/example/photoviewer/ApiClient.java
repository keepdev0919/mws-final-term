package com.example.photoviewer;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

/**
 * Retrofit API 클라이언트
 * Token 인증을 자동으로 모든 요청에 추가
 */
public class ApiClient {
    // 에뮬레이터 사용 시: http://10.0.2.2:8000/
    // 실제 기기 사용 시: http://[컴퓨터IP]:8000/
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    
    // Django 서버에서 생성한 Token (표 요구사항: 보안키를 이용한 로그인)
    private static final String TOKEN = "3aaa6f4666681d72f9aeb065a6074b9c3c1613e1";

    private static Retrofit retrofit = null;

    /**
     * Token 인증을 자동으로 추가하는 Interceptor
     */
    private static Interceptor getAuthInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                
                // Authorization 헤더 추가
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Token " + TOKEN);
                
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    /**
     * Retrofit 클라이언트 생성 (Token 인증 포함)
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // OkHttpClient에 Interceptor 추가
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(getAuthInterceptor())
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)  // Token 인증이 포함된 OkHttpClient 사용
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}

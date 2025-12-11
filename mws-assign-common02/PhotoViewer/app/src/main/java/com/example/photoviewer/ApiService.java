package com.example.photoviewer;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api_root/Post/")
    Call<List<Post>> getPosts();
}

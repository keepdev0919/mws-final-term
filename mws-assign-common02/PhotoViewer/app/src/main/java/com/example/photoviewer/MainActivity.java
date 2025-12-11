package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPosts();
    }

    private void loadPosts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Post>> call = apiService.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    adapter = new PostAdapter(posts, ApiClient.getBaseUrl());
                    recyclerView.setAdapter(adapter);
                    Log.d(TAG, "Loaded " + posts.size() + " posts");
                    Toast.makeText(MainActivity.this,
                            "Loaded " + posts.size() + " posts",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Failed to load posts: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }
}

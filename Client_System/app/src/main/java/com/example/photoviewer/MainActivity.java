package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메인 액티비티
 * TabLayout을 통해 전체/방문자/택배 로그를 필터링하여 표시
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TabLayout 초기화 및 설정
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("전체"));
        tabLayout.addTab(tabLayout.newTab().setText("방문자"));
        tabLayout.addTab(tabLayout.newTab().setText("택배"));

        // 탭 선택 리스너 설정
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 탭 선택 시 해당 타입의 로그만 로드
                String type = null;
                switch (tab.getPosition()) {
                    case 0:  // 전체
                        type = null;
                        break;
                    case 1:  // 방문자
                        type = "VISITOR";
                        break;
                    case 2:  // 택배
                        type = "PACKAGE";
                        break;
                }
                loadLogs(type);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 필요 없음
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 탭 재선택 시에도 다시 로드
                onTabSelected(tab);
            }
        });

        // 초기 로드 (전체 로그)
        loadLogs(null);
    }

    /**
     * 서버에서 로그 목록을 로드
     * @param type 필터링 타입 (null: 전체, "VISITOR": 방문자, "PACKAGE": 택배)
     */
    private void loadLogs(String type) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Post>> call = apiService.getLogs(type);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> logs = response.body();
                    adapter = new PostAdapter(logs, ApiClient.getBaseUrl());
                    recyclerView.setAdapter(adapter);
                    Log.d(TAG, "Loaded " + logs.size() + " logs (type: " + type + ")");
                    Toast.makeText(MainActivity.this,
                            "로드 완료: " + logs.size() + "개",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "로드 실패: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "오류: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }
}

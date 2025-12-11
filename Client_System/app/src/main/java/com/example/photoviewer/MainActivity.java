package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.tabs.TabLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메인 액티비티
 * TabLayout을 통해 전체/방문자/택배 로그를 필터링하여 표시
 * SwipeRefreshLayout을 통해 Pull-to-Refresh 기능 제공
 * 정렬 기능(최신순/오래된순)을 제공
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String currentType = null;  // 현재 선택된 필터 타입 저장
    
    // 정렬 관련 변수
    private Button btnSortNewest;
    private Button btnSortOldest;
    private List<Post> currentLogs = new ArrayList<>();  // 현재 로드된 로그 목록
    private boolean isNewestFirst = true;  // true: 최신순, false: 오래된순

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // SwipeRefreshLayout 초기화 (Pull-to-Refresh 기능)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
        
        // Pull-to-Refresh 리스너 설정
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 현재 선택된 탭의 타입으로 새로고침
                Toast.makeText(MainActivity.this, "새로고침 중...", Toast.LENGTH_SHORT).show();
                loadLogs(currentType);
            }
        });

        // 정렬 버튼 초기화
        btnSortNewest = findViewById(R.id.btnSortNewest);
        btnSortOldest = findViewById(R.id.btnSortOldest);
        
        // 최신순 버튼 클릭 리스너
        btnSortNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNewestFirst) {
                    isNewestFirst = true;
                    updateSortButtonStyle();
                    sortAndDisplayLogs();
                    Toast.makeText(MainActivity.this, "최신순 정렬", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // 오래된순 버튼 클릭 리스너
        btnSortOldest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewestFirst) {
                    isNewestFirst = false;
                    updateSortButtonStyle();
                    sortAndDisplayLogs();
                    Toast.makeText(MainActivity.this, "오래된순 정렬", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                switch (tab.getPosition()) {
                    case 0:  // 전체
                        currentType = null;
                        break;
                    case 1:  // 방문자
                        currentType = "VISITOR";
                        break;
                    case 2:  // 택배
                        currentType = "PACKAGE";
                        break;
                }
                loadLogs(currentType);
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
     * Pull-to-Refresh 완료 시 새로고침 애니메이션 종료
     * @param type 필터링 타입 (null: 전체, "VISITOR": 방문자, "PACKAGE": 택배)
     */
    private void loadLogs(String type) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Post>> call = apiService.getLogs(type);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                // 새로고침 애니메이션 종료
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    // 로드된 로그 저장 및 정렬 적용
                    currentLogs = new ArrayList<>(response.body());
                    sortAndDisplayLogs();
                    Log.d(TAG, "Loaded " + currentLogs.size() + " logs (type: " + type + ")");
                    Toast.makeText(MainActivity.this,
                            "로드 완료: " + currentLogs.size() + "개",
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
                // 새로고침 애니메이션 종료
                swipeRefreshLayout.setRefreshing(false);
                
                Toast.makeText(MainActivity.this,
                        "오류: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }
    
    /**
     * 현재 로그 목록을 정렬하고 화면에 표시
     * isNewestFirst 값에 따라 최신순 또는 오래된순으로 정렬
     */
    private void sortAndDisplayLogs() {
        if (currentLogs == null || currentLogs.isEmpty()) {
            return;
        }
        
        // 정렬 수행 (createdAt 기준)
        Collections.sort(currentLogs, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                String date1 = p1.getCreatedAt() != null ? p1.getCreatedAt() : "";
                String date2 = p2.getCreatedAt() != null ? p2.getCreatedAt() : "";
                
                if (isNewestFirst) {
                    // 최신순: 내림차순 (최신이 위로)
                    return date2.compareTo(date1);
                } else {
                    // 오래된순: 오름차순 (오래된게 위로)
                    return date1.compareTo(date2);
                }
            }
        });
        
        // 어댑터 업데이트
        adapter = new PostAdapter(currentLogs, ApiClient.getBaseUrl());
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * 정렬 버튼 스타일 업데이트
     * 선택된 버튼은 초록색, 미선택 버튼은 회색으로 표시
     */
    private void updateSortButtonStyle() {
        if (isNewestFirst) {
            btnSortNewest.setBackgroundTintList(
                getResources().getColorStateList(android.R.color.holo_green_dark));
            btnSortOldest.setBackgroundTintList(
                getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            btnSortNewest.setBackgroundTintList(
                getResources().getColorStateList(android.R.color.darker_gray));
            btnSortOldest.setBackgroundTintList(
                getResources().getColorStateList(android.R.color.holo_green_dark));
        }
    }
}

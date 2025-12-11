package com.example.photoviewer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts;
    private String baseUrl;

    public PostAdapter(List<Post> posts, String baseUrl) {
        this.posts = posts;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        
        // 로그 타입 표시 (방문자/택배)
        holder.titleTextView.setText(post.getLogTypeDisplay());
        
        // 설명 표시 (감지된 객체명)
        holder.textTextView.setText("감지된 객체: " + post.getDescription());

        // 감지 시간 표시
        if (post.getCreatedAt() != null && post.getCreatedAt().length() >= 19) {
            holder.dateTextView.setText("감지 시간: " + post.getCreatedAt().substring(0, 19).replace("T", " "));
        } else if (post.getCreatedAt() != null) {
            holder.dateTextView.setText("감지 시간: " + post.getCreatedAt());
        }

        // 이미지 로드
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            // API returns full URL with 127.0.0.1, need to replace with 10.0.2.2 for emulator
            String imageUrl = post.getImage().replace("127.0.0.1", "10.0.2.2");
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, textTextView, dateTextView;
        ImageView imageView;

        PostViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            textTextView = itemView.findViewById(R.id.textTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

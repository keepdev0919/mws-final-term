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
        holder.titleTextView.setText(post.getTitle());
        holder.textTextView.setText(post.getText());

        if (post.getPublishedDate() != null) {
            holder.dateTextView.setText("Published: " + post.getPublishedDate().substring(0, 19));
        }

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

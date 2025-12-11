package com.example.photoviewer;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("text")
    private String text;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("published_date")
    private String publishedDate;

    @SerializedName("image")
    private String image;

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getText() { return text; }
    public String getCreatedDate() { return createdDate; }
    public String getPublishedDate() { return publishedDate; }
    public String getImage() { return image; }
}

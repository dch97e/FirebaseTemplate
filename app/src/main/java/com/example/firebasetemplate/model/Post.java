package com.example.firebasetemplate.model;

import java.util.HashMap;

public class Post {
    public String content;
    public String authorName;
    public String date;
    public String imageUrl;
    public String postid;
    public HashMap<String, Boolean>  likes = new HashMap<>();
}
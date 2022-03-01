package com.example.firebasetemplate.model;

import java.util.HashMap;

public class Post {
    public String content;
    public String authorName;
    public String date;
    public String imageUrl;
    public String postid;
    public String imageUser;
    public HashMap<String, Boolean>  likes = new HashMap<>();

    //colections("posts").documetn(postid).collections("collections")
}
package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class ReviewObject implements Serializable {
    public int id;
    public User owner;
    public FoodPost post;
    public String review;
    public String reason;
    public float rating;
    public String createdAt;

    public ReviewObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        post = new FoodPost(jo.get("post").getAsJsonObject());
        review = jo.get("review").getAsString();
        reason = jo.get("star_reason").getAsString();
        rating = jo.get("rating").getAsFloat();

        createdAt = jo.get("created_at").getAsString();
    }
}
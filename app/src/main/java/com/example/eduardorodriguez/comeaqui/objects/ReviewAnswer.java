package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class ReviewAnswer implements Serializable {
    public int id;
    public User owner;
    public ReviewObject review;
    public String answer;
    public String createdAt;

    public ReviewAnswer(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        review = new ReviewObject(jo.get("review").getAsJsonObject());
        answer = jo.get("answer").getAsString();
        createdAt = jo.get("created_at").getAsString();
    }
}
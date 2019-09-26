package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class OrderObject implements Serializable {
    public int id;
    public User owner;
    public FoodPost post;
    public User poster;
    public String status;
    public String createdAt;
    boolean seen;
    public OrderObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        post = new FoodPost(jo.get("post").getAsJsonObject());
        poster = new User(jo.get("poster").getAsJsonObject());
        status = jo.get("order_status").getAsString();
        seen = jo.get("seen").getAsBoolean();
        createdAt = jo.get("created_at").getAsString();
    }
}
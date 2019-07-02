package com.example.eduardorodriguez.comeaqui;

import com.example.eduardorodriguez.comeaqui.profile.User;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class OrderObject implements Serializable {
    public int id;
    public User owner;
    public FoodPost post;
    public User poster;
    public OrderObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        post = new FoodPost(jo.get("post").getAsJsonObject());
        poster = new User(jo.get("poster").getAsJsonObject());
    }
}
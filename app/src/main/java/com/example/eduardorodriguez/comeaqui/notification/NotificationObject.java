package com.example.eduardorodriguez.comeaqui.notification;

import com.example.eduardorodriguez.comeaqui.FoodPost;
import com.example.eduardorodriguez.comeaqui.profile.User;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class NotificationObject implements Serializable {
    public int id;
    public User owner;
    public User sender;
    public FoodPost post;
    public String createdAt;

    public NotificationObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        sender = new User(jo.get("sender").getAsJsonObject());
        post = new FoodPost(jo.get("post").getAsJsonObject());
        createdAt = jo.get("created_at").getAsString();
    }
}

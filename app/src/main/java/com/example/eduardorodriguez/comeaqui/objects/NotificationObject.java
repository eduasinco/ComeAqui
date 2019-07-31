package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class NotificationObject implements Serializable {
    public int id;
    public OrderObject order;
    public User owner;
    public User sender;
    public FoodPost post;
    public String createdAt;

    public NotificationObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        order = new OrderObject(jo.get("order").getAsJsonObject());
        owner = order.post.owner;
        sender = order.owner;
        post = order.post;
        createdAt = jo.get("created_at").getAsString();
    }
}
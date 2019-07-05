package com.example.eduardorodriguez.comeaqui.chat;

import com.example.eduardorodriguez.comeaqui.profile.User;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class MessageObject implements Serializable {
    public User sender;
    public String message;
    public String createdAt;
    public MessageObject(JsonObject jo){
        sender = new User(jo.get("sender").getAsJsonObject());
        message = jo.get("message").getAsString();
        createdAt = jo.get("created_at").getAsString();
    }
}

package com.example.eduardorodriguez.comeaqui.chat.chat_objects;

import com.example.eduardorodriguez.comeaqui.objects.User;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class MessageObject implements Serializable {
    public User sender;
    public String message;
    public String createdAt;

    public boolean newDay = false;
    public boolean topSpace = false;
    public boolean lastInGroup = false;
    public boolean isOwner = false;

    public MessageObject(JsonObject jo){
        sender = new User(jo.get("sender").getAsJsonObject());
        message = jo.get("message").getAsString();
        createdAt = jo.get("created_at").getAsString();
    }
}



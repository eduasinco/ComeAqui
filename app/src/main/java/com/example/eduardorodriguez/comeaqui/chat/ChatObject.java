package com.example.eduardorodriguez.comeaqui.chat;

import com.example.eduardorodriguez.comeaqui.objects.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {
    public int id;
    public ArrayList<User> users;
    public ArrayList<MessageObject> messages;
    public String createdAt;
    public ChatObject(JsonObject jo){
        users = new ArrayList<>();
        id = jo.get("id").getAsInt();
        createdAt = jo.get("created_at").getAsString();
        for (JsonElement je: jo.get("users").getAsJsonArray()){
            users.add(new User(je.getAsJsonObject()));
        }

        messages = new ArrayList<>();
        for (JsonElement je: jo.get("message_set").getAsJsonArray()){
            messages.add(new MessageObject(je.getAsJsonObject()));
        }
    }
}

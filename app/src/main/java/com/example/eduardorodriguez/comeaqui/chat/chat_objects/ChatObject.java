package com.example.eduardorodriguez.comeaqui.chat.chat_objects;

import com.example.eduardorodriguez.comeaqui.objects.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {
    public int id;
    public ArrayList<User> users;
    public MessageObject last_message;
    public String createdAt;

    public int unread_count = 0;

    public ChatObject(JsonObject jo){
        users = new ArrayList<>();
        id = jo.get("id").getAsInt();
        createdAt = jo.get("created_at").getAsString();
        for (JsonElement je: jo.get("users").getAsJsonArray()){
            users.add(new User(je.getAsJsonObject()));
        }

        last_message = new MessageObject(jo.get("last_message").getAsJsonObject());
    }
}

package com.example.eduardorodriguez.comeaqui.chat;

import com.example.eduardorodriguez.comeaqui.profile.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {
    public int id;
    public ArrayList<User> users;
    public MessageObject lastMessage;
    public ChatObject(JsonObject jo){
        users = new ArrayList<>();
        id = jo.get("id").getAsInt();
        JsonArray ja = jo.get("users").getAsJsonArray();
        for (JsonElement je: ja){
            JsonObject userJson = je.getAsJsonObject();
            users.add(new User(userJson));
        }
    }
}

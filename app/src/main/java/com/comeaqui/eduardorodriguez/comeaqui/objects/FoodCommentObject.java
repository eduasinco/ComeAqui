package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodCommentObject implements Serializable {
    public int id;
    public User owner;
    public FoodPost post;
    public ArrayList<FoodCommentObject> replies;
    public String message;
    public String createdAt;
    public FoodCommentObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        try{post = new FoodPost(jo.get("post").getAsJsonObject());} catch (Exception e){};
        message = jo.get("message").getAsString();
        replies = new ArrayList<>();
        try {
            for (JsonElement je : jo.get("replies").getAsJsonArray()) {
                replies.add(new FoodCommentObject(je.getAsJsonObject()));
            }
        }catch (Exception e){
        }
        createdAt = jo.get("created_at").getAsString();
    }
}
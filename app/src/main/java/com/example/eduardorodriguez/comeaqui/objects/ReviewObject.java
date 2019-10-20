package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ReviewObject implements Serializable {
    public int id;
    public User owner;
    public FoodPost post;
    public String review;
    public String reason;
    public float rating;
    public ArrayList<ReviewAnswer> answers;
    public String createdAt;

    public ReviewObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        review = jo.get("review").getAsString();
        reason = jo.get("star_reason").getAsString();
        rating = jo.get("rating").getAsFloat();

        answers = new ArrayList<>();
        if (jo.get("answers") != null){
            for (JsonElement je: jo.get("answers").getAsJsonArray()){
                try{
                    answers.add(new ReviewAnswer(je.getAsJsonObject()));
                }catch (Exception ignore){}
            }
        }
        createdAt = jo.get("created_at").getAsString();
    }
}
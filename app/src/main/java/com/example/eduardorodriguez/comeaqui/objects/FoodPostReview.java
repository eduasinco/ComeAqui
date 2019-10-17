package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodPostReview extends FoodPost implements Serializable {
    public ArrayList<ReviewObject> reviews;

    public FoodPostReview(JsonObject jo){
        super(jo);
        try {
            reviews = new ArrayList<>();
            for (JsonElement je: jo.get("reviews").getAsJsonArray()){
                reviews.add(new ReviewObject(je.getAsJsonObject()));
            }
        } catch (Exception e){}
    }
}
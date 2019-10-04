package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class FoodPostReview extends FoodPost implements Serializable {
    public ReviewObject review;

    public FoodPostReview(JsonObject jo){
        super(jo);
        review = new ReviewObject(jo.get("review").getAsJsonObject());
    }
}
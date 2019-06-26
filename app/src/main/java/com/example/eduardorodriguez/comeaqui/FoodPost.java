package com.example.eduardorodriguez.comeaqui;

import com.google.gson.JsonObject;
import java.io.Serializable;

public class FoodPost implements Serializable {
    public int id;
    public int owner_id;
    public String owner_name;
    public String owner_username;
    public String owner_first_name;
    public String owner_last_name;
    public String plate_name;
    public String price;
    public String type;
    public String description;
    public String food_photo;
    public String owner_photo;
    public String time;
    public float lat;
    public float lng;
    public FoodPost(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner_id = jo.get("owner_id").getAsInt();
        owner_first_name = jo.get("owner_first_name").getAsString();
        owner_last_name = jo.get("owner_last_name").getAsString();
        owner_username = jo.get("owner_username").getAsString();
        plate_name = jo.get("plate_name").getAsString();
        price = jo.get("price").getAsString();
        type = jo.get("food_type").getAsString();
        description = jo.get("description").getAsString();
        food_photo = jo.get("food_photo").getAsString();
        owner_photo = jo.get("poster_image").getAsString();
        time = jo.get("time").getAsString();
        lat = jo.get("lat").getAsFloat();
        lng = jo.get("lng").getAsFloat();
    }
}
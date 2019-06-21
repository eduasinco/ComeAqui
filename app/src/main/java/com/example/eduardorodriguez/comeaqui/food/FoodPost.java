package com.example.eduardorodriguez.comeaqui.food;

import com.google.gson.JsonObject;
import java.io.Serializable;

public class FoodPost implements Serializable {
    public int id;
    public int owner_id;
    public String plate_name;
    public String price;
    public String type;
    public String description;
    public String food_photo;
    public FoodPost(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner_id = jo.get("owner_id").getAsInt();
        plate_name = jo.get("plate_name").getAsString();
        price = jo.get("price").getAsString();
        type = jo.get("food_type").getAsString();
        description = jo.get("description").getAsString();
        food_photo = jo.get("food_photo").getAsString();
    }
}
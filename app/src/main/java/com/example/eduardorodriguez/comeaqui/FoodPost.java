package com.example.eduardorodriguez.comeaqui;

import com.example.eduardorodriguez.comeaqui.profile.User;
import com.google.gson.JsonObject;
import java.io.Serializable;

public class FoodPost implements Serializable {
    public int id;
    public User owner;
    public String plate_name;
    public String price;
    public String type;
    public String description;
    public String food_photo;
    public String time;
    public String address;
    public float lat;
    public float lng;
    public boolean favourite = false;
    public boolean favourite_from_server = false;
    public int favouriteId;

    public FoodPost(JsonObject jo){
        id = jo.get("id").getAsInt();
        plate_name = jo.get("plate_name").getAsString();
        price = jo.get("price").getAsString();
        type = jo.get("food_type").getAsString();
        description = jo.get("description").getAsString();
        food_photo = jo.get("food_photo").isJsonNull() ? null: jo.get("food_photo").getAsString();
        time = jo.get("time").getAsString();
        lat = jo.get("lat").getAsFloat();
        lng = jo.get("lng").getAsFloat();
        address = jo.get("address").getAsString();
        owner = new User(jo.get("owner").getAsJsonObject());
    }
}
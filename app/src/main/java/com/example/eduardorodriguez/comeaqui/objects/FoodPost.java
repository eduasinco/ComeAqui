package com.example.eduardorodriguez.comeaqui.objects;

import com.example.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;

public class FoodPost implements Serializable {
    public int id;
    public User owner;
    public String plate_name;
    public int max_dinners;
    public String price;
    public String type;
    public String description;
    public String time_to_show;
    public String start_time;
    public String end_time;
    public String address;
    public ArrayList<FoodPostImageObject> images;
    public float lat;
    public float lng;
    public boolean favourite = false;
    public int favouriteId;
    public float rating;

    public FoodPost(JsonObject jo){
        id = jo.get("id").getAsInt();
        plate_name = jo.get("plate_name").getAsString();
        max_dinners = jo.get("max_dinners").getAsInt();
        price = jo.get("price").getAsString();
        type = jo.get("food_type").getAsString();
        description = jo.get("description").getAsString();
        start_time = jo.get("start_time").getAsString();
        end_time = jo.get("end_time").getAsString();
        time_to_show = DateFormatting.hhmmHappenedNowTodayYesterdayWeekDay(start_time, end_time);
        lat = jo.get("lat").getAsFloat();
        lng = jo.get("lng").getAsFloat();
        address = jo.get("address").getAsString();
        rating = jo.get("rating").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());

        images = new ArrayList<>();
        try {
            for (JsonElement je: jo.get("images").getAsJsonArray()){
                images.add(new FoodPostImageObject(je.getAsJsonObject()));
            }
        }catch (Exception ignore){}
    }
}
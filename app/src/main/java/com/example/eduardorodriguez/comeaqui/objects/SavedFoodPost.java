package com.example.eduardorodriguez.comeaqui.objects;

import com.example.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class SavedFoodPost extends FoodPost {

    public SavedFoodPost(JsonObject jo){
        id = jo.get("id").getAsInt();
        plate_name = jo.get("plate_name") instanceof JsonNull ? "" : jo.get("plate_name").getAsString();

        formatted_address = jo.get("formatted_address") instanceof JsonNull ? "" : jo.get("formatted_address").getAsString();
        place_id = jo.get("place_id") instanceof JsonNull ? "" : jo.get("place_id").getAsString();
        lat = jo.get("lat").getAsDouble();
        lng = jo.get("lng").getAsDouble();
        street_n = jo.get("street_n") instanceof JsonNull ? "" : jo.get("street_n").getAsString();
        route = jo.get("route") instanceof JsonNull ? "" : jo.get("route").getAsString();
        administrative_area_level_2 = jo.get("administrative_area_level_2") instanceof JsonNull ? "" : jo.get("administrative_area_level_2").getAsString();
        administrative_area_level_1 = jo.get("administrative_area_level_1") instanceof JsonNull ? "" : jo.get("administrative_area_level_1").getAsString();
        country = jo.get("country") instanceof JsonNull ? "" : jo.get("country").getAsString();
        postal_code = jo.get("postal_code") instanceof JsonNull ? "" : jo.get("postal_code").getAsString();

        start_time = jo.get("start_time") instanceof JsonNull ? "" : jo.get("start_time").getAsString();
        end_time = jo.get("end_time") instanceof JsonNull ? "" : jo.get("end_time").getAsString();
        time_to_show = jo.get("start_time") instanceof JsonNull && jo.get("end_time") instanceof JsonNull ? "" : DateFormatting.hhmmHappenedNowTodayYesterdayWeekDay(start_time, end_time);
        max_dinners = jo.get("max_dinners") instanceof JsonNull ? 0 : jo.get("max_dinners").getAsInt();
        price = jo.get("price") instanceof JsonNull ? 0 :  jo.get("price").getAsInt();
        type = jo.get("food_type") instanceof JsonNull ? "" : jo.get("food_type").getAsString();
        description = jo.get("description") instanceof JsonNull ? "" : jo.get("description").getAsString();

        images = new ArrayList<>();
        try {
            for (JsonElement je: jo.get("images").getAsJsonArray()){
                images.add(0, new FoodPostImageObject(je.getAsJsonObject()));
            }
        }catch (Exception ignore){}

        rating = jo.get("rating").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        visible = jo.get("visible").getAsBoolean();
    }
}

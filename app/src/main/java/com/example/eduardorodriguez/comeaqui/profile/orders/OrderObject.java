package com.example.eduardorodriguez.comeaqui.profile.orders;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class OrderObject implements Serializable {
    public String id;
    public String owner_id;
    public String owner_username;
    public String orderStatus;
    public String postPlateName;
    public String postFoodPhoto;
    public String postPrice;
    public String postDescription;
    public String posterFirstName;
    public String posterLastName;
    public String posterEmail;
    public String posterImage;
    public String postAddress;
    public String posterPhoneNumber;
    public String posterPhoneCode;
    public String time;
    public String postLat;
    public String postLng;
    public OrderObject(JsonObject jo){
        id = jo.get("id").getAsNumber().toString();
        owner_id = jo.get("owner_id").getAsNumber().toString();
        owner_username = jo.get("owner_username").getAsString();
        orderStatus = jo.get("order_status").getAsString();

        postPlateName = jo.get("post_plate_name").getAsString();
        postFoodPhoto = jo.get("post_food_photo").getAsString();
        time = jo.get("post_time").getAsString();
        postPrice = jo.get("post_price").getAsString();
        postDescription = jo.get("poster_first_name").getAsString();
        postLat = jo.get("post_lat").getAsString();
        postLng = jo.get("post_lng").getAsString();
        postAddress = jo.get("post_address").getAsString();

        posterFirstName = jo.get("poster_first_name").getAsString();
        posterLastName = jo.get("poster_last_name").getAsString();
        posterEmail = jo.get("poster_email").getAsString();
        posterImage = jo.get("poster_image").getAsString();
        posterPhoneNumber = jo.get("poster_phone_number").getAsString();
        posterPhoneCode = jo.get("poster_phone_code").getAsString();
    }
}
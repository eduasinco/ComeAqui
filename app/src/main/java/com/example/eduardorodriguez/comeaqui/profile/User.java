package com.example.eduardorodriguez.comeaqui.profile;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class User implements Serializable {
    public int id;
    public String email;
    public String first_name;
    public String last_name;
    public String username;
    public String bio;
    public String phone_number;
    public String phone_code;
    public String location;
    public String deliver_radius;
    public String profile_photo;
    public String is_active;
    public String is_admin;

    public User(JsonObject jo){
        id = jo.get("id").getAsInt();
        email = jo.get("email").getAsString();
        first_name = jo.get("first_name").getAsString();
        last_name = jo.get("last_name").getAsString();
        bio = jo.get("bio").getAsString();
        phone_number = jo.get("phone_number").getAsString();
        phone_code = jo.get("phone_code").getAsString();
        location = jo.get("location").getAsString();
        deliver_radius = jo.get("deliver_radius").getAsString();
        profile_photo = jo.get("profile_photo").getAsString();
        is_active = jo.get("is_active").getAsString();
        is_admin = jo.get("is_admin").getAsString();
    }
}

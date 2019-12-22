package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class StripeAccountInfoObject implements Serializable {
    public int id;
    public User owner;
    public String first_name;
    public String last_name;
    public String date_of_birth;
    public String SSN_last_4;
    public String identity_document_front;
    public String identity_document_back;
    public String business_website;
    public String email;
    public String phone_number;
    public String address_line_1;
    public String address_line_2;
    public String city;
    public String state;
    public String zip_code;
    public String country;



    public StripeAccountInfoObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        first_name = jo.get("first_name").getAsString();
        last_name = jo.get("last_name").getAsString();
        date_of_birth = jo.get("date_of_birth").getAsString();
        SSN_last_4 = jo.get("SSN_last_4").getAsString();
        identity_document_front = jo.get("identity_document_front").getAsString();
        identity_document_back = jo.get("identity_document_back").getAsString();
        business_website = jo.get("business_website").getAsString();
        email = jo.get("email").getAsString();
        phone_number = jo.get("phone_number").getAsString();
        address_line_1 = jo.get("address_line_1").getAsString();
        address_line_2 = jo.get("address_line_2").getAsString();
        city = jo.get("city").getAsString();
        state = jo.get("state").getAsString();
        zip_code = jo.get("zip_code").getAsString();
        country = jo.get("country").getAsString();

    }
}
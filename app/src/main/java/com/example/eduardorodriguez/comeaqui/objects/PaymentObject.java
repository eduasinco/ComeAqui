package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

public class PaymentObject{
    String card_number;
    String expiration_date;
    String card_type;
    String cvv;
    String zip_code;
    String country;
    public PaymentObject(JsonObject jo){
        card_number = jo.get("card_number").getAsString();
        expiration_date = jo.get("expiration_date").getAsString();
        card_type = jo.get("card_type").getAsString();
        cvv = jo.get("cvv").getAsString();
        zip_code = jo.get("zip_code").getAsString();
        country = jo.get("country").getAsString();
    }
}

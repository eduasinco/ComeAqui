package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

public class PaymentObject{
    public String card_number;
    public String expiration_date;
    public String card_type;
    public String cvv;
    public PaymentObject(JsonObject jo){
        card_number = jo.get("card_number").getAsString();
        expiration_date = jo.get("expiration_date").getAsString();
        card_type = jo.get("card_type").getAsString();
        cvv = jo.get("cvv").getAsString();
    }
}

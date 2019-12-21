package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

public class PaymentMethodObject {
    public int id;
    public String card_number;
    public String expiration_date;
    public String card_type;
    public String cvv;
    public boolean chosen;
    public PaymentMethodObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        card_number = jo.get("card_number").getAsString();
        expiration_date = jo.get("expiration_date").getAsString();
        card_type = jo.get("card_type").getAsString();
        cvv = jo.get("cvv").getAsString();
        chosen = jo.get("chosen").getAsBoolean();
    }
}

package com.example.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

public class PaymentMethodObject {
    public int id;
    public String card_number;
    public Integer exp_month;
    public Integer exp_year;
    public String cvc;
    public boolean chosen;
    public PaymentMethodObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        card_number = jo.get("card_number").getAsString();
        exp_month = jo.get("exp_month").getAsInt();
        exp_year = jo.get("exp_year").getAsInt();
        cvc = jo.get("cvc").getAsString();
        chosen = jo.get("chosen").getAsBoolean();
    }
}

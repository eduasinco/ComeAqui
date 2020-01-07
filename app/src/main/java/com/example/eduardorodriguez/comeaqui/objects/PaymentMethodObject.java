package com.example.eduardorodriguez.comeaqui.objects;

import com.example.eduardorodriguez.comeaqui.R;
import com.google.gson.JsonObject;

public class PaymentMethodObject {
    public int id;
    public String card_number;
    public Integer exp_month;
    public Integer exp_year;
    public String cvc;
    public boolean chosen;
    public String brand;
    public int brandImage = R.drawable.credit_card;
    public PaymentMethodObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        card_number = jo.get("card_number").getAsString();
        exp_month = jo.get("exp_month").getAsInt();
        exp_year = jo.get("exp_year").getAsInt();
        cvc = jo.get("cvc").getAsString();
        chosen = jo.get("chosen").getAsBoolean();
        brand = jo.get("brand").getAsString();

        switch(brand){
            case "Visa":
                brandImage = R.drawable.visa_icon;
            case "MasterCard":
                brandImage = R.drawable.mastercard_icon;

        }
    }
}

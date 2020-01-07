package com.example.eduardorodriguez.comeaqui.objects;

import com.example.eduardorodriguez.comeaqui.R;
import com.google.gson.JsonObject;

public class PaymentMethodObject {
    public String id;
    public String last4;
    public Integer exp_month;
    public Integer exp_year;
    public boolean chosen;
    public String brand;
    public int brandImage = R.drawable.credit_card;
    public PaymentMethodObject(JsonObject jo){
        id = jo.get("id").getAsString();
        last4 = jo.get("last4").getAsString();
        exp_month = jo.get("exp_month").getAsInt();
        exp_year = jo.get("exp_year").getAsInt();
        try{chosen = jo.get("chosen").getAsBoolean();}catch(Exception ignored){}
        brand = jo.get("brand").getAsString();
        switch(brand){
            case "Visa":
                brandImage = R.drawable.visa_icon;
            case "MasterCard":
                brandImage = R.drawable.mastercard_icon;

        }
    }
}

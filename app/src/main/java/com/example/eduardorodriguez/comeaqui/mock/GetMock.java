package com.example.eduardorodriguez.comeaqui.mock;
import android.support.v7.app.AppCompatActivity;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class GetMock extends AppCompatActivity {

    public static JsonArray get(String index){
        String path = "";
        switch (index){
            case "food":
                path = "mock/food.txt";
                break;
            case "my_profile":
                path = "mock/my_profile.txt";
                break;
            case "my_profile_card":
                path = "mock/my_profile_card.txt";
                break;
            case "my_messages":
                path = "mock/my_messages.txt";
                break;
            case "my_get_orders":
                path = "mock/my_get_orders.txt";
                break;


        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(SplashActivity.context.getResources().getAssets().open(path), "UTF-8"));
            StringBuilder content= new StringBuilder("");
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                content.append(mLine);
            }
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(content.toString()).getAsJsonArray();
            return jsonArray;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}

package com.example.eduardorodriguez.comeaqui.mock;
import android.support.v7.app.AppCompatActivity;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import java.io.*;

public class GetMock extends AppCompatActivity {

    public static String get(int index){
        String path = "";
        switch (index){
            case 1:
                path = "mock/food.txt";
                break;
            case 2:
                path = "mock/my_profile.txt";
                break;
            case 3:
                path = "mock/my_profile_card.txt";
                break;
            case 4:
                path = "mock/my_messages.txt";
                break;
            case 6:
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
            return content.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}

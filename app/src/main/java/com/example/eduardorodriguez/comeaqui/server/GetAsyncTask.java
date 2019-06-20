package com.example.eduardorodriguez.comeaqui.server;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.example.eduardorodriguez.comeaqui.mock.GetMock;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderFragment;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.example.eduardorodriguez.comeaqui.food.GetFoodFragment;
import com.example.eduardorodriguez.comeaqui.eat.MapFragment;
import com.example.eduardorodriguez.comeaqui.profile.*;
import com.example.eduardorodriguez.comeaqui.profile.messages.MessagesFragment;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.payment.PaymentMethodFragment;
import com.example.eduardorodriguez.comeaqui.profile.settings.SettingsActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;

public class GetAsyncTask extends AsyncTask<String, Void, String>
{
    String uri;

    int url_index;
    public GetAsyncTask(int url_index, String uri){
        this.url_index = url_index;
        this.uri = "http://127.0.0.1:8000/";
        this.uri += uri;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String... params)
    {
        if (SplashActivity.mock){
            return GetMock.get(url_index);
        }

        if (params.length != 0 && params[0] == "editAccount"){
            this.url_index = 5;
        }

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);

// Add authorization header
        httpGet.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());

// Set up the header types needed to properly transfer JSON
        httpGet.setHeader("Content-Type", "application/json");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            } else {
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response)
    {
        if(response != null)
        {
            switch (url_index){
                case 0:
                    UserPostFragment.makeList(response);
                    break;
                case 1:
                    GetFoodFragment.makeList(response);
                    break;
                case 2:
                    ProfileFragment.setProfile(response);
                    break;
                case 3:
                    PaymentMethodFragment.makeList(response);
                    break;
                case 4:
                    MessagesFragment.makeList(response);
                    break;
                case 5:
                    SettingsActivity.setProfile(response);
                    break;
                case 6:
                    OrderFragment.makeList(response);
                    break;
                case 7:
                    MapFragment.makeList(response);
                    break;
                case 8:
                    OrderLookActivity.putData(response);
                    break;
                case 9:
                    MapFragment.makeOrderList(response);
                    break;
            }
        }
    }
}
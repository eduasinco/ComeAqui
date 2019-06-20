package com.example.eduardorodriguez.comeaqui.server;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.example.eduardorodriguez.comeaqui.mock.GetMock;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;

public class GetAsyncTask extends AsyncTask<String, Void, JsonArray>
{
    String uri;

    public GetAsyncTask(String uri){
        this.uri = "http://127.0.0.1:8000/";
        this.uri += uri;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected JsonArray doInBackground(String... params)
    {
        if (SplashActivity.mock){
            return GetMock.get(uri);
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

                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(builder.toString()).getAsJsonArray();
                return jsonArray;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JsonArray response)
    {
        if(response != null) {}
    }
}
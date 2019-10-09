package com.example.eduardorodriguez.comeaqui.server;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAsyncTask extends AsyncTask<String[], Void, String>
{


    private String uri;
    public String method;

    public GetAsyncTask(String method, String uri){
        this.uri = uri;
        this.method = method;
    }

    @Override
    protected String doInBackground(String[]... params)
    {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();


        HttpGet httpGet = new HttpGet(this.uri);
        httpGet.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());
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
                String resp = builder.toString();
                return resp;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
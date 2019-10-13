package com.example.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.*;

public class PatchAsyncTask extends AsyncTask<String[], Void, JSONObject> {

    public Bitmap bitmap;
    String uri;
    public PatchAsyncTask(String uri){
        this.uri = uri;
    }

    @Override
    protected JSONObject doInBackground(String[]... params)
    {

        HttpPatch httpPatch = new HttpPatch(uri);
        httpPatch.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for(String[] ss: params){
            if (ss[0].equals("food_photo") && bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                entityBuilder.addPart(ss[0], new ByteArrayBody(imageBytes, "ANDROID.png"));
            } else {
                entityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
            }
        }
        httpPatch.setEntity(entityBuilder.build());
        try {
            HttpResponse response = httpclient.execute(httpPatch);
            InputStream instream = response.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            return new JSONObject(stringBuffer.toString());

        }  catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        if(response != null)
        {
        }
    }
}
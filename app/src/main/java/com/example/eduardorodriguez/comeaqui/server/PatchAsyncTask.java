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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.*;

public class PatchAsyncTask extends AsyncTask<String, Void, JSONObject> {

    public Bitmap imageBitmap;
    String uri;
    public PatchAsyncTask(String uri){
        this.uri = uri;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {

        HttpPatch httpPatch = new HttpPatch( this.uri);
        httpPatch.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());

        HttpClient httpclient = new DefaultHttpClient();

        StringBody value = new StringBody(params[1], ContentType.TEXT_PLAIN);
        HttpEntity entity;

        if (imageBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            ByteArrayBody bab = new ByteArrayBody(imageBytes, "ANDROID.png");
            entity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart(params[0], bab)
                    .build();
        } else {
            entity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart(params[0], value)
                    .build();
        }

        httpPatch.setEntity(entity);
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
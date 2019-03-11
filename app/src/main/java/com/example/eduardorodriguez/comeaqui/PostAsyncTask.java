package com.example.eduardorodriguez.comeaqui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.*;

public class PostAsyncTask extends AsyncTask<Void, Void, JSONObject>
{
    Bitmap bitmap;
    @Override
    protected JSONObject doInBackground(Void... params)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes = baos.toByteArray();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8000/get_food/");

        String boundary = "-------------" + System.currentTimeMillis();

        httpPost.setHeader("Content-type","multipart/form-data; boundary="+boundary);

        ByteArrayBody bab = new ByteArrayBody(imageBytes, "ANDROID.png");
        StringBody name = new StringBody("ANDROID", ContentType.TEXT_PLAIN);
        StringBody description = new StringBody("ANDROID", ContentType.TEXT_PLAIN);

        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary)
                .addPart("plate_name", name)
                .addPart("description", description)
                .addPart("food_photo", bab)
                .build();

        httpPost.setEntity(entity);

        try {
            HttpResponse response = httpclient.execute(httpPost);
            System.out.println(response.toString());
            return new JSONObject(response.toString());
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
            System.out.print(response.toString());
            GetFoodFragment.makeList(response.toString());
        }
    }
}
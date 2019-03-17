package com.example.eduardorodriguez.comeaqui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
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
import java.security.acl.LastOwnerException;

public class PostAsyncTask extends AsyncTask<String, Void, JSONObject>
{
    Bitmap bitmap;
    @Override
    protected JSONObject doInBackground(String... params)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8000/foods/");
        httpPost.addHeader("Authorization", "Basic " + LoginActivity.getAuthoritation());

        HttpClient httpclient = new DefaultHttpClient();


        String boundary = "-------------" + System.currentTimeMillis();

        httpPost.setHeader("Content-type","multipart/form-data; boundary="+boundary);

        ByteArrayBody bab = new ByteArrayBody(imageBytes, "ANDROID.png");
        StringBody name = new StringBody(params[0], ContentType.TEXT_PLAIN);
        StringBody price = new StringBody(params[1], ContentType.TEXT_PLAIN);
        StringBody types = new StringBody(params[2], ContentType.TEXT_PLAIN);
        StringBody description = new StringBody(params[3], ContentType.TEXT_PLAIN);

        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary)
                .addPart("plate_name", name)
                .addPart("price", price)
                .addPart("food_type", types)
                .addPart("description", description)
                .addPart("food_photo", bab)
                .build();

        httpPost.setEntity(entity);

        try {
            HttpResponse response = httpclient.execute(httpPost);
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
            System.out.print(response.toString());
            GetFoodFragment.appendToList(response.toString());
        }
    }
}
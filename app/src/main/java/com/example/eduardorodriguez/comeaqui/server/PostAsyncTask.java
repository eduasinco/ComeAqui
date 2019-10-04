package com.example.eduardorodriguez.comeaqui.server;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.*;

public class PostAsyncTask extends AsyncTask<String[], Void, String>
{
    public PostAsyncTask(String uri){
        this.uri = uri;
    }
    String uri;
    public Bitmap bitmap;
    @Override
    protected String doInBackground(String[]... params)
    {

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());

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

        HttpEntity entity = entityBuilder.build();

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
            return stringBuffer.toString();

        }  catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) { if(response != null) {}}
}
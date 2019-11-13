package com.example.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.io.*;

import static android.content.Context.MODE_PRIVATE;

public class PostAsyncTask extends AsyncTask<String[], Void, String>
{
    private Context context;
    public PostAsyncTask(Context context, String uri){
        this.context = context;
        this.uri = uri;
    }
    String uri;
    public Bitmap bitmap;
    @Override
    protected String doInBackground(String[]... params)
    {
        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", "Basic " + credentials);

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for(String[] ss: params){
            if (ss[0].equals("image") && bitmap != null) {
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
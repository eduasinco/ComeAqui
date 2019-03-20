package com.example.eduardorodriguez.comeaqui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PutAsyncTask extends AsyncTask<String[], Void, JSONObject> {

    Bitmap imageBitmap;

    @Override
    protected JSONObject doInBackground(String[]... params)
    {

        HttpPut httpPut = new HttpPut("http://127.0.0.1:8000/password_change/");
        httpPut.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();
        httpPut.setHeader("Content-type","multipart/form-data; boundary="+boundary);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for (String[] ss: params){
            multipartEntityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
        }
        HttpEntity entity = multipartEntityBuilder.build();
        httpPut.setEntity(entity);
        try {
            HttpResponse response = httpclient.execute(httpPut);
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
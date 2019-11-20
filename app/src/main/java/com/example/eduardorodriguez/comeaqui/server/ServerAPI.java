package com.example.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

public class ServerAPI {

    static public String get(Context context, String uri) throws IOException {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Authorization", "Basic " + credentials);
        httpGet.setHeader("Content-Type", "application/json");
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
    }

    static public String post(Context context, String uri, String[][] params, Bitmap bitmap) throws IOException {
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

    }

    static public String postImages(Context context, String uri, String[][] params, Bitmap[] bitmaps) throws IOException {
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
            entityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
        }
        for(int i = 0; i < bitmaps.length; i++){
            Bitmap bitmap = bitmaps[i];
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                entityBuilder.addPart("image" + i, new ByteArrayBody(imageBytes, "ANDROID.png"));
            }
        }

        HttpEntity entity = entityBuilder.build();

        httpPost.setEntity(entity);

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

    }

    static public String patch(Context context, String uri, String[][] params, Bitmap bitmap) throws IOException {
        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        HttpPatch httpPatch = new HttpPatch(uri);
        httpPatch.addHeader("Authorization", "Basic " + credentials);

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for(String[] ss: params){
            if (ss[1].equals("image") && bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                entityBuilder.addPart(ss[0], new ByteArrayBody(imageBytes, "ANDROID.png"));
            } else {
                entityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
            }
        }
        httpPatch.setEntity(entityBuilder.build());
        HttpResponse response = httpclient.execute(httpPatch);
        InputStream instream = response.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();

    }

    static public String patchImages(Context context, String uri, String[][] params, Bitmap bitmap) throws IOException {
        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        HttpPatch httpPatch = new HttpPatch(uri);
        httpPatch.addHeader("Authorization", "Basic " + credentials);

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for(String[] ss: params){
            if (ss[1].equals("image") && bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                entityBuilder.addPart(ss[0], new ByteArrayBody(imageBytes, "ANDROID.png"));
            } else {
                entityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
            }
        }
        httpPatch.setEntity(entityBuilder.build());
        HttpResponse response = httpclient.execute(httpPatch);
        InputStream instream = response.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }


    static public String put(Context context, String uri, String[][] params) throws IOException {
        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        HttpPut httpPut = new HttpPut(uri);
        httpPut.addHeader("Authorization", "Basic " + credentials);

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();
        httpPut.setHeader("Content-type","multipart/form-foodPostHashMap; boundary="+boundary);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for (String[] ss: params){
            multipartEntityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
        }
        HttpEntity entity = multipartEntityBuilder.build();
        httpPut.setEntity(entity);
        HttpResponse response = httpclient.execute(httpPut);
        InputStream instream = response.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
        StringBuffer stringBuffer = new StringBuffer();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();


    }
}

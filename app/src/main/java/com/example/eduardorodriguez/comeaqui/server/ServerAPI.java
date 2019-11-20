package com.example.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.general.EditFoodPostActivity;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class ServerAPI {

    public static String get(Context context, String url) throws IOException {

        String credentials = getCredentials(context);

        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String upload(Context context, String method, String url, String[][] params) throws IOException {

        String credentials = getCredentials(context);
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);

            List<NameValuePair> paramsArr = new ArrayList<>();
            for (String[] ss: params){
                paramsArr.add(new BasicNameValuePair(ss[0], ss[1]));
            }
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(paramsArr));
            writer.flush();
            writer.close();
            os.close();
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String uploadImage(Context context, String method, String url, String param, Bitmap imageBitmap) throws IOException {
        String credentials = getCredentials(context);

        String boundary = "-------------" + System.currentTimeMillis();
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.setRequestProperty("Connection", "Keep-Alive");

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setBoundary(boundary);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            entityBuilder.addPart(param, new ByteArrayBody(imageBytes, "ANDROID.png"));
            HttpEntity reqEntity = entityBuilder.build();
            connection.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            connection.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

            OutputStream os = connection.getOutputStream();
            reqEntity.writeTo(connection.getOutputStream());
            os.close();
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static String getCredentials(Context context){
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            return pref.getString("cred", "");
        }
        return null;
    }
    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String readStream(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        StringBuffer buffer = new StringBuffer();
        String line;
        BufferedReader bufferedReader = new BufferedReader(reader);
        while ((line = bufferedReader.readLine()) != null)
        {
            buffer.append(line);
        }
        return buffer.toString();
    }
}


//class PatchAsyncTask extends AsyncTask<String[], Void, String> {
//    public Bitmap bitmap;
//    String uri;
//
//    public PatchAsyncTask(String uri){
//        this.uri = uri;
//    }
//    @Override
//    protected void onPreExecute() {
//        showProgress(true);
//        super.onPreExecute();
//    }
//    @Override
//    protected String doInBackground(String[]... params) {
//        try {
//            return ServerAPI.upload(getApplicationContext(), "PATCH", this.uri, params);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @Override
//    protected void onPostExecute(String response) {
//        super.onPostExecute(response);
//    }
//}
//class PatchImagesAsyncTask extends AsyncTask<String[], Void, String> {
//    String uri;
//    HashMap<Integer, Bitmap> bitmapHashMap;
//
//    public PatchImagesAsyncTask(String uri, HashMap<Integer, Bitmap> bitmapHashMap){
//        this.uri = uri;
//        this.bitmapHashMap = bitmapHashMap;
//    }
//    @Override
//    protected String doInBackground(String[]... params) {
//        try {
//            for (Integer imageId: bitmapHashMap.keySet()){
//                ServerAPI.uploadImage(getApplicationContext(),"PATCH", this.uri + imageId + "/", "food_photo", this.bitmapHashMap.get(imageId));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @Override
//    protected void onPostExecute(String response) {
//        showProgress(false);
//        finish();
//        super.onPostExecute(response);
//    }
//}
//class PostImagesAsyncTask extends AsyncTask<String[], Void, String> {
//    String uri;
//    public Bitmap[] bitmaps;
//    public PostImagesAsyncTask(String uri, Bitmap[] bitmaps){
//        this.uri = uri;
//        this.bitmaps = bitmaps;
//    }
//    @Override
//    protected String doInBackground(String[]... params) {
//        try {
//            for (Bitmap image: this.bitmaps){
//                ServerAPI.uploadImage(getApplicationContext(), "POST",  this.uri, "image", image);
//            }
//            return "";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @Override
//    protected void onPostExecute(String response) {
//
//        HashMap<Integer, Bitmap> bitmapHashMap= new HashMap<>();
//        for(int i = 0; i < imageBitmaps.length; i++){
//            if (imageBitmaps[i] != null && i <= foodPostDetail.images.size()-1){
//                bitmapHashMap.put(foodPostDetail.images.get(i).id, imageBitmaps[i]);
//            }
//        }
//        EditFoodPostActivity.PatchImagesAsyncTask patch = new EditFoodPostActivity.PatchImagesAsyncTask(getResources().getString(R.string.server) + "/edit_image/", bitmapHashMap);
//        patch.execute();
//        super.onPostExecute(response);
//    }
//}

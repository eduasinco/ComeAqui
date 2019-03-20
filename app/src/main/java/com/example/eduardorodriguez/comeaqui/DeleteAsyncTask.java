package com.example.eduardorodriguez.comeaqui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
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

public class DeleteAsyncTask extends AsyncTask<String, Void, JSONObject>
{
    String uri = "http://127.0.0.1:8000/foods/";
    public DeleteAsyncTask(String index) {

        this.uri += index + "/";
    }
    @Override
    protected JSONObject doInBackground(String... params)
    {

        HttpDelete hhtpDelete = new HttpDelete(uri);
        hhtpDelete.addHeader("Authorization", "Basic " + SplashActivity.getCredemtials());

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();
        hhtpDelete.setHeader("Content-type","multipart/form-data; boundary="+boundary);

        try {
            HttpResponse response = httpclient.execute(hhtpDelete);
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
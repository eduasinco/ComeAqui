package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;

public class GetFoodAsyncTask extends AsyncTask<ProfileFragment, Void, String>
{
    String uri;
    String[] url_end = {"my_foods/", "foods/", "my_profile/"};
    ProfileFragment profileContext;
    int url_index;
    public GetFoodAsyncTask(int url_index){
        this.url_index = url_index;
        this.uri = "http://127.0.0.1:8000/";
        this.uri += url_end[url_index];
    }
    @Override
    protected String doInBackground(ProfileFragment... params)
    {

        if (url_index == 2) {
            profileContext = params[0];
        }
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);

// Add authorization header
        httpGet.addHeader(BasicScheme.authenticate(
                new UsernamePasswordCredentials("eduasinco@gmail.com", "dQMLDQML1"),
                "UTF-8", false));

// Set up the header types needed to properly transfer JSON
        httpGet.setHeader("Content-Type", "application/json");
        try {
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
                return builder.toString();
            } else {
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response)
    {
        if(response != null)
        {
            switch (url_index){
                case 0:
                    UserPostFragment.makeList(response);
                    break;
                case 1:
                    GetFoodFragment.makeList(response);
                    break;
                case 2:
                    profileContext.setProfile(profileContext, response);
                    break;
            }
        }
    }
}
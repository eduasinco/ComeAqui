package com.example.eduardorodriguez.comeaqui;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GoogleAPIAsyncTask extends AsyncTask<ProfileFragment, Void, String>
{

    private static String uri;
    public GoogleAPIAsyncTask(String uri){
        this.uri = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
        for (char c: uri.toCharArray()){
            if(c == ' '){
                this.uri += "+";
            }else{
                this.uri += c;
            }
        }
        this.uri += "&types=geocode&language=en&key=AIzaSyAY98SJhng3EjroCSGZ7yfhOWhbiqUB-tw";
    }
    @Override
    protected String doInBackground(ProfileFragment... params)
    {

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);

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
            PlacesAutocompleteFragment.makeList(response);
        }
    }
}
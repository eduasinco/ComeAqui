package com.example.eduardorodriguez.comeaqui.server;

import android.os.AsyncTask;
import com.example.eduardorodriguez.comeaqui.profile.PlacesAutocompleteFragment;
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

public class GoogleAPIAsyncTask extends AsyncTask<String, Void, String>
{

    private static String uri;
    private static int index;
    public GoogleAPIAsyncTask(String uri1, String place, String uri2, int ix){
        index = ix;
        this.uri = uri1;
        for (char c: place.toCharArray()){
            if(c == ' '){
                this.uri += "+";
            }else{
                this.uri += c;
            }
        }
        this.uri += uri2;
        this.uri += "key=AIzaSyAY98SJhng3EjroCSGZ7yfhOWhbiqUB-tw";
    }
    @Override
    protected String doInBackground(String... params)
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
            switch (index){
                case 0:
                    PlacesAutocompleteFragment.makeList(response);
                    break;
                case 1:
                    PlacesAutocompleteFragment.parseLatLng(response);
                    break;
                case 2:
                    PlacesAutocompleteFragment.makeList2(response);
                    break;
            }
        }
    }
}
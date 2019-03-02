package com.example.eduardorodriguez.comeaqui;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;

public class GetFoodAsyncTask extends AsyncTask<Void, Void, JSONObject>
{
    @Override
    protected JSONObject doInBackground(Void... params)
    {

        String str="http://127.0.0.1:8000/get_food/";
        InputStream urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(str);
            urlConn = url.openConnection().getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        if(response != null)
        {
            System.out.print(response.toString());
            GetFoodFragment.makeList(response.toString());
        }
    }
}
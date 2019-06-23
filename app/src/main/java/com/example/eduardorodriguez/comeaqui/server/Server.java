package com.example.eduardorodriguez.comeaqui.server;

import android.os.AsyncTask;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Server extends AsyncTask<String[], Void, String>
{

    private String uri;
    public String method;

    public Server(String method, String uri){
        this.uri = uri;
        this.uri += "key=AIzaSyDqkl1DgwHu03SmMoqVey3sgR62GnJ-VY4";
        this.method = method;
    }

    public HttpURLConnection getConnection(String[]... params) throws IOException {
        URL url = new URL(this.uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(this.method);
        conn.setRequestProperty("Content-Type","multipart/form-data");
        conn.setDoInput(true);
        conn.setDoOutput(true);


        if (params.length > 0) {
            String inputJsonString = cerateBodyString(params);
            byte[] outputInBytes = inputJsonString.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();
        }
        return conn;
    }

    @Override
    protected String doInBackground(String[]... params)
    {

        try{
            this.uri += cerateParams(params);
            HttpURLConnection conn = getConnection(params);
            conn.connect();

            InputStream instream = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    static String cerateParams(String[][] param) {
        StringBuilder str = new StringBuilder();
        for (String[] ss: param){
            if (ss[1] != null)
                str.append("&").append(ss[0]).append("=").append(ss[1]);
        }
        return str.toString();
    }

    static String cerateBodyString(String[][] param) {
        StringBuilder json = new StringBuilder("{");
        for (String[] ss: param){
            json.append("\"").append(ss[0]).append("\"").append(":").append("\"").append(ss[1]).append("\",");
        }
        String js = json.substring(0, json.length() - 1);
        js += "}";
        return js;
    }

    @Override
    protected void onPostExecute(String response) {
        if(response != null){ }
    }
}
package com.example.eduardorodriguez.comeaqui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.squareup.picasso.Picasso;


public class ImageAsyncTask extends AsyncTask<String, Void, Bitmap>
{
    @Override
    protected Bitmap doInBackground(String... params)
    {
        try {
            Bitmap bitmap = Picasso.get().load(params[0]).get();
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    @Override
    protected void onPostExecute(Bitmap bimage)
    {
        if(bimage != null)
        {
            System.out.print(bimage.toString());
        }
    }
}

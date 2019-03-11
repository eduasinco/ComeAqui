package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetFoodAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context context;
    ArrayList<String[]> data;

    public GetFoodAdapter(Context context, ArrayList<String[]> data){

        this.context = context;
        this.data = data;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void addNewRow(ArrayList<String[]> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = inflater.inflate(R.layout.getfood_list_element, null);
        TextView food = view.findViewById(R.id.foodName);
        TextView price = view.findViewById(R.id.price);
        ImageView foodImage = view.findViewById(R.id.foodImage);
        food.setText(data.get(position)[1]);
        price.setText(data.get(position)[2]);

        final StringBuilder path = new StringBuilder();
        path.append("http://127.0.0.1:8000");
        path.append(data.get(position)[3]);


        Glide.with(context).load(path.toString()).into(foodImage);

        foodImage.setImageDrawable(LoadImageFromWebOperations(path.toString()));
        ConstraintLayout item = view.findViewById(R.id.listItem);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodLook = new Intent(context, FoodLookActivity.class);
                foodLook.putExtra("SRC", path.toString());
                context.startActivity(foodLook);
            }

        });

        return view;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getCount() { return data != null ? data.size(): 0; }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
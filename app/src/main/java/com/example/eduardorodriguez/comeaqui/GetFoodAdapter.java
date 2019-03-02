package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = inflater.inflate(R.layout.getfood_list_element, null);
        TextView food = (TextView) view.findViewById(R.id.foodName);
        TextView price = (TextView) view.findViewById(R.id.price);
        ImageView foodImage = (ImageView) view.findViewById(R.id.foodImage);

        ImageLoadTask it = new ImageLoadTask("http://127.0.0.1:8000" + data.get(position)[0], foodImage);
        it.execute();
        food.setText(data.get(position)[1]);
        price.setText(data.get(position)[2]);

        ConstraintLayout item = (ConstraintLayout) view.findViewById(R.id.listItem);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodLook = new Intent(context, FoodLookActivity.class);
                foodLook.putExtra("IMG", R.drawable.hamburger);
                context.startActivity(foodLook);
            }

        });

        return view;
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

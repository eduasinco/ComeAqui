package com.example.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;

import java.util.ArrayList;


public class GetFoodAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    View view;
    TextView poster_name;
    TextView priceDate;
    TextView posterUsername;
    TextView postAddress;
    ImageView imageView;

    Context context;
    FoodPost foodPost;
    ArrayList<FoodPost> data;

    public GetFoodAdapter(Context context, ArrayList<FoodPost> data){

        this.context = context;
        this.data = data;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void addNewRow(ArrayList<FoodPost> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        view = inflater.inflate(R.layout.getfood_list_element, null);
        poster_name = view.findViewById(R.id.poster_name);
        priceDate = view.findViewById(R.id.price_date);
        posterUsername = view.findViewById(R.id.poster_username);
        postAddress = view.findViewById(R.id.address);
        imageView = view.findViewById(R.id.poster_image);

        foodPost = data.get(position);

        poster_name.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUsername.setText(foodPost.owner.email);
        postAddress.setText(foodPost.address);
        String priceTextE = "€" + foodPost.price + "-";
        priceDate.setText(priceTextE);

        final StringBuilder path = new StringBuilder();
        path.append(context.getResources().getString(R.string.server) );
        path.append(foodPost.food_photo);

        if (!foodPost.owner.profile_photo.contains("no-image")){
            Glide.with(context).load(foodPost.owner.profile_photo).into(imageView);
        }
        ConstraintLayout item = view.findViewById(R.id.listItem);

        item.setOnClickListener(v -> {
            Intent foodLook = new Intent(context, FoodLookActivity.class);
            foodLook.putExtra("object", foodPost);
            boolean delete = false;
            if (foodPost.owner.id == MainActivity.user.id){
                delete = true;
            }
            foodLook.putExtra("delete", delete);
            context.startActivity(foodLook);
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
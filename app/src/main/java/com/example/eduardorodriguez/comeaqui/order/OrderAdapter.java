package com.example.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OrderAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    View view;
    TextView poster_name;
    TextView price;
    TextView posterUsername;
    TextView postAddress;
    TextView orderStatus;
    TextView dateView;
    ImageView imageView;

    Context context;
    OrderObject orderObject;
    ArrayList<OrderObject> data;

    public OrderAdapter(Context context, ArrayList<OrderObject> data){

        this.context = context;
        this.data = data;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void addNewRow(ArrayList<OrderObject> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        view = inflater.inflate(R.layout.order_list_element, null);
        poster_name = view.findViewById(R.id.poster_name);
        price = view.findViewById(R.id.price);
        posterUsername = view.findViewById(R.id.poster_username);
        postAddress = view.findViewById(R.id.address);
        imageView = view.findViewById(R.id.poster_image);
        orderStatus = view.findViewById(R.id.order_status);
        dateView = view.findViewById(R.id.date);

        orderObject = data.get(position);

        poster_name.setText(orderObject.owner.first_name + " " + orderObject.owner.last_name);
        posterUsername.setText(orderObject.owner.email);
        postAddress.setText(orderObject.post.address);
        String priceTextE = "€" + orderObject.post.price + " - ";
        price.setText(priceTextE);
        orderStatus.setText(orderObject.status);

        if (orderObject.status.equals("CONFIRMED")){
            orderStatus.setTextColor(context.getResources().getColor(R.color.success));
        } else if (orderObject.status.equals("CANCELED")){
            orderStatus.setTextColor(context.getResources().getColor(R.color.canceled));
        } else {
            orderStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        if (!orderObject.owner.profile_photo.contains("no-image")){
            Glide.with(context).load(orderObject.owner.profile_photo).into(imageView);
        }
        ConstraintLayout item = view.findViewById(R.id.listItem);

        item.setOnClickListener(v -> {
            Intent foodLook = new Intent(context, OrderLookActivity.class);
            foodLook.putExtra("object", orderObject);
            boolean delete = false;
            if (orderObject.owner.id == MainActivity.user.id){
                delete = true;
            }
            foodLook.putExtra("delete", delete);
            context.startActivity(foodLook);
        });
        dateView.setText(DateFragment.getDateInSimpleFormat(orderObject.createdAt));

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
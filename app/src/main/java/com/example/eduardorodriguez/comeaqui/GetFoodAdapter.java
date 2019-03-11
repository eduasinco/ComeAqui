package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.io.InputStream;
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
        final TextView food_name = view.findViewById(R.id.foodName);
        TextView price = view.findViewById(R.id.price);
        TextView description = view.findViewById(R.id.description);
        ImageView imageView = view.findViewById(R.id.foodImage);
        food_name.setText(data.get(position)[0]);
        String priceText = data.get(position)[1] + " $";
        price.setText(priceText);
        setTypes(view, data.get(position)[2]);
        description.setText(data.get(position)[3]);

        final StringBuilder path = new StringBuilder();
        path.append("http://127.0.0.1:8000");
        path.append(data.get(position)[4]);


        Glide.with(context).load(path.toString()).into(imageView);

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


    public static void setTypes(View view, String types){
        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
        imageViewArrayList.add((ImageView) view.findViewById(R.id.vegetarian));
        imageViewArrayList.add((ImageView) view.findViewById(R.id.vegan));
        imageViewArrayList.add((ImageView) view.findViewById(R.id.celiac));
        imageViewArrayList.add((ImageView) view.findViewById(R.id.spicy));
        imageViewArrayList.add((ImageView) view.findViewById(R.id.fish));
        imageViewArrayList.add((ImageView) view.findViewById(R.id.meat));
        imageViewArrayList.add((ImageView) view.findViewById(R.id.dairy));
        int[] resources = new int[]{
                R.drawable.vegetarianfill,
                R.drawable.veganfill,
                R.drawable.cerealfill,
                R.drawable.spicyfill,
                R.drawable.fishfill,
                R.drawable.meatfill,
                R.drawable.dairyfill,
        };

        for (int i = 0; i < types.length(); i++){
            if (types.charAt(i) == '1'){
                imageViewArrayList.get(i).setImageResource(resources[i]);
            }
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
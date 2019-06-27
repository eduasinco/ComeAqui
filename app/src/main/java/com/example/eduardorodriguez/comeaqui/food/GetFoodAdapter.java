package com.example.eduardorodriguez.comeaqui.food;

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

    Context context;
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

        final View view = inflater.inflate(R.layout.getfood_list_element, null);
        final TextView food_name = view.findViewById(R.id.plateName);
        TextView priceView = view.findViewById(R.id.priceText);
        TextView descriptionView = view.findViewById(R.id.orderMessage);
        ImageView imageView = view.findViewById(R.id.orderImage);


        final FoodPost foodPost = data.get(position);

        food_name.setText(foodPost.plate_name);
        String priceTextE = foodPost.price + "€";
        priceView.setText(priceTextE);
        setTypes(view, foodPost.type);
        descriptionView.setText(foodPost.description);

        final StringBuilder path = new StringBuilder();
        path.append("http://127.0.0.1:8000");
        path.append(foodPost.food_photo);

        if(SplashActivity.mock){
            imageView.setImageResource(R.drawable.food_post);
        } else {
            if (!path.toString().contains("no-image")){
                Glide.with(context).load(path.toString()).into(imageView);
            }
        }
        ConstraintLayout item = view.findViewById(R.id.listItem);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodLook = new Intent(context, FoodLookActivity.class);
                foodLook.putExtra("object", foodPost);
                boolean delete = false;
                if (foodPost.owner_id == MainActivity.user.id){
                    delete = true;
                }
                foodLook.putExtra("delete", delete);
                context.startActivity(foodLook);
            }

        });

        return view;
    }


    public static void setTypes(View view, String types){
        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
        imageViewArrayList.add(view.findViewById(R.id.vegetarian));
        imageViewArrayList.add(view.findViewById(R.id.vegan));
        imageViewArrayList.add(view.findViewById(R.id.celiac));
        imageViewArrayList.add(view.findViewById(R.id.spicy));
        imageViewArrayList.add(view.findViewById(R.id.fish));
        imageViewArrayList.add(view.findViewById(R.id.meat));
        imageViewArrayList.add(view.findViewById(R.id.dairy));
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
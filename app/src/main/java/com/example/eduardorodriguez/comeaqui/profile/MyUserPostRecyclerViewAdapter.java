package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;

import java.util.ArrayList;

public class MyUserPostRecyclerViewAdapter extends RecyclerView.Adapter<MyUserPostRecyclerViewAdapter.ViewHolder> {

    private ArrayList<FoodPost> mValues;


    public MyUserPostRecyclerViewAdapter(ArrayList<FoodPost> items) {
        mValues = items;
    }

    public void addNewRow(ArrayList<FoodPost> data){
        this.mValues = data;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_post_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FrameLayout postButton = holder.mView.findViewById(R.id.post_button);

        final FoodPost foodPost = mValues.get(position);

        holder.postNameView.setText(foodPost.plate_name);
        holder.postPrice.setText(foodPost.price + "€");
        holder.postTime.setText(foodPost.time);
        holder.posterDescriptionView.setText(foodPost.description);

        if (!foodPost.food_photo.contains("no-image")) {
            holder.imageLayout.setVisibility(View.VISIBLE);
            Glide.with(holder.mView.getContext()).load(foodPost.food_photo).into(holder.imageView);
        }

        holder.cardButtonView.setOnClickListener(v -> {
                Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
                foodLook.putExtra("object", foodPost);
                holder.mView.getContext().startActivity(foodLook);
            });

        holder.cardButtonView.setOnClickListener(v -> {
            Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
            foodLook.putExtra("object", foodPost);

            if (foodPost.owner.id == MainActivity.user.id){
                foodLook.putExtra("delete", true);
            } else {
                foodLook.putExtra("delete", false);
            }

            holder.mView.getContext().startActivity(foodLook);
        });

        ((AppCompatActivity) holder.mView.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.food_types, FoodTypeFragment.newInstance(foodPost.type))
                .commit();
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size(): 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView foodNameView;
        public TextView postTime;
        public TextView postPrice;
        public TextView posterDescriptionView;
        public TextView postNameView;
        public View cardButtonView;
        public ImageView imageView;
        public CardView imageLayout;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            foodNameView = view.findViewById(R.id.plateName);
            postNameView = view.findViewById(R.id.plate_name);
            postTime = view.findViewById(R.id.time);
            postPrice = view.findViewById(R.id.price);
            posterDescriptionView = view.findViewById(R.id.description);
            cardButtonView = view.findViewById(R.id.cardButton);
            imageView = view.findViewById(R.id.image);
            imageLayout = view.findViewById(R.id.image_layout);
        }
    }
}

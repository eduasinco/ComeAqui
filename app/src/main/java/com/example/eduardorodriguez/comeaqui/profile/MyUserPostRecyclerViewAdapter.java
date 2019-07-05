package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.FoodPost;
import com.example.eduardorodriguez.comeaqui.profile.UserPostFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUserPostRecyclerViewAdapter extends RecyclerView.Adapter<MyUserPostRecyclerViewAdapter.ViewHolder> {

    private ArrayList<FoodPost> mValues;
    private final OnListFragmentInteractionListener mListener;


    public MyUserPostRecyclerViewAdapter(ArrayList<FoodPost> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        TextView posterUserName = holder.mView.findViewById(R.id.poster_username);
        TextView posterName = holder.mView.findViewById(R.id.poster_name);
        TextView food_name = holder.mView.findViewById(R.id.plate_name);
        TextView descriptionView = holder.mView.findViewById(R.id.description);
        TextView time = holder.mView.findViewById(R.id.time);
        TextView priceView = holder.mView.findViewById(R.id.price);
        ImageView imageView = holder.mView.findViewById(R.id.post_image);
        ConstraintLayout imageLayout = holder.mView.findViewById(R.id.image_layout);
        ConstraintLayout postButton = holder.mView.findViewById(R.id.post_button);
        View cardButton = holder.mView.findViewById(R.id.card_button);

        final FoodPost foodPost = mValues.get(position);

        posterName.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUserName.setText(foodPost.owner.username);
        food_name.setText(foodPost.plate_name);
        String priceTextE = foodPost.price + "€";
        priceView.setText(priceTextE);
        descriptionView.setText(foodPost.description);
        time.setText(foodPost.time);
        priceView.setText(foodPost.price);

        if (!foodPost.food_photo.contains("no-image")) {
            imageLayout.setVisibility(View.VISIBLE);
            Glide.with(holder.mView.getContext()).load(foodPost.food_photo).into(imageView);
        }

        postButton.setOnClickListener(v -> {
            cardButton.animate().alpha(0).setDuration(200).withEndAction(() -> {
                cardButton.animate().alpha(1).setDuration(200).withEndAction(() -> {
                    Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
                    foodLook.putExtra("object", foodPost);
                    foodLook.putExtra("delete", true);
                    holder.mView.getContext().startActivity(foodLook);
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size(): 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView foodNameView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            foodNameView = view.findViewById(R.id.plateName);
        }
    }
}

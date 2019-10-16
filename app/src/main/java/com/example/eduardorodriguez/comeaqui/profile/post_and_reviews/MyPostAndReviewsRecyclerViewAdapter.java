package com.example.eduardorodriguez.comeaqui.profile.post_and_reviews;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;

import java.util.ArrayList;
import java.util.List;

public class MyPostAndReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MyPostAndReviewsRecyclerViewAdapter.ViewHolder> {

    private final List<FoodPostReview> mValues;

    public MyPostAndReviewsRecyclerViewAdapter(List<FoodPostReview> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_postandreviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FoodPostReview foodPost = mValues.get(position);

        holder.postNameView.setText(foodPost.plate_name);
        holder.postPrice.setText(foodPost.price + "€");
        holder.postTime.setText(foodPost.time);
        holder.posterDescriptionView.setText(foodPost.description);

        if (!foodPost.food_photo.contains("no-image")) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.mView.getContext()).load(foodPost.food_photo).into(holder.imageView);
            holder.imageView.setOnClickListener((v) -> {
                Intent imageLook = new Intent(holder.mView.getContext(), ImageLookActivity.class);
                imageLook.putExtra("image_url", foodPost.food_photo);
                holder.mView.getContext().startActivity(imageLook);
            });
        }

        holder.cardButtonView.setOnClickListener(v -> {
            Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
            foodLook.putExtra("object", foodPost);
            holder.mView.getContext().startActivity(foodLook);
        });

        holder.cardButtonView.setOnClickListener(v -> {
            Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
            foodLook.putExtra("object", foodPost);
            holder.mView.getContext().startActivity(foodLook);
        });
        if (foodPost.review != null){
            holder.wholeReview.setVisibility(View.VISIBLE);
            if (!foodPost.review.owner.profile_photo.contains("no-image")) {
                Glide.with(holder.mView.getContext()).load(foodPost.review.owner.profile_photo).into(holder.reviewerImage);
            }
            holder.reviewerName.setText(foodPost.review.owner.username);
            holder.review.setText(foodPost.review.review);
        }
        setTypes(foodPost.type, holder);
        if (null != foodPost.review)
            setStars(holder, foodPost.review.rating);
    }

    void setTypes(String types, ViewHolder holder){
        ImageView[] imageViews = new ImageView[]{
                holder.vegetarian,
                holder.vegan,
                holder.cereal,
                holder.spicy,
                holder.fish,
                holder.meat,
                holder.dairy
        };
        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
        for (ImageView imageView: imageViews){
            imageView.setVisibility(View.GONE);
            imageViewArrayList.add(imageView);
        }
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
                imageViewArrayList.get(i).setVisibility(View.VISIBLE);
            }
        }
    }
    void setStars(ViewHolder holder, float rating){
        ImageView[] starArray = new ImageView[]{
                holder.star0,
                holder.star1,
                holder.star2,
                holder.star3,
                holder.star4
        };

        for (int i = 0; i < starArray.length; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_empty));
        }

        for (int i = 0; i < rating; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_filled));
        }

        double decimal = rating - Math.floor(rating);
        if(decimal < 0.75 && decimal >= 0.25){
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_half_filled));
        } else if (decimal >= 0.75){
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_filled));
        }

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

        ImageView vegetarian;
        ImageView vegan;
        ImageView cereal;
        ImageView spicy;
        ImageView fish;
        ImageView meat;
        ImageView dairy;

        ConstraintLayout wholeReview;
        ImageView reviewerImage;
        TextView reviewerName;
        TextView review;

        ImageView star0;
        ImageView star1;
        ImageView star2;
        ImageView star3;
        ImageView star4;

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

            vegetarian = view.findViewById(R.id.vegetarian);
            vegan = view.findViewById(R.id.vegan);
            cereal = view.findViewById(R.id.cereal);
            spicy = view.findViewById(R.id.spicy);
            fish = view.findViewById(R.id.fish);
            meat = view.findViewById(R.id.meat);
            dairy = view.findViewById(R.id.dairy);

            wholeReview = view.findViewById(R.id.whole_review);
            reviewerImage = view.findViewById(R.id.reviewer_image);
            reviewerName = view.findViewById(R.id.reviewer_name);
            review = view.findViewById(R.id.review);

            star0 = view.findViewById(R.id.star0);
            star1 = view.findViewById(R.id.star1);
            star2 = view.findViewById(R.id.star2);
            star3 = view.findViewById(R.id.star3);
            star4 = view.findViewById(R.id.star4);
        }
    }
}

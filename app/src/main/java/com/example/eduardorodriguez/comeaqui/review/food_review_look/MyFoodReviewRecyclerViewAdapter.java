package com.example.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;

import java.util.List;

public class MyFoodReviewRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodReviewRecyclerViewAdapter.ViewHolder> {

    private final List<ReviewObject> mValues;

    public MyFoodReviewRecyclerViewAdapter(List<ReviewObject> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_foodreview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.reviewerName.setText(holder.mItem.owner.first_name + ", " + holder.mItem.owner.last_name);
        holder.reviewerUsername.setText(holder.mItem.owner.username);
        holder.review.setText(holder.mItem.review);

        if(!holder.mItem.owner.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.reviewerImage);
        }

        if (holder.mItem.answers.size() > 0){
            holder.answerWhole.setVisibility(View.VISIBLE);
            holder.answererName.setText(holder.mItem.answers.get(0).owner.first_name + ", " + holder.mItem.answers.get(0).owner.last_name);
            holder.answererUsername.setText(holder.mItem.answers.get(0).owner.username);
            holder.answer.setText(holder.mItem.answers.get(0).answer);

            if(!holder.mItem.answers.get(0).owner.profile_photo.contains("no-image")) {
                Glide.with(holder.mView.getContext()).load(holder.mItem.answers.get(0).owner.profile_photo).into(holder.answererImage);
            }
        }

        setStars(holder, holder.mItem.rating);

        holder.mView.setOnClickListener(v -> {});
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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView reviewerImage;
        public final ImageView answererImage;
        public final TextView reviewerName;
        public final TextView reviewerUsername;
        public final TextView review;
        public final TextView answererName;
        public final TextView answererUsername;
        public final TextView answer;
        public final LinearLayout answerWhole;
        public ReviewObject mItem;

        public ImageView star0;
        public ImageView star1;
        public ImageView star2;
        public ImageView star3;
        public ImageView star4;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            reviewerImage = view.findViewById(R.id.reviewer_image);
            answererImage = view.findViewById(R.id.reviewer_image_ans);
            reviewerName = view.findViewById(R.id.reviewer_name);
            reviewerUsername = view.findViewById(R.id.reviewer_username);
            review = view.findViewById(R.id.review);
            answererName = view.findViewById(R.id.reviewer_name_ans);
            answererUsername = view.findViewById(R.id.reviewer_username_ans);
            answer = view.findViewById(R.id.answer);
            answerWhole = view.findViewById(R.id.review_answer);

            star0 = view.findViewById(R.id.star0);
            star1 = view.findViewById(R.id.star1);
            star2 = view.findViewById(R.id.star2);
            star3 = view.findViewById(R.id.star3);
            star4 = view.findViewById(R.id.star4);
        }
    }
}

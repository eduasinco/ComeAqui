package com.example.eduardorodriguez.comeaqui.profile.post_and_reviews;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;

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
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.review.owner.first_name);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public FoodPostReview mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.post_review);
            mContentView = view.findViewById(R.id.content);
        }
    }
}

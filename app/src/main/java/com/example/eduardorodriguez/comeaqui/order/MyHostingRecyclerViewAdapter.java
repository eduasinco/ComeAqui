package com.example.eduardorodriguez.comeaqui.order;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.example.eduardorodriguez.comeaqui.order.HostingFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyHostingRecyclerViewAdapter extends RecyclerView.Adapter<MyHostingRecyclerViewAdapter.ViewHolder> {

    private final List<SavedFoodPost> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyHostingRecyclerViewAdapter(List<SavedFoodPost> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_hosting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (!holder.mItem.plate_name.isEmpty()){
            holder.mealTitle.setText(holder.mItem.plate_name);
        }
        if (!holder.mItem.time_to_show.isEmpty()){
            holder.mealTime.setText(holder.mItem.time_to_show);
            holder.mealTitle.setVisibility(View.VISIBLE);
        }

        if (checkIfInfoMissing(holder.mItem)){
            holder.notCompletedMessage.setVisibility(View.VISIBLE);
        } else if (!holder.mItem.visible){
            holder.notCompletedMessage.setVisibility(View.VISIBLE);
            holder.notCompletedMessage.setText("Meal not posted yet");
        }

        if (holder.mItem.images.size() > 0){
            Glide.with(holder.mView.getContext()).load(holder.mItem.images.get(0).image).into(holder.postImage);
        } else {
            holder.postImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.empty_plate));
        }

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                if (holder.mItem.visible){
                    mListener.goToPostLook(holder.mItem);
                } else {
                    mListener.goToPostEdit(holder.mItem);
                }
            }
        });

        if (holder.mItem.time_to_show != null){
            holder.mealTime.setVisibility(View.VISIBLE);
            holder.mealTime.setText(holder.mItem.time_to_show);
        }
    }

    boolean checkIfInfoMissing(FoodPost foodPost){
        return
                foodPost.max_dinners == 0 ||
                foodPost.start_time.isEmpty() ||
                foodPost.end_time.isEmpty() ||
                foodPost.formatted_address.isEmpty();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView postImage;
        public final TextView mealTitle;
        public final TextView mealTime;
        public final TextView notCompletedMessage;
        public FoodPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            postImage = view.findViewById(R.id.post_image);
            mealTitle = view.findViewById(R.id.title);
            mealTime = view.findViewById(R.id.meal_time);
            notCompletedMessage = view.findViewById(R.id.not_completed_message);
        }
    }
}

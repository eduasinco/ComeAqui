package com.example.eduardorodriguez.comeaqui.order;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.order.HostingFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.order.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHostingRecyclerViewAdapter extends RecyclerView.Adapter<MyHostingRecyclerViewAdapter.ViewHolder> {

    private final List<FoodPost> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyHostingRecyclerViewAdapter(List<FoodPost> items, OnListFragmentInteractionListener listener) {
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
        }

        if (checkIfInfoMissing(holder.mItem)){
            holder.notCompletedMessage.setVisibility(View.VISIBLE);
        }

        if (holder.mItem.images.size() > 0){
            Glide.with(holder.mView.getContext()).load(holder.mItem.images.get(0).image).into(holder.postImage);
        }

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                if (checkIfInfoMissing(holder.mItem)){
                    mListener.goToPostEdit(holder.mItem);
                } else {
                    mListener.goToPostLook(holder.mItem);
                }
            }
        });
    }

    boolean checkIfInfoMissing(FoodPost foodPost){
        return foodPost.start_time.isEmpty() || foodPost.end_time.isEmpty() || foodPost.address.isEmpty() || foodPost.address.isEmpty() || foodPost.price.isEmpty();
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

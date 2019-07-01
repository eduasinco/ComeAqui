package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
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

import static com.example.eduardorodriguez.comeaqui.order.GetFoodAdapter.setTypes;

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
                .inflate(R.layout.getfood_list_element, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.foodNameView.setText(mValues.get(position).plate_name);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        final TextView food_name = holder.mView.findViewById(R.id.plateName);
        TextView priceView = holder.mView.findViewById(R.id.priceText);
        TextView descriptionView = holder.mView.findViewById(R.id.orderMessage);
        ImageView imageView = holder.mView.findViewById(R.id.orderImage);

        final FoodPost userPostObject = mValues.get(position);

        food_name.setText(userPostObject.plate_name);
        String priceTextE = userPostObject.price + "€";
        priceView.setText(priceTextE);
        setTypes(holder.mView, userPostObject.type);
        descriptionView.setText(userPostObject.description);

        if (!userPostObject.food_photo.contains("no-image"))
            Glide.with(holder.mView.getContext()).load(userPostObject.food_photo).into(imageView);

        ConstraintLayout item = holder.mView.findViewById(R.id.listItem);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
                foodLook.putExtra("object", userPostObject);
                foodLook.putExtra("delete", true);
                holder.mView.getContext().startActivity(foodLook);
            }

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

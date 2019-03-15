package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.UserPostFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

import static com.example.eduardorodriguez.comeaqui.GetFoodAdapter.setTypes;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUserPostRecyclerViewAdapter extends RecyclerView.Adapter<MyUserPostRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String[]> mValues;
    private final OnListFragmentInteractionListener mListener;


    public MyUserPostRecyclerViewAdapter(ArrayList<String[]> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addNewRow(ArrayList<String[]> data){
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
        holder.foodNameView.setText(mValues.get(position)[0]);
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

        final TextView food_name = holder.mView.findViewById(R.id.foodName);
        TextView priceView = holder.mView.findViewById(R.id.price);
        TextView descriptionView = holder.mView.findViewById(R.id.description);
        ImageView imageView = holder.mView.findViewById(R.id.foodImage);

        String nameText = mValues.get(position)[0];
        String priceText = mValues.get(position)[1];
        final String typesText = mValues.get(position)[2];
        final String descriptionText = mValues.get(position)[3];
        String pathText = mValues.get(position)[4];

        food_name.setText(nameText);
        String priceTextE = priceText + "€";
        priceView.setText(priceTextE);
        setTypes(holder.mView, typesText);
        descriptionView.setText(descriptionText);

        final StringBuilder path = new StringBuilder();
        path.append("http://127.0.0.1:8000");
        path.append(pathText);


        Glide.with(holder.mView.getContext()).load(path.toString()).into(imageView);

        ConstraintLayout item = holder.mView.findViewById(R.id.listItem);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
                foodLook.putExtra("src", path.toString());
                foodLook.putExtra("name", food_name.getText().toString());
                foodLook.putExtra("des", descriptionText);
                foodLook.putExtra("types", typesText);
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
            foodNameView = view.findViewById(R.id.foodName);
        }
    }
}

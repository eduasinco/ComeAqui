package com.example.eduardorodriguez.comeaqui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.PlacesAutocompleteFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPlacesAutocompleteRecyclerViewAdapter extends RecyclerView.Adapter<MyPlacesAutocompleteRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String[]> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPlacesAutocompleteRecyclerViewAdapter(ArrayList<String[]> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void updateData(ArrayList<String[]> data){
        this.mValues = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_placesautocomplete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.contentView.setText(mValues.get(position)[0]);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.setAddress(holder.contentView.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mValues != null) ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView contentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            contentView = view.findViewById(R.id.content);
        }
    }
}

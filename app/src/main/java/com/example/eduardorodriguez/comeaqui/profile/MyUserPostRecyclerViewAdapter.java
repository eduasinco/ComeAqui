package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.utilities.FoodElementFragment;
import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
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
    Fragment f;


    public MyUserPostRecyclerViewAdapter(ArrayList<FoodPost> items, OnListFragmentInteractionListener listener, Fragment f) {
        mValues = items;
        mListener = listener;
        this.f = f;
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
        ImageView imageView = holder.mView.findViewById(R.id.post_image);
        CardView imageLayout = holder.mView.findViewById(R.id.image_layout);
        FrameLayout postButton = holder.mView.findViewById(R.id.post_button_element);

        final FoodPost foodPost = mValues.get(position);

        if (!foodPost.food_photo.contains("no-image")) {
            imageLayout.setVisibility(View.VISIBLE);
            Glide.with(holder.mView.getContext()).load(holder.mView.getContext().getResources().getString(R.string.server) + foodPost.food_photo).into(imageView);
        }

        //f.getChildFragmentManager().beginTransaction()
        //        .replace(R.id.post_button_element, FoodElementFragment.newInstance(foodPost))
        //        .commit();

        postButton.setOnClickListener(v -> {
            Intent foodLook = new Intent(holder.mView.getContext(), FoodLookActivity.class);
            foodLook.putExtra("object", foodPost);

            if (foodPost.owner.id == MainActivity.user.id){
                foodLook.putExtra("delete", true);
            } else {
                foodLook.putExtra("delete", false);
            }

            holder.mView.getContext().startActivity(foodLook);
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

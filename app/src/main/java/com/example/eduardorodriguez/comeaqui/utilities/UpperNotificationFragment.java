package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;

import java.io.Serializable;


public class UpperNotificationFragment extends Fragment {
    private static final String OBJECT = "type";
    private Serializable object;

    TextView title;
    TextView username;
    TextView plateName;
    TextView time;
    ImageView posterImage;
    CardView cardView;


    public UpperNotificationFragment() {}
    public static UpperNotificationFragment newInstance(Serializable object) {
        UpperNotificationFragment fragment = new UpperNotificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(OBJECT, object);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            object = getArguments().getSerializable(OBJECT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upper_notification, container, false);

        title = view.findViewById(R.id.title);
        username = view.findViewById(R.id.poster_username);
        plateName = view.findViewById(R.id.plate_name);
        time = view.findViewById(R.id.time);
        posterImage = view.findViewById(R.id.poster_image);
        cardView = view.findViewById(R.id.card_view);

        if (object instanceof OrderObject){
            title.setText("Meal with " + ((OrderObject) object).poster.first_name + ((OrderObject) object).poster.last_name);
            username.setText(((OrderObject) object).poster.email);
            plateName.setText(((OrderObject) object).post.plate_name);
            time.setText(((OrderObject) object).post.time);

            if(!((OrderObject) object).poster.profile_photo.contains("no-image")) {
                Glide.with(getActivity()).load(((OrderObject) object).poster.profile_photo).into(posterImage);
            }

            cardView.setOnClickListener(v -> {
                Intent orderLook = new Intent(getContext(), OrderLookActivity.class);
                orderLook.putExtra("object", (OrderObject) object);
                orderLook.putExtra("delete", false);
                getContext().startActivity(orderLook);
            });
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

package com.example.eduardorodriguez.comeaqui;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        final CircularImageView circularImageView = view.findViewById(R.id.profile_image);
        circularImageView.setBorderColor(getResources().getColor(R.color.colorPrimary));
        circularImageView.setBorderWidth(10);

        final ImageView mImage =  view.findViewById(R.id.profile_image);
        AppBarLayout mAppBar = view.findViewById(R.id.app_bar);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mImage.setY(-verticalOffset / 2 + 100);
                circularImageView.setShadowRadius(0 - verticalOffset / 5);
            }
        });

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ListView listview;
        listview = view.findViewById(R.id.fragment_dds_review_listView);

        ViewCompat.setNestedScrollingEnabled(listview, true);
        ArrayList<String[]> data = new ArrayList<String[]>();
        for (int i = 0; i < 100; i++) {
            data.add(new String[]{"h", "h", "h", "h", "f"});
        }
        listview.setAdapter(new GetFoodAdapter(getActivity(), data));

        return view;
    }
}
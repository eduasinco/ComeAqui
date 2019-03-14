package com.example.eduardorodriguez.comeaqui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

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
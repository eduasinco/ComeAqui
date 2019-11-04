package com.example.eduardorodriguez.comeaqui.map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;

public class NoLocationFragmentFragment extends Fragment {
    public NoLocationFragmentFragment() {}

    public static NoLocationFragmentFragment newInstance() {
        return new NoLocationFragmentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_location, container, false);
    }
}

package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;

public class ContinueCancelFragment extends Fragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    private String title;
    private String message;

    Button continueButton;
    Button cancelButton;

    private OnFragmentInteractionListener mListener;

    public ContinueCancelFragment() {}
    public static ContinueCancelFragment newInstance(String title, String message) {
        ContinueCancelFragment fragment = new ContinueCancelFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_continue_cancel, container, false);
        TextView titleView = view.findViewById(R.id.title);
        TextView messageView = view.findViewById(R.id.message);
        continueButton = view.findViewById(R.id.continue_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        titleView.setText(title);
        messageView.setText(message);

        setButtons();
        return view;
    }

    void setButtons(){
        continueButton.setOnClickListener(v -> {
            onButtonPressed(true);
        });
        cancelButton.setOnClickListener(v -> {
            onButtonPressed(false);
        });
    }


    public void onButtonPressed(boolean ok) {
        if (mListener != null) {
            mListener.onFragmentInteraction(ok);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean ok);
    }
}

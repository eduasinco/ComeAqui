package com.example.eduardorodriguez.comeaqui.login_and_register.register;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;


public class EmailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView validationText;
    EditText email;
    Button nextButton;

    public EmailFragment() {}

    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        validationText = view.findViewById(R.id.validation_text);
        email = view.findViewById(R.id.email);
        nextButton = view.findViewById(R.id.next_button);

        setNextButton();
        setEditText(email);
        return view;
    }

    void setNextButton(){
        nextButton.setOnClickListener(v -> {
            if (validateNameSurname()){
                mListener.onEmail(email.getText().toString());
            }
        });
    }

    void setEditText(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
                validationText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    boolean validateNameSurname(){
        boolean valid = true;
        String text = "";
        String target = email.getText().toString();
        if (!(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())){
            text += "Not a valid email \n";
            validationText.setVisibility(View.VISIBLE);
            validationText.setText(text);
            email.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
            valid = false;
        }
        if (!valid){
            validationText.setVisibility(View.VISIBLE);
            validationText.setText(text);
        }
        return valid;
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
        void onEmail(String email);
    }
}

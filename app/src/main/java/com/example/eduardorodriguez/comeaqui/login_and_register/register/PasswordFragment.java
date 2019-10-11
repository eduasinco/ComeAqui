package com.example.eduardorodriguez.comeaqui.login_and_register.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView validationText;
    EditText password;
    Button nextButton;

    public PasswordFragment() {}
    public static PasswordFragment newInstance() {
        return new PasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        validationText = view.findViewById(R.id.validation_text);
        password = view.findViewById(R.id.password);
        nextButton = view.findViewById(R.id.next_button);

        setNextButton();
        setEditText(password);
        return view;
    }

    void setNextButton(){
        nextButton.setOnClickListener(v -> {
            if (validateNameSurname()){
                mListener.onPassword(password.getText().toString());
            }
        });
    }

    void setEditText(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
                validationText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    boolean validateNameSurname(){
        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$%^&+=])(?=\\S+$).{8,}$");
        Matcher m = p.matcher(password.getText().toString());

        boolean valid = true;
        String text = "";
        if (!m.find()){
            text += "A digit must occur at least once \n" +
                    "a lower case letter must occur at least once \n" +
                    "an upper case letter must occur at least once \n" +
                    "a special character (!?@#$%^&+=) must occur at least once \n" +
                    "no whitespace allowed in the entire string \n";
            password.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
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
        void onPassword(String password);
    }
}

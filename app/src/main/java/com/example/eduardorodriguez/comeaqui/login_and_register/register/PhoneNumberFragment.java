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
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class PhoneNumberFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView validationText;
    EditText phone;
    Button nextButton;

    CountryCodePicker ccp;

    public PhoneNumberFragment() {}

    public static PhoneNumberFragment newInstance() {
        return new PhoneNumberFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number, container, false);
        validationText = view.findViewById(R.id.validation_text);
        phone = view.findViewById(R.id.phone_number_edt);
        nextButton = view.findViewById(R.id.next_button);
        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);

        setNextButton();
        setEditText(phone);
        return view;
    }

    void setNextButton(){
        nextButton.setOnClickListener(v -> {
            if (validateNameSurname()){
                mListener.onPhoneNumber(ccp.getFullNumberWithPlus(), phone.getText().toString());
            }
        });
    }

    void setEditText(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
                validationText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    boolean validateNameSurname(){
        boolean valid = true;
        String text = "";
        if (phone.getText().toString() == null || phone.getText().toString().trim().equals("")){
            text += "Please, insert a phone number \n";
            phone.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
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
        void onPhoneNumber(String fullNumber, String number);
    }
}

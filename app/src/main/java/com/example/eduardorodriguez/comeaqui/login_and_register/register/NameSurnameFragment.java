package com.example.eduardorodriguez.comeaqui.login_and_register.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;

import org.w3c.dom.Text;


public class NameSurnameFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView validationText;
    EditText name;
    EditText surname;
    Button nextButton;

    public NameSurnameFragment() {}
    public static NameSurnameFragment newInstance() {
        return new NameSurnameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name_surname, container, false);
        validationText = view.findViewById(R.id.validation_text);
        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        nextButton = view.findViewById(R.id.next_button);

        setNextButton();

        setEditText(name);
        setEditText(surname);
        return view;
    }

    void setNextButton(){
        nextButton.setOnClickListener(v -> {
            if (validateNameSurname()){
                mListener.onNameSurname(name.getText().toString(), surname.getText().toString());
            }
        });
    }

    void setEditText(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    boolean validateNameSurname(){
        boolean valid = true;
        String text = "";
        if (TextUtils.isEmpty(name.getText().toString())){
            text += "Please, insert a name \n";
            name.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
            valid = false;
        }

        if (TextUtils.isEmpty(surname.getText().toString())){
            text += "Please, insert a surname \n";
            surname.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
            valid = false;
        }
        validationText.setText(text);
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
        void onNameSurname(String name, String surname);
    }
}

package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_bank_account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.AddFoodActivity;
import com.example.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.example.eduardorodriguez.comeaqui.objects.StripeAccountInfoObject;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;

public class EditBankAccountActivity extends AppCompatActivity {

    ImageView backArrow;
    EditText firstName;
    EditText lastName;
    DatePicker birth;
    EditText ssn;
    EditText website;
    EditText email;
    EditText phone;
    EditText address1;
    EditText address2;
    EditText city;
    EditText state;
    EditText zip;
    EditText country;

    TextView firstNameVal;
    TextView lastNameVal;
    TextView birthVal;
    TextView ssnVal;
    TextView websiteVal;
    TextView emailVal;
    TextView phoneVal;
    TextView address1Val;
    TextView address2Val;
    TextView cityVal;
    TextView stateVal;
    TextView zipVal;
    TextView countryVal;

    CountryCodePicker ccp;
    TextView validationText;
    Button saveButton;
    View progress;

    StripeAccountInfoObject accountInfoObject;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_account);
        backArrow = findViewById(R.id.back_arr);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        birth = findViewById(R.id.date_of_birth);
        ssn = findViewById(R.id.ssn_digits);
        website = findViewById(R.id.website);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address1 = findViewById(R.id.address_line_1);
        address2 = findViewById(R.id.address_line_2);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zip = findViewById(R.id.zip_code);
        country = findViewById(R.id.country);

        firstNameVal = findViewById(R.id.first_name_val_text);
        lastNameVal = findViewById(R.id.last_name_val_text);
        birthVal = findViewById(R.id.date_of_birth_val_text);
        ssnVal = findViewById(R.id.ssn_val_text);
        websiteVal = findViewById(R.id.website_val_text);
        emailVal = findViewById(R.id.email_valtext);
        phoneVal = findViewById(R.id.phone_val_text);
        address1Val = findViewById(R.id.address1_val_text);
        address2Val = findViewById(R.id.address2_val_text);
        cityVal = findViewById(R.id.city_val_text);
        stateVal = findViewById(R.id.state_val_text);
        zipVal = findViewById(R.id.zip_val_text);
        countryVal = findViewById(R.id.country_val_text);

        ccp = findViewById(R.id.ccp);
        validationText = findViewById(R.id.validation_text);
        saveButton = findViewById(R.id.save_button);
        progress = findViewById(R.id.register_progress);

        setEditText(firstName, firstNameVal);
        setEditText(lastName, lastNameVal);
        setEditText(ssn, ssnVal);
        setEditText(website, websiteVal);
        setEditText(email, emailVal);
        setEditText(phone, phoneVal);
        setEditText(address1, address1Val);
        setEditText(address2, address2Val);
        setEditText(city, cityVal);
        setEditText(state, stateVal);
        setEditText(zip, zipVal);
        setEditText(country, countryVal);

        saveButton.setOnClickListener((v) -> {
            register();
        });

        getBankAccountInfo();

        backArrow.setOnClickListener(v -> finish());
    }

    void setInfo(){
        firstName.setText(accountInfoObject.first_name);
        lastName.setText(accountInfoObject.last_name);
        // birth.setText(accountInfoObject.date_of_birth);
        ssn.setText(accountInfoObject.SSN_last_4);
        website.setText(accountInfoObject.identity_document_front);
        email.setText(accountInfoObject.identity_document_back);
        phone.setText(accountInfoObject.business_website);
        address1.setText(accountInfoObject.email);
        address2.setText(accountInfoObject.phone_number);
        city.setText(accountInfoObject.address_line_1);
        state.setText(accountInfoObject.address_line_2);
        zip.setText(accountInfoObject.city);
        country.setText(accountInfoObject.state);
    }

    void getBankAccountInfo(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_stripe_account_info/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                accountInfoObject = new StripeAccountInfoObject(new JsonParser().parse(response).getAsJsonObject());
                setInfo();
            }
            super.onPostExecute(response);
        }
    }

    void register(){
        if (valid()){
            submit();
        }
    }

    void showProgress(boolean show){
        if (show){
            progress.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    void submit(){
        tasks.add(new UploadAsyncTask(getResources().getString(R.string.server) + "/register/").execute(
                new String[]{"first_name", firstName.getText().toString()},
                new String[]{"last_name", lastName.getText().toString()},
                new String[]{"date_of_birth", birth.getMinDate() + ""},
                new String[]{"SSN_last_4", ssn.getText().toString()},
                new String[]{"identity_document_front", ""},
                new String[]{"identity_document_back", ""},
                new String[]{"business_website", website.getText().toString()},
                new String[]{"email", email.getText().toString()},
                new String[]{"phone_number", phone.getText().toString()},
                new String[]{"address_line_1", address1.getText().toString()},
                new String[]{"address_line_2", address2.getText().toString()},
                new String[]{"city", city.getText().toString()},
                new String[]{"state", state.getText().toString()},
                new String[]{"zip_code", zip.getText().toString()},
                new String[]{"country", country.getText().toString()}
        ));
    }
    private class UploadAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public UploadAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            showProgress(false);
            finish();
            super.onPostExecute(response);
        }
    }
    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }

    void showValtext(TextView tv, String text, EditText et){
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        et.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
    }

    boolean valid(){
        boolean valid = true;

        if (TextUtils.isEmpty(firstName.getText().toString())){
            showValtext(firstNameVal, "Please, insert a firstName", firstName);
            valid = false;
        }

        if (TextUtils.isEmpty(lastName.getText().toString())){
            showValtext(lastNameVal, "Please, insert a lastName", lastName);
            valid = false;
        }

        if (TextUtils.isEmpty(ssn.getText().toString())){
            showValtext(ssnVal, "Please, insert a firstName", ssn);
            valid = false;
        }

        if (TextUtils.isEmpty(website.getText().toString())){
            showValtext(websiteVal, "Please, insert a lastName", website);
            valid = false;
        }

        String target = email.getText().toString();
        if (!(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())){
            showValtext(emailVal, "Not a valid email", email);
            valid = false;
        }

        if (TextUtils.isEmpty(phone.getText().toString())){
            showValtext(phoneVal, "Please, insert a lastName", phone);
            valid = false;
        }

        if (TextUtils.isEmpty(address1.getText().toString())){
            showValtext(phoneVal, "Please, insert a lastName", phone);
            valid = false;
        }

        if (TextUtils.isEmpty(city.getText().toString())){
            showValtext(cityVal, "Please, insert a lastName", city);
            valid = false;
        }

        if (TextUtils.isEmpty(state.getText().toString())){
            showValtext(stateVal, "Please, insert a lastName", state);
            valid = false;
        }

        if (TextUtils.isEmpty(zip.getText().toString())){
            showValtext(zipVal, "Please, insert a lastName", zip);
            valid = false;
        }

        if (TextUtils.isEmpty(country.getText().toString())){
            showValtext(countryVal, "Please, insert a lastName", country);
            valid = false;
        }

        if (!valid){
            validationText.setVisibility(View.VISIBLE);
        } else {
            validationText.setVisibility(View.GONE);
        }
        return valid;
    }

    void setEditText(EditText editText, TextView valtext){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
                valtext.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


}
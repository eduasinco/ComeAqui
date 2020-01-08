package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_bank_account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.StripeAccountInfoObject;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    EditText phone;
    EditText address1;
    EditText address2;
    EditText city;
    EditText state;
    EditText zip;
    EditText country;
    EditText routingN;
    EditText accountN;

    TextView firstNameVal;
    TextView lastNameVal;
    TextView birthVal;
    TextView ssnVal;
    LinearLayout ssnProvidedView;
    Button ssnReplaceButton;
    TextView phoneVal;
    TextView address1Val;
    TextView address2Val;
    TextView cityVal;
    TextView stateVal;
    TextView zipVal;
    TextView countryVal;
    TextView routingVal;
    TextView accountVal;

    TextView accountMessage;
    CountryCodePicker ccp;
    TextView validationText;
    Button saveButton;
    View progress;

    boolean replaceSNN = false;
    StripeAccountInfoObject accountInfoObject;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_account);
        backArrow = findViewById(R.id.back_arrow);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        birth = findViewById(R.id.date_of_birth);
        ssn = findViewById(R.id.ssn_digits);
        phone = findViewById(R.id.phone);
        address1 = findViewById(R.id.address_line_1);
        address2 = findViewById(R.id.address_line_2);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zip = findViewById(R.id.zip_code);
        country = findViewById(R.id.country);
        routingN = findViewById(R.id.routing_n);
        accountN = findViewById(R.id.account_n);

        firstNameVal = findViewById(R.id.first_name_val_text);
        lastNameVal = findViewById(R.id.last_name_val_text);
        birthVal = findViewById(R.id.date_of_birth_val_text);
        ssnVal = findViewById(R.id.ssn_val_text);
        phoneVal = findViewById(R.id.phone_val_text);
        address1Val = findViewById(R.id.address1_val_text);
        address2Val = findViewById(R.id.address2_val_text);
        cityVal = findViewById(R.id.city_val_text);
        stateVal = findViewById(R.id.state_val_text);
        zipVal = findViewById(R.id.zip_val_text);
        countryVal = findViewById(R.id.country_val_text);
        routingVal = findViewById(R.id.routing_n_val_text);
        accountVal = findViewById(R.id.account_n_val_text);

        ccp = findViewById(R.id.ccp);
        validationText = findViewById(R.id.validation_text);
        saveButton = findViewById(R.id.save_button);
        progress = findViewById(R.id.register_progress);
        accountMessage = findViewById(R.id.account_message);

        ssnProvidedView = findViewById(R.id.ssn_provided);
        ssnReplaceButton = findViewById(R.id.replace_ssn);

        setEditText(firstName, firstNameVal);
        setEditText(lastName, lastNameVal);
        setEditText(ssn, ssnVal);
        setEditText(phone, phoneVal);
        setEditText(address1, address1Val);
        setEditText(address2, address2Val);
        setEditText(city, cityVal);
        setEditText(state, stateVal);
        setEditText(zip, zipVal);
        setEditText(country, countryVal);
        setEditText(routingN, routingVal);
        setEditText(accountN, accountVal);

        saveButton.setOnClickListener((v) -> {
            submit("PATCH");
        });

        getBankAccountInfo();

        backArrow.setOnClickListener(v -> finish());
    }

    void setInfo(){
        if (accountInfoObject.individual.first_name != null && !accountInfoObject.individual.first_name.isEmpty()) {
            firstName.setHint(accountInfoObject.individual.first_name);
            firstName.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.last_name != null && !accountInfoObject.individual.last_name.isEmpty()) {
            lastName.setHint(accountInfoObject.individual.last_name);
            lastName.setHintTextColor(Color.BLACK);
        }
        birth.updateDate(accountInfoObject.individual.dob.year, accountInfoObject.individual.dob.month - 1, accountInfoObject.individual.dob.day);
        if (accountInfoObject.individual.ssn_last_4_provided){
            ssnProvidedView.setVisibility(View.VISIBLE);
            ssnReplaceButton.setOnClickListener(v -> {
                replaceSNN = true;
                ssn.setVisibility(View.VISIBLE);
                ssnProvidedView.setVisibility(View.INVISIBLE);
            });
            ssn.setVisibility(View.INVISIBLE);
        }
        if (accountInfoObject.individual.phone != null && !accountInfoObject.individual.phone.isEmpty()) {
            phone.setHint(accountInfoObject.individual.phone);
            phone.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.address.line1 != null && !accountInfoObject.individual.address.line1.isEmpty()) {
            address1.setHint(accountInfoObject.individual.address.line1);
            address1.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.address.line2 != null && !accountInfoObject.individual.address.line2.isEmpty()) {
            address2.setHint(accountInfoObject.individual.address.line2);
            address2.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.address.city != null && !accountInfoObject.individual.address.city.isEmpty()) {
            city.setHint(accountInfoObject.individual.address.city);
            city.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.address.state != null && !accountInfoObject.individual.address.state.isEmpty()) {
            state.setHint(accountInfoObject.individual.address.state);
            state.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.address.postal_code != null && !accountInfoObject.individual.address.postal_code.isEmpty()) {
            zip.setHint(accountInfoObject.individual.address.postal_code);
            zip.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.individual.address.country != null && !accountInfoObject.individual.address.country.isEmpty()) {
            country.setHint(accountInfoObject.individual.address.country);
            country.setHintTextColor(Color.BLACK);
        }
        if (accountInfoObject.external_accounts.data.size() > 0){
            routingN.setHint(accountInfoObject.external_accounts.data.get(0).routing_number);
            routingN.setHintTextColor(Color.BLACK);
            accountN.setHint("Ending " + accountInfoObject.external_accounts.data.get(0).last4);
            accountN.setHintTextColor(Color.BLACK);
        } else {
            showValtext(routingVal, "Please, provide a routing number", routingN);
            showValtext(accountVal, "Please, provide a bank account number", accountN);
        }

        if (accountInfoObject.payouts_enabled){
            accountMessage.setText("Account verified");
            accountMessage.setBackground(ContextCompat.getDrawable(this, R.color.success));
        } else if (accountInfoObject.requirements.currently_due.size() == 0){
            accountMessage.setText("Pending review");
            accountMessage.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimary));
        } else {
            accountMessage.setText("Account incomplete");
            accountMessage.setBackground(ContextCompat.getDrawable(this, R.color.canceled));
        }
        accountMessage.setVisibility(View.VISIBLE);

        for (String due: accountInfoObject.requirements.currently_due){
            switch (due){
                case "individual.address.line1":
                    showValtext(address1Val, "Please, insert a valid address", address1);
                    break;
                case "individual.address.line2":
                    showValtext(address2Val, "Please, insert a valid address", address2);
                    break;
                case "individual.address.country":
                    showValtext(countryVal, "Please, insert a country", country);
                    break;
                case "individual.address.city":
                    showValtext(cityVal, "Please, insert a city", city);
                    break;
                case "individual.address.postal_code":
                    showValtext(zipVal, "Please, insert a postal code", zip);
                    break;
                case "individual.address.state":
                    showValtext(stateVal, "Please, insert a state", state);
                    break;
                case "individual.dob.day":
                    break;
                case "individual.dob.month":
                    break;
                case "individual.dob.year":
                    break;
                case "individual.first_name":
                    showValtext(firstNameVal, "Please, insert a first name", firstName);
                    break;
                case "individual.last_name":
                    showValtext(lastNameVal, "Please, insert a last name", lastName);
                    break;
                case "individual.phone":
                    showValtext(phoneVal, "Please, insert a phone", phone);
                    break;
                case "individual.ssn_last_4":
                    showValtext(ssnVal, "Please, insert last 4 digits of your SSN", ssn);
                    break;
            }
        }

    }
    void getBankAccountInfo(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/stripe_account/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
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
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    accountInfoObject = new StripeAccountInfoObject(jo);
                    setInfo();
                } else {
                    submit("POST");
                }
            }
            showProgress(false);
            super.onPostExecute(response);
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

    void submit(String method){
        tasks.add(new UploadAsyncTask(method,getResources().getString(R.string.server) + "/stripe_account/").execute(
                new String[]{"first_name", firstName.getText().toString()},
                new String[]{"last_name", lastName.getText().toString()},
                new String[]{"day", birth.getDayOfMonth() + ""},
                new String[]{"month", (birth.getMonth() + 1) + ""},
                new String[]{"year", birth.getYear() + ""},
                new String[]{"ssn_last_4", ssn.getText().toString()},
                new String[]{"phone", phone.getText().toString()},
                new String[]{"line1", address1.getText().toString()},
                new String[]{"line2", address2.getText().toString()},
                new String[]{"city", city.getText().toString()},
                new String[]{"state", state.getText().toString()},
                new String[]{"postal_code", zip.getText().toString()},
                new String[]{"country", country.getText().toString()},
                new String[]{"routing_number", routingN.getText().toString()},
                new String[]{"account_number", accountN.getText().toString()}
        ));
    }
    private class UploadAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        String method;
        public UploadAsyncTask(String method, String uri){
            this.method = method;
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
                return ServerAPI.upload(getApplicationContext(), method, this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    Toast.makeText(getApplication(), "Account saved", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplication(), jo.get("error_message").getAsString(), Toast.LENGTH_LONG).show();
                }
            }
            showProgress(false);
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
            public void afterTextChanged(Editable s) {}
        });
    }


}
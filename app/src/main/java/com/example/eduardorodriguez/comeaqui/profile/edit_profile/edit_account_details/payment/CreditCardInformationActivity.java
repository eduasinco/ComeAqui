package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;

import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CreditCardInformationActivity extends AppCompatActivity {

    EditText creditCardView;
    EditText expiryDateView;
    EditText cvvView;
    String cardType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_information);

        creditCardView = findViewById(R.id.creditCard);
        expiryDateView = findViewById(R.id.expiryDate);
        cvvView = findViewById(R.id.cvv);
        Button saveCardButtonView = findViewById(R.id.saveCardButton);

        saveCardButtonView.setOnClickListener(v -> {
            PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/card/");
            post.execute(
                        new String[]{"card_number", creditCardView.getText().toString(), ""},
                        new String[]{"expiration_date", expiryDateView.getText().toString(), ""},
                        new String[]{"card_type", cardType, ""},
                        new String[]{"cvv", cvvView.getText().toString(), ""}
                        );
        });
    }

    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
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
            Intent back = new Intent(CreditCardInformationActivity.this, PaymentMethodsActivity.class);
            startActivity(back);
            super.onPostExecute(response);
        }
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, 10);
    }

    /**
     * Formatting a credit card number: #### #### #### #######
     */
    public static class CreditCardNumberFormattingTextWatcher implements TextWatcher {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 16) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                creditCardView.setText(scanResult.getRedactedCardNumber());
                cardType = (scanResult.getCardType() != null) ? scanResult.getCardType() + "" : " ";

                resultDisplayStr += "Card Type: " + scanResult.getCardType() + "\n";
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    String exYear = Integer.toString(scanResult.expiryYear);
                    creditCardView.setText(scanResult.expiryMonth + "/" + exYear.substring(2));
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    cvvView.setText(scanResult.cvv);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }
}

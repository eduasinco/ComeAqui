package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.profile.payment.AddPaymentMethodActivity;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonParser;
import com.hbb20.CountryCodePicker;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.util.concurrent.ExecutionException;

public class CreditCardInformationActivity extends AppCompatActivity {

    EditText creditCardView;
    EditText expiryDateView;
    EditText cvvView;
    EditText zipCodeView;
    String cardType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_information);

        creditCardView = findViewById(R.id.creditCard);
        expiryDateView = findViewById(R.id.expiryDate);
        cvvView = findViewById(R.id.cvv);
        zipCodeView = findViewById(R.id.zipCode);
        final CountryCodePicker countryView = findViewById(R.id.ccp);
        Button saveCardButtonView = findViewById(R.id.saveCardButton);

        saveCardButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/card/");
                try {
                    String response = post.execute(
                            new String[]{"card_number", creditCardView.getText().toString(), ""},
                            new String[]{"expiration_date", expiryDateView.getText().toString(), ""},
                            new String[]{"card_type", cardType, ""},
                            new String[]{"cvv", cvvView.getText().toString(), ""},
                            new String[]{"zip_code", zipCodeView.getText().toString(), ""},
                            new String[]{"country", countryView.getDefaultCountryName(), ""}
                    ).get();
                    new JsonParser().parse(response).getAsJsonObject();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Intent back = new Intent(CreditCardInformationActivity.this, AddPaymentMethodActivity.class);
                startActivity(back);
            }
        });

        creditCardView.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());
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
                    zipCodeView.setText(scanResult.postalCode);
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }
}
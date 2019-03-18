package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.hbb20.CountryCodePicker;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class CardInformationActivity extends AppCompatActivity {

    EditText creditCardView;
    EditText expiryDateView;
    EditText cvvView;
    EditText zipCodeView;
    String cardType;

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
                PostAsyncTask post = new PostAsyncTask("http://127.0.0.1:8000/foods/");
                post.execute(
                        new String[]{"card_number", creditCardView.getText().toString()},
                        new String[]{"expiration_date", expiryDateView.toString()},
                        new String[]{"card_type", cardType},
                        new String[]{"cvv", cvvView.getText().toString()},
                        new String[]{"zip_code", zipCodeView.getText().toString()},
                        new String[]{"country", countryView.getDefaultCountryName()}
                );
                Intent back = new Intent(CardInformationActivity.this, AddPaymentMethodActivity.class);
                startActivity(back);
            }
        });

        creditCardView.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());
        expiryDateView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 1 && count == 2 && s.charAt(s.length()-1) != '/') {
                    expiryDateView.setText(expiryDateView.getText().toString() + "/");
                }
                if (expiryDateView.getText().toString().toCharArray().length < 3) {
                    expiryDateView.setText(expiryDateView.getText().toString().replace("/", ""));
                }
            }
        });
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
                cardType = scanResult.getCardType().toString();

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

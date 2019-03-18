package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.util.ArrayList;

public class FoodLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_look);

        ImageView image = findViewById(R.id.foodLookImage);
        TextView plateNameView = findViewById(R.id.name);
        TextView descriptionView = findViewById(R.id.descriptionId);
        Button addButtonView = findViewById(R.id.addButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            String path = b.getString("src");
            String name = b.getString("name");
            String description = b.getString("des");
            String types = b.getString("types");
            boolean delete = b.getBoolean("delete");
            plateNameView.setText(name);
            descriptionView.setText(description);
            Glide.with(this).load(path).into(image);

            ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
            imageViewArrayList.add((ImageView) findViewById(R.id.vegetarian));
            imageViewArrayList.add((ImageView) findViewById(R.id.vegan));
            imageViewArrayList.add((ImageView) findViewById(R.id.celiac));
            imageViewArrayList.add((ImageView) findViewById(R.id.spicy));
            imageViewArrayList.add((ImageView) findViewById(R.id.fish));
            imageViewArrayList.add((ImageView) findViewById(R.id.meat));
            imageViewArrayList.add((ImageView) findViewById(R.id.dairy));
            int[] resources = new int[]{
                    R.drawable.vegetarianfill,
                    R.drawable.veganfill,
                    R.drawable.cerealfill,
                    R.drawable.spicyfill,
                    R.drawable.fishfill,
                    R.drawable.meatfill,
                    R.drawable.dairyfill,
            };

            for (int i = 0; i < types.length(); i++){
                if (types.charAt(i) == '1'){
                    imageViewArrayList.get(i).setImageResource(resources[i]);
                }
            }
            if (delete){
                addButtonView.setText("Delete Post");
                addButtonView.setBackgroundColor(Color.parseColor("#FFFF0E01"));
            }
        }
    }
    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, 10);
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

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }
}

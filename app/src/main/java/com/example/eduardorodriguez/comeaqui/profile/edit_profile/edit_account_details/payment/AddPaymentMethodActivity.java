package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;

public class AddPaymentMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);
        ImageView back = findViewById(R.id.back);
        Button addCard = findViewById(R.id.add_card);

        addCard.setOnClickListener(v -> {
            Intent addPaymentMethod = new Intent(this, CreditCardInformationActivity.class);
            startActivity(addPaymentMethod);
        });

        back.setOnClickListener((v) -> finish());
    }
}

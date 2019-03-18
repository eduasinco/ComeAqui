package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.craftman.cardform.Card;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class AddPaymentMethodActivity extends AppCompatActivity {

    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);
        TextView addPaymentView = findViewById(R.id.addPayment);

        addPaymentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent card = new Intent(AddPaymentMethodActivity.this, CardInformationActivity.class);
                startActivity(card);
            }
        });
    }
}

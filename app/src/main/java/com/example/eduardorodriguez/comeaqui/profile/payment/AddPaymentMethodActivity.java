package com.example.eduardorodriguez.comeaqui.profile.payment;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.CardInformationActivity;
import com.example.eduardorodriguez.comeaqui.R;

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

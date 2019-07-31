package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.PaymentObject;

import java.util.ArrayList;

public class PaymentMethodsActivity extends AppCompatActivity {

    PaymentMethodsAdapter adapter;
    ArrayList<PaymentObject> paymentObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        LinearLayout addPaymentMethod = findViewById(R.id.add_payment_method);
        ImageView back = findViewById(R.id.back);

        RecyclerView recyclerView = findViewById(R.id.payment_methods_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentMethodsAdapter(this, paymentObjects);
        recyclerView.setAdapter(adapter);


        addPaymentMethod.setOnClickListener(v -> {

        });

        back.setOnClickListener((v) -> finish());
    }
}

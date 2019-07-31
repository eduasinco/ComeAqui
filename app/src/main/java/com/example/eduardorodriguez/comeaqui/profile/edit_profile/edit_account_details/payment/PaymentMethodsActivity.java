package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.ChatObject;
import com.example.eduardorodriguez.comeaqui.objects.PaymentObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

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
            Intent addPaymentMethodA = new Intent(this, AddPaymentMethodActivity.class);
            startActivity(addPaymentMethodA);
        });


        getCardPaymentMethods();
        back.setOnClickListener((v) -> finish());
    }

    void getCardPaymentMethods(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_cards/");
        try {
            String response = process.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void makeList(JsonArray jsonArray){
        try {
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                adapter.addPaymentMethod(new PaymentObject(jo));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

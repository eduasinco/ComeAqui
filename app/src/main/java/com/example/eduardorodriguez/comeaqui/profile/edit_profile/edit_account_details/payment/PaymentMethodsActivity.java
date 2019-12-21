package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.PaymentMethodObject;

import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class PaymentMethodsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    PaymentMethodsAdapter adapter;
    ArrayList<PaymentMethodObject> data;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        LinearLayout addPaymentMethod = findViewById(R.id.add_payment_method);
        ImageView back = findViewById(R.id.back_arrow);

        recyclerView = findViewById(R.id.payment_methods_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentMethodsAdapter(this, data);
        recyclerView.setAdapter(adapter);


        addPaymentMethod.setOnClickListener(v -> {
            Intent addPaymentMethodA = new Intent(this, AddPaymentMethodActivity.class);
            startActivity(addPaymentMethodA);
        });


        getCardPaymentMethods();
        back.setOnClickListener((v) -> finish());
    }

    void getCardPaymentMethods(){
        GetAsyncTask process = new GetAsyncTask(getResources().getString(R.string.server) + "/my_payment_methods/");
        tasks.add(process.execute());
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
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
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
            super.onPostExecute(response);
        }
    }

    void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new PaymentMethodObject(jo));
            }
            adapter = new PaymentMethodsAdapter(this, data);
            recyclerView.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void onPaymentMethodClicked(PaymentMethodObject paymentMethodObject){
        patchPayment(paymentMethodObject.id);
    }
    void patchPayment(int paymentMethodId){
        tasks.add(new PatchAsyncTask(getResources().getString(R.string.server) + "/select_as_payment_method/" + paymentMethodId + "/").execute());
    }
    private class PatchAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PatchAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "PATCH", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            getCardPaymentMethods();
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
}

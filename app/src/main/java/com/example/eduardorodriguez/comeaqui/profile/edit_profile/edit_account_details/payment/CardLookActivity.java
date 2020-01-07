package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.AddFoodActivity;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.example.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class CardLookActivity extends AppCompatActivity {

    TextView cardNumber;
    TextView expiryDate;
    ImageButton options;

    int cardId;
    PaymentMethodObject card;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_look);

        cardNumber = findViewById(R.id.card_number);
        expiryDate = findViewById(R.id.expiry_date);
        options = findViewById(R.id.options);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("cardId") != null) {
            cardId = b.getInt("cardId");
            getCard();
        }

        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("Delete");

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
    }

    void setView(){
        cardNumber.setText("****" + card.card_number.substring(card.card_number.length() - 4));
        expiryDate.setText(card.exp_month + "/" + card.exp_year);
    }

    void setOptionsActions(String title){
        switch (title){
            case "Delete":
                deleteCard();
                break;
        }
    }

    void deleteCard(){
        tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/card_detail/" + cardId + "/").execute());
    }

    class DeletePostAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public DeletePostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.delete(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                finish();
            }
            super.onPostExecute(response);
        }
    }

    void getCard(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/card_detail/" + cardId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
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
            if (response != null){
                card = new PaymentMethodObject(new JsonParser().parse(response).getAsJsonObject());
                setView();
            }
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

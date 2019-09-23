package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NotificationsFragment extends Fragment {

    ArrayList<NotificationObject> data;
    static MyNotificationsRecyclerViewAdapter notificationAdapter;
    ColorDrawable swipeBackgroundConfirm = new ColorDrawable(Color.parseColor("#FF4CAF50"));
    ColorDrawable swipeBackgroundCancel= new ColorDrawable(Color.parseColor("#FF0000"));
    Drawable deleteIcon;
    Drawable confirmIcon;

    RecyclerView recyclerView;
    SwipeController swipeController = null;


    public NotificationsFragment() {
    }

    public void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new NotificationObject(jo));
            }
            notificationAdapter.addNewRow(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        notificationAdapter = new MyNotificationsRecyclerViewAdapter(getContext(), data);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view;

        getData();
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(notificationAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(notificationAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                confirmOrder(notificationAdapter.mValues.get(position).order, false, getContext());
            }

            @Override
            public void onLeftClicked(int position) {
                confirmOrder(notificationAdapter.mValues.get(position).order, true, getContext());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    void getData(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_notifications/");
        try {
            String response = process.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void confirmOrder(OrderObject order, boolean confirm, Context context){
        PostAsyncTask orderStatus = new PostAsyncTask(context.getString(R.string.server) + "/set_order_status/");
        order.status = confirm ? "CONFIRMED" : "CANCELED";
        try {
            orderStatus.execute(
                    new String[]{"order_id",  order.id + ""},
                    new String[]{"order_status", order.status}
            ).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        notificationAdapter.notifyDataSetChanged();
    }
}

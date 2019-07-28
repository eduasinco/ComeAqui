package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NotificationsFragment extends Fragment {

    ArrayList<NotificationObject> data;
    static MyNotificationsRecyclerViewAdapter notificationAdapter;
    ColorDrawable swipeBackgroundConfirm = new ColorDrawable(Color.parseColor("#FF4CAF50"));
    ColorDrawable swipeBackgroundCancel= new ColorDrawable(Color.parseColor("#FF0000"));
    Drawable deleteIcon;
    Drawable confirmIcon;

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
        RecyclerView recyclerView = (RecyclerView) view;

        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayout.VERTICAL));
        getData();

        deleteIcon = view.getContext().getDrawable(R.drawable.cancel);
        confirmIcon = view.getContext().getDrawable(R.drawable.confirm);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(notificationAdapter);

        return view;
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

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,   ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


            if (direction == ItemTouchHelper.RIGHT){
                viewHolder.itemView.setAlpha(0);
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFD0FFD2"));
                viewHolder.itemView.animate().alpha(1).x(0).setDuration(200).withEndAction(() -> {
                    confirmOrder(data.get(viewHolder.getAdapterPosition()).order, true, getContext());
                });
            } else {
                viewHolder.itemView.setAlpha(0);
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFD3D2"));
                viewHolder.itemView.animate().alpha(1).x(0).setDuration(200).withEndAction(() -> {
                    confirmOrder(data.get(viewHolder.getAdapterPosition()).order, false, getContext());
                });
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            System.out.println(Math.abs(dX));
            int margin = 200;
            int rise = 300;
            int down = 100;
            int max_margin = 180;
            int start_margin = 300;
            if (rise >= Math.abs(dX) && Math.abs(dX) > 0){
                margin = start_margin - (int) (Math.abs(dX) * (start_margin - max_margin)/rise);
            } else if (rise + down > Math.abs(dX) && Math.abs(dX) > rise){
                margin = max_margin + (int) ((Math.abs(dX) - rise) * (margin - max_margin)/down);
            }
            if (dX > 0){
                swipeBackgroundConfirm.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                confirmIcon.setBounds(itemView.getLeft() + iconMargin + margin, itemView.getTop() + iconMargin + margin, itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth() - margin,
                        itemView.getBottom() - iconMargin - margin);
                swipeBackgroundConfirm.draw(c);
            } else {
                swipeBackgroundCancel.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteIcon.setBounds(itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth() + margin, itemView.getTop() + iconMargin + margin, itemView.getRight() - iconMargin - margin,
                        itemView.getBottom() - iconMargin - margin);
                swipeBackgroundCancel.draw(c);

            }
            c.save();
            if (dX > 0){
                c.clipRect(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                confirmIcon.draw(c);
            } else {
                c.clipRect(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteIcon.draw(c);
            }
            c.restore();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}

package com.example.eduardorodriguez.comeaqui;

import android.app.Notification;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.eduardorodriguez.comeaqui.App.CHANNEL_1_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManagerCompat notificationManager;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
        // sendRegistrationToServer(token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        notificationManager = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("My notification")
                .setContentText(remoteMessage.getData().get("message"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);

        System.out.println("HOLAAAAAAAA: " + remoteMessage.getData().get("message"));
        super.onMessageReceived(remoteMessage);
    }
}

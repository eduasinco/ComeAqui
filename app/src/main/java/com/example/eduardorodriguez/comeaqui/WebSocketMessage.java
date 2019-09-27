package com.example.eduardorodriguez.comeaqui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketMessage {
    public static void send(Activity c, String url, String message){
        try {
            URI uri = new URI(c.getResources().getString(R.string.server) + url);
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    c.runOnUiThread(() -> {
                        send(message);
                    });
                }
                @Override
                public void onMessage(String s) {}
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }
                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

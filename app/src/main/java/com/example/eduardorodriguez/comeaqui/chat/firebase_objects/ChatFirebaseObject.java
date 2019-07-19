package com.example.eduardorodriguez.comeaqui.chat.firebase_objects;

import java.io.Serializable;
import java.util.Map;

public class ChatFirebaseObject implements Serializable {
    public String id;
    public String signature;
    public FirebaseUser user1;
    public FirebaseUser user2;
    public String last_message;
}
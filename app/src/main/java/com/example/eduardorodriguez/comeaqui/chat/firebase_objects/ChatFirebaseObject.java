package com.example.eduardorodriguez.comeaqui.chat.firebase_objects;

import java.io.Serializable;
import java.util.Map;

public class ChatFirebaseObject implements Serializable {
    public Map<String, FirebaseUser> users;
    public String last_message;
}
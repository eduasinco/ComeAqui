package com.example.eduardorodriguez.comeaqui.chat;

import com.google.gson.JsonObject;

public class DateBetweenMessages extends MessageObject {

    public DateBetweenMessages(JsonObject jo) {
        super(jo);
        super.message = jo.get("message").getAsString();
    }
}
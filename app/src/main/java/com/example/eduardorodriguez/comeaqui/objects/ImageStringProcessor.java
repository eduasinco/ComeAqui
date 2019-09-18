package com.example.eduardorodriguez.comeaqui.objects;

class ImageStringProcessor {

    public static String server = "http://127.0.0.1:8000";

    static String processString(String imageString){

        if (imageString == null) {
            return "";
        }
        if (!imageString.contains("http")) {
            String s = server + imageString;
            return s;
        }
        return imageString;
    }
}

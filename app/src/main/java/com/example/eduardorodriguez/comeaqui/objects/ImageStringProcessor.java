package com.example.eduardorodriguez.comeaqui.objects;

class ImageStringProcessor {

    public static String server = "http://192.168.0.13:65100";

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

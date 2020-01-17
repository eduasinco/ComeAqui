package com.example.eduardorodriguez.comeaqui.objects;

class ImageStringProcessor {

    public static String server = "http://52.53.228.140";

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

package com.comeaqui.eduardorodriguez.comeaqui.objects;

class ImageStringProcessor {

    public static String server1 = "http://10.0.0.33:65100";
    public static String server2 = "http://54.193.13.44";
    public static String server3 = "http://10.153.30.138:65100";

    static String processString(String imageString){

        if (imageString == null) {
            return "";
        }
        if (!imageString.contains("http")) {
            String s = server3 + imageString;
            return s;
        }
        return imageString;
    }
}

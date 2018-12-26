package com.wzw.demo.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatter {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String doFormat(String date){
        try {
            return simpleDateFormat.format(simpleDateFormat1.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }
}

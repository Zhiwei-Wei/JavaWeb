package com.wzw.demo.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NickNameGenerator {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static Random random = new Random();
    public static String getNickName(){
        return simpleDateFormat.format(new Date())+ random.nextInt(1000);
    }
}

package com.easyjava.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @FileName DataUtils
 * @Description
 * @Author fahrtwind
 * @date 2024-05-18
 **/


public class DateUtils {

    public static final String style1 = "yyyy_MM_dd ";
    public static final String style2 = "yyyy/MM/dd";
    public static final String style3 = "yyyy-MM-dd";
    public static final String style4 = "yyyy-MM-dd HH:mm:ss";

    public static String format(Date date, String patten){
        return new SimpleDateFormat(patten).format(date);

    }

    public static String parse(String date,String patten) throws ParseException {
        new SimpleDateFormat(patten).parse(date);
        return null;
    }

}

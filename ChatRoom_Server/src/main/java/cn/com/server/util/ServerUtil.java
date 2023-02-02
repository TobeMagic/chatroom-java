package cn.com.server.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class ServerUtil {

    public static String getDateInNow() {
        Date date = new Date();// 默认为当前时间
        String dateTime = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA).format(date) + DateFormat.getTimeInstance(DateFormat.FULL, Locale.CHINA).format(date);
        return "\t<" + dateTime + ">";
    }
}

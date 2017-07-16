package me.esca.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Me on 16/07/2017.
 */

public class DateFormatting {

    public static String formatDateTime(String timeToFormat) {

        Date date = new Date(Long.parseLong(timeToFormat));
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);

        return dt1.format(date);
    }
}

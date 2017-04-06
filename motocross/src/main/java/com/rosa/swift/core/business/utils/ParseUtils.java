package com.rosa.swift.core.business.utils;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mteterin on 14.02.2017.
 */

public class ParseUtils {

    private ParseUtils() {
        throw new AssertionError();
    }

    public static String calendarToStringByMask(Calendar calendar, String mask) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mask, Locale.ENGLISH);

        return dateFormat.format(calendar.getTime());
    }

    public static Calendar convertTimeToCalendar(Time date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, date.monthDay);
        calendar.set(Calendar.MONTH, date.month);
        calendar.set(Calendar.YEAR, date.year);
        calendar.set(Calendar.HOUR_OF_DAY, date.hour);
        calendar.set(Calendar.MINUTE, date.minute);

        return calendar;
    }
}
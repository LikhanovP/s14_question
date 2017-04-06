package com.rosa.swift.core.business.utils;

import android.text.TextUtils;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aboyarkin on 01.08.13.
 */
@SuppressWarnings("unused")
public class StringUtils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean canRepresentedAsNumber(String value) {
        return !StringUtils.isNullOrEmpty(value) && isNumber(value.replace(" ", ""));
    }

    public static boolean isNumber(String value) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String Alpha(String input) {
        if (input == null) return "";
        String output = input;
        while (output.length() > 0 &&
                output.startsWith("0")) {
            output = output.substring(1);
        }

        return output;
    }

    public static String representAsNumber(String value) {

        if (StringUtils.isNullOrEmpty(value)) return "0";

        String woSpace = value.replace(" ", "");

        if (isNumber(woSpace)) return woSpace;
        return "0";

    }

    public static String BoolToFlag(boolean bool) {
        return (bool ? "X" : "");
    }

    public static boolean FlagToBool(String flag) {
        return !isNullOrEmpty(flag);
    }

    public static Date SAPDateToDate(String sapDate) {
        Date date = null;
        if (!TextUtils.isEmpty(sapDate)) {
            DateFormat format = new SimpleDateFormat("yyyyMMdd");
            try {
                date = format.parse(sapDate);
            } catch (ParseException e) {
                Log.e("Не удалось преобразовать строку даты", e);
            }
        }
        return date;
    }

    public static Date getDateFromSapDateTime(String date, String time) {
        String sapDateTime = String.format("%1$s %2$s", date, time);

        return getDateFromSapDateTime(sapDateTime);
    }

    public static Date getDateFromSapDateTime(String dateTime) {
        //TODO: ipopov 29.03.2017 пробрасывать Exception на верхние уровни
        Date result = null;
        if (!TextUtils.isEmpty(dateTime)) {
            SimpleDateFormat sapDateTimeFormat = new SimpleDateFormat(
                    Constants.FORMAT_SAP_DATETIME,
                    Locale.ENGLISH);
            try {
                result = sapDateTimeFormat.parse(dateTime);
            } catch (ParseException ex) {
                Log.e("Не удалось преобразовать дату или время формата SAP", ex);
            }
        }
        return result;
    }

    public static String getFormatStringFromDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.ENGLISH).format(date);
    }

    public static Time getTimeFromSapDateTime(String date, String time) {
        Time result = null;
        if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
            try {
                int year = Integer.parseInt(date.substring(0, 4));
                int month = Integer.parseInt(date.substring(4, 6));
                int day = Integer.parseInt(date.substring(6, 8));
                int hours = Integer.parseInt(time.substring(0, 2));
                int minutes = Integer.parseInt(time.substring(2, 4));
                int seconds = Integer.parseInt(time.substring(4, 6));
                result = new Time(Time.getCurrentTimezone());
                result.set(seconds, minutes, hours, day, month - 1, year);
            } catch (Exception ignored) {
                Log.e("Не удалось преобразовать дату или время формата SAP");
                return null;
            }
        }
        return result;
    }

}

package com.rosa.mvp.core.utils;

import org.junit.Test;

import java.util.Calendar;

import static com.rosa.swift.core.business.utils.ParseUtils.calendarToStringByMask;
import static org.junit.Assert.assertTrue;

public class ParseUtilsTest {

    @Test
    public void testCalendarToStringByMask() throws Exception {
        String mask = "dd-MM-yyyy hh:mm";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 1999);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 13);

        String parsedDate = calendarToStringByMask(calendar, mask);

        System.out.println(parsedDate);

        assertTrue("Incorrect parsing",
                "12-01-1999 12:13".equals(parsedDate));
    }
}
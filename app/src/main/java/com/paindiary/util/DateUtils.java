package com.paindiary.util;

import android.provider.Telephony;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static Date addMonth(Date date) {
        return addMonths(date, 1);
    }

    public static Date addMonths(Date date, int count) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        c.add(Calendar.MONTH, count);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

    public static Date subtractMonth(Date date) {
        return subtractMonths(date, 1);
    }

    public static Date subtractMonths(Date date, int count) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        c.add(Calendar.MONTH, count * -1);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

    public static Date addDay(Date date) {
        return addDays(date, 1);
    }

    public static Date addDays(Date date, int count) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        c.add(Calendar.DAY_OF_MONTH, count);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

    public static Date subtractDay(Date date) {
        return subtractDays(date, 1);
    }

    public static Date subtractDays(Date date, int count) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        c.add(Calendar.DAY_OF_MONTH, count * -1);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

    public static Date endOfDay(Date date) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate());
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

    public static Date startOfDay(Date date) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }
}

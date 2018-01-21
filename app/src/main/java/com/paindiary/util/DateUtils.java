package com.paindiary.util;

import android.provider.Telephony;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static Date addMonth(Date date) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        c.add(Calendar.MONTH, 1);
        return new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }
    public static Date subtractMonth(Date date) {
        Calendar c = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        c.add(Calendar.MONTH, -1);
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

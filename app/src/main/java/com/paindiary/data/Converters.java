package com.paindiary.data;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class Converters {

    private static final String ARRAY_MERGE_STRING = "--;--";

    @TypeConverter
    public static Date timestampToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String[] splitStringArray(String value) {
        return value.isEmpty() ? new String[0] : value.split(ARRAY_MERGE_STRING);
    }

    @TypeConverter
    public static String mergeStringArray(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            if (sb.length() > 0)
                sb.append(ARRAY_MERGE_STRING);
            sb.append(s);
        }
        return sb.toString();
    }
}

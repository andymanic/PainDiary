package com.paindiary.util;


import java.util.List;

public class ListUtils {

    public static double avg(List<Integer> values) {
        Integer sum = 0;
        if(!values.isEmpty()) {
            for (Integer value : values) {
                sum += value;
            }
            return sum.doubleValue() / values.size();
        }
        return sum;
    }
}

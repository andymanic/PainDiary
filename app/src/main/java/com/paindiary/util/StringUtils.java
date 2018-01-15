package com.paindiary.util;

import java.util.Arrays;

public class StringUtils {

    public static boolean areSameWords(String s1, String s2) {
        String[] s1Words = s1.split("\\s");
        String[] s2Words = s2.split("\\s");

        if (s1Words.length != s2Words.length)
            return false;

        Arrays.sort(s1Words);
        Arrays.sort(s2Words);

        for (int i = 0; i < s1Words.length; i++)
            if (!s1Words[i].toLowerCase().equals(s2Words[i].toLowerCase()))
                return false;

        return true;
    }
}

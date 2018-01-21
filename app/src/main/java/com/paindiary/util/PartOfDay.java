package com.paindiary.util;


import android.widget.Switch;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum PartOfDay {
    EARLY_MORNING, //3-7
    MORNING, //7-11
    MIDDAY, //11-15
    AFTERNOON, //15-19
    EVENING, //19-23
    NIGHT; //23-3

    @Override
    public String toString(){
        switch(this){

            case EARLY_MORNING:
                return "3-7";
            case MORNING:
                return "7-11";
            case MIDDAY:
                return "11-15";
            case AFTERNOON:
                return "15-19";
            case EVENING:
                return "19-23";
            case NIGHT:
                return "23-3";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static PartOfDay mapHourToPartOfDay (int hour){
        if(hour >= 3 && hour < 7){
            return EARLY_MORNING;
        }else if(hour >= 7 && hour < 11){
            return MORNING;
        }else if(hour >= 11 && hour < 15){
            return MIDDAY;
        }else if(hour >= 15 && hour < 19){
            return AFTERNOON;
        }else if(hour >= 19 && hour < 23){
            return EVENING;
        }else{
            return NIGHT;
        }
    }
    public static PartOfDay mapHourToPartOfDay (Date date){
        return mapHourToPartOfDay(date.getHours());
    }






}








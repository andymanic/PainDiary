package com.paindiary.domain;


import java.util.Date;

public enum PartOfDay {
    EARLY_MORNING(1), //3-7
    MORNING(2), //7-11
    MIDDAY(3), //11-15
    AFTERNOON(4), //15-19
    EVENING(5), //19-23
    NIGHT(6); //23-3

    private final int _value;
    public int getValue() {return _value;}
    PartOfDay(int value){_value = value;}

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








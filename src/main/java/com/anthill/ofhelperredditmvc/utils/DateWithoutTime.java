package com.anthill.ofhelperredditmvc.utils;

import java.util.Calendar;
import java.util.Date;

public class DateWithoutTime {

    public static Date getNowDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}

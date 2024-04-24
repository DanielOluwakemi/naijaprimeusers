package com.app.naijaprimeusers.utils;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class DateConverter {

    //Method to get current timestamp
    public long getCurrentTimestamp() {
        System.out.println("Getting Current Timestamp");
        Date date = new Date();
        return date.getTime();
    }

    //Method to get current day
    public int getCurrentDay(){
        System.out.println("Getting Current Day");
        Calendar cal = Calendar.getInstance();

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    //Method to get current month
    public int getCurrentMonth(){
        System.out.println("Getting Current Month");
        Calendar cal = Calendar.getInstance();

        return cal.get(Calendar.MONTH);
    }

    //Method to get current year
    public int getCurrentYear(){
        System.out.println("Getting Current Year");
        Calendar cal = Calendar.getInstance();

        return cal.get(Calendar.YEAR);
    }

    //Method to get timestamp of start of day
    public long getStartOfDayTimestamp() {
        System.out.println("Getting Start Of Day Timestamp");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //Method to get timestamp of start of month
    public long getStartOfMonthForTimestamp(long timestamp) {
        System.out.println("Getting Start Of Month For Timestamp");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    //Method to get timestamp of start of year
    public long getStartOfYearForTimestamp(long timestamp) {
        System.out.println("Getting Start Of Year For Timestamp");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

}


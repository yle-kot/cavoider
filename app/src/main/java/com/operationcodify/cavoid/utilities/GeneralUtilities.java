package com.operationcodify.cavoid.utilities;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.time.Clock;

public class GeneralUtilities {

    /**
     * Pretty much what the name says. It will return the time until the next occurrence of the `hour`th
     * hour of the day (24 hour format). If the method is called withing one hour of the occurnce of
     * `hour` today, it will return the number of milliseconds until tomorrow's occurence of `hour`.`
     * @param hour Hour of the day (24 hour format)
     * @return Milliseconds until specified hour
     */
    public static long getSecondsUntilHour(int hour){
        return getSecondsUntilHour(hour, DateTime.now());
    }
    public static long getSecondsUntilHour(int hour, DateTime now){
        long delay;
        if (now.getHourOfDay() < (hour -  1)) {
            delay = new Duration(now , now.withTimeAtStartOfDay().plusHours(hour)).getStandardSeconds();
        } else {
            delay = new Duration(now , now.withTimeAtStartOfDay().plusDays(1).plusHours(hour)).getStandardSeconds();
        }
        return delay;
    }
}

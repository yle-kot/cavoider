package com.example.cavoid.utilities;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Utilities {

    public static long getMilliSecondsUntilTime(int hour){
        long delay;
        if (DateTime.now().getHourOfDay() < (hour -  1)) {
            delay = new Duration(DateTime.now() , DateTime.now().withTimeAtStartOfDay().plusHours(hour)).getStandardMinutes();
        } else {
            delay = new Duration(DateTime.now() , DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(hour)).getStandardMinutes();
        }
        return delay;
    }
}

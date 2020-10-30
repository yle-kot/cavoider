package com.operationcodify.cavoid;


import com.operationcodify.cavoid.utilities.GeneralUtilities;

import junit.framework.TestCase;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUtilities {
    @Test
    public void getSecondsUntilHour_LessThanOneHour_ReturnsTomorrowsOccurrence(){
        DateTime fakeNow = new DateTime(2020, 10, 22, 7, 10, 0, 0);
        int expectedSeconds = 89400; // (50min * 60sec) + (24hr * 60min * 60 sec)
        assertEquals(expectedSeconds, GeneralUtilities.getSecondsUntilHour(8, fakeNow));
    }

    @Test
    public void getSecondsUntilHour_GtOneHour_ReturnsNext_Occurrence(){
        DateTime fakeNow = new DateTime(2020, 10, 22, 05, 0, 0, 0);
        int expectedSeconds = 10800; //3 hr * 60 sec * 60
        assertEquals(expectedSeconds, GeneralUtilities.getSecondsUntilHour(8, fakeNow));
    }
}

package com.operationcodify.cavoid;

import com.operationcodify.cavoid.database.Converters;

import org.junit.Test;

import org.joda.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class localDateToStringTest {
    @Test
    public void localDateToString_isCorrect() {
        LocalDate date = new LocalDate(2020,1,3);
        assertEquals("2020-01-03", Converters.localDateToString(date));
    }
}

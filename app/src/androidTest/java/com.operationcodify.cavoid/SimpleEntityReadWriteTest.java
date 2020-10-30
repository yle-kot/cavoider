package com.operationcodify.cavoid;

import android.content.Context;
import android.location.Location;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private LocationDao locationDao;
    private LocationDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, LocationDatabase.class).build();
        locationDao = db.getLocationDao();
    }
    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeAndReadData() {
        PastLocation pastLocation = new PastLocation();
        pastLocation.date = "May";
        pastLocation.fips = "51087";
        pastLocation.wasNotified = false;
        locationDao.insertAll(pastLocation);
        List<PastLocation> things = locationDao.getAll();
        assertEquals(pastLocation.date,things.get(0).date);
    }
}

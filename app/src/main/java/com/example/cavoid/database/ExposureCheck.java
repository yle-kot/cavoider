package com.example.cavoid.database;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExposureCheck {

    public static ArrayList<String> getPastFips(Context context) {
        LocationDatabase locDb = LocationDatabase.getDatabase(context.getApplicationContext());
        LocationDao dao = locDb.getLocationDao();
        LocalDate startDate = DateTime.now().toLocalDate();
        LocalDate[] dateList = new LocalDate[15];
        int arrayIndex = 0;
        for (int i = 1; i <= 14; i++) {
            LocalDate prevDay = startDate.minusDays(i);
            dateList[arrayIndex] = prevDay;
            arrayIndex++;
        }
        List<PastLocation> pastLocations = dao.loadAllByDates(dateList);
        ArrayList<String> pastFips = new ArrayList<String>();
        for (PastLocation location : pastLocations) {
            pastFips.add(location.fips);
        }
        return pastFips;
    }

    public ArrayList<String> fipsToNotify(Context context, ArrayList<String> pastLocations) {
        ArrayList<String> fipsToNotify = new ArrayList<String>();
        for (String location : pastLocations) {
            Repository.getPosTests(context, location, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String percentChange;
                    try {
                        percentChange = response.getString("percent_change_14_days");
                    } catch (JSONException e) {
                        Log.e("fipsToNotify", "Expected percent_change_14_days to be a string" + e.getMessage());
                        percentChange = null;
                    }
                    try {
                        if (Integer.parseInt(percentChange) > 0) {
                            fipsToNotify.add(location);
                        }
                    }
                    catch (NumberFormatException exception) {
                        Log.i("fipsToNotify", "Expected percent_change_14_days to have a integer value");
                    }
                }
            });
        }
        return fipsToNotify;
    }
}

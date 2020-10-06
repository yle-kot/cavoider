package com.example.cavoid.workers;
import com.example.cavoid.api.Utilities;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.utilities.AppNotificationHandler;
import com.example.cavoid.utilities.Utilities;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import java.io.IOException;

public class DailyCovidTrendWorker extends Worker {
    public DailyCovidTrendWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        String lat = data.getString("latitude");
        String lon = data.getString("longitude");
        String fips;
        try {
            fips = Utilities.getCurrentLocationFromFipsCode(lat, lon);
        } catch (IOException e) {
            fips = "-1";
        }
        notifyOfCurrentCovidTrend(getApplicationContext(), fips);
        notifyOfCurrentCovidTrend(getApplicationContext());

        /* Create next instance of the worker, ~12 hours from now! */
        long delay = Utilities.getMilliSecondsUntilHour(8);
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        mWorkManager.enqueue(CovidRequest);

        return Result.success();
    }

    private void notifyOfCurrentCovidTrend(final Context context, final String fips){
        Repository repository = new Repository();
        repository.getPosTests(context, fips, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject data = response;
                String posTests;
                //Saves the positive case number from JSON file to string in application
                try{
                    posTests = data.getString("case_trend_14_days");
                }catch (JSONException e){
                    posTests = "ERR";
                }

                String title = "Daily COVID Trend Alert";
                StringBuilder message = new StringBuilder("COVID is trending ");
                try {
                    message.append(Float.parseFloat(posTests) > 0 ? "upwards" : "downwards");
                }
                catch (NumberFormatException ex) {
                    message.append("flat");
                }
                message.append(" in your area.");
                AppNotificationHandler.deliverNotification(context, title,message.toString());
            }
        });
    }
}

package com.example.cavoid.workers;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.ActiveCases;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;
import com.example.cavoid.utilities.AppNotificationHandler;
import com.example.cavoid.utilities.GeneralUtilities;

import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyCovidTrendUpdateWorker extends Worker {
    public DailyCovidTrendUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        LocationDatabase locDb = LocationDatabase.getDatabase(getApplicationContext());
        LocationDao dao = locDb.getLocationDao();
        LocalDate twoWeeksAgoDate = DateTime.now().minusDays(14).toLocalDate();

        // Cleans database of old records
        LocationDatabase.databaseWriteExecutor.execute(() -> dao.cleanRecordsOlderThan(twoWeeksAgoDate));

        /* Updates the reports for a location every day */
        List<PastLocation> locations = dao.getAll();
        for (PastLocation location : locations){
            Repository.getPosTests(getApplicationContext(), location.fips, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ActiveCases activeCases = new ActiveCases();
                    activeCases.fips=location.fips;
                    try {
                        activeCases.activeCases = response.getInt("active_cases_est");
                    } catch (JSONException e) {
                        activeCases.activeCases = -1;
                    }
                    activeCases.reportDate = LocalDate.now();
                    dao.insertReports(activeCases);
                }
            });
        }

        /* Create next instance of the worker, ~12 hours from now! */
        long delay = GeneralUtilities.getSecondsUntilHour(8);
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendUpdateWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        mWorkManager.enqueue(CovidRequest);

        return Result.success();
    }

    private void notifyOfCurrentCovidTrend(final Context context, final String fips){
        Repository.getPosTests(context, fips, new Response.Listener<JSONObject>() {
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

package com.example.cavoid.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.ActiveCases;
import com.example.cavoid.database.ExposureCheckViewModel;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;
import com.example.cavoid.utilities.AppNotificationHandler;
import com.example.cavoid.utilities.GeneralUtilities;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyCovidTrendUpdateWorker extends Worker {
    private LocationDatabase locDb;
    private LocationDao dao;
    private LocalDate twoWeeksAgoDate;
    private Repository repo;
    private Context context;
    private volatile ArrayList<String> fipsToNotify;
    private volatile int counter;
    private Boolean isDone;

    public DailyCovidTrendUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.locDb = LocationDatabase.getDatabase(getApplicationContext());
        this.dao = locDb.getLocationDao();
        this.twoWeeksAgoDate = DateTime.now().minusDays(14).toLocalDate();
        this.repo = new Repository(getApplicationContext());
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        cleanDatabaseRecordsOlderThan(twoWeeksAgoDate);

        updateCovidReportsForAllLocationsSince(twoWeeksAgoDate);

        ArrayList<String> fipsVisitedLastTwoWeeks = getsFipsVisitedOn(getDatesSince(twoWeeksAgoDate));
        addFipsToNotify(repo, fipsVisitedLastTwoWeeks);

        return Result.success();
    }

    private LocalDate[] getDatesSince(LocalDate date) {
        LocalDate startDate = DateTime.now().toLocalDate().minusDays(1);
        int interval = Days.daysBetween(date, startDate).getDays();
        LocalDate[] dateList = new LocalDate[interval];
        for (int i = 0; i < interval; i++) {
            LocalDate prevDay = startDate.minusDays(i);
            dateList[i] = prevDay;
        }
        return dateList;
    }

    private ArrayList<String> getsFipsVisitedOn(LocalDate[] dateList) {
        List<PastLocation> pastLocations = dao.loadAllByDates(dateList);
        ArrayList<String> pastFips = new ArrayList<String>();
        for (PastLocation location : pastLocations) {
            pastFips.add(location.fips);
        }
        return pastFips;
    }

    public void addFipsToNotify(Repository repo, ArrayList<String> pastFips) {
        for (String fips : pastFips) {
            repo.getPosTests(fips, new Response.Listener<JSONObject>() {
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
                            fipsToNotify.add(fips);
                            synchronized (DailyCovidTrendUpdateWorker.class){
                                counter = counter + 1;
                                if (counter == pastFips.size()) {
                                    finalizeWorker();
                                }
                            }

                        }
                    }
                    catch (NumberFormatException exception) {
                        Log.i("fipsToNotify", "Expected percent_change_14_days to have a integer value");
                    }
                }
            });

        }
    }

    private void finalizeWorker() {
        scheduleNextWorker();
    }

    private void scheduleNextWorker() {
        /* Create next instance of the worker, ~12 hours from now! */
        long delay = GeneralUtilities.getSecondsUntilHour(8);
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendUpdateWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        mWorkManager.enqueue(CovidRequest);
    }

    private void updateCovidReportsForAllLocationsSince(LocalDate date) {
        /* Updates the reports for a location every day */
        List<String> fipsVistedLastTwoWeeks = getsFipsVisitedOn(getDatesSince(date));
        for (String fip : fipsVistedLastTwoWeeks){
            repo.getPosTests(fip, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ActiveCases activeCases = new ActiveCases();
                    activeCases.fips=fip;
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
    }

    private void cleanDatabaseRecordsOlderThan(LocalDate date) {
        // Cleans database of old records
        LocationDatabase.databaseWriteExecutor.execute(() -> dao.cleanRecordsOlderThan(date));
    }

    private void notifyOfCurrentCovidTrend(final Context context, final String fips){
        Repository repo = new Repository(context);
        repo.getPosTests(fips, new Response.Listener<JSONObject>() {
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

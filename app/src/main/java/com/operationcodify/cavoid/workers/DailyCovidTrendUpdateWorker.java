package com.operationcodify.cavoid.workers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.operationcodify.cavoid.R;
import com.operationcodify.cavoid.activities.PastLocationActivity;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.ActiveCases;
import com.operationcodify.cavoid.database.LocationDao;
import com.operationcodify.cavoid.database.LocationDatabase;
import com.operationcodify.cavoid.database.NotifiedLocation;
import com.operationcodify.cavoid.database.PastLocation;

import org.joda.time.DateTime;
import net.danlew.android.joda.JodaTimeAndroid;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * DailyCovidTrendUpdateWorker is a worker that is intended to be run once a day. The worker's
 * responsibilities include cleaning the database of records older than 2 weeks, updating the covid reports
 * for the last two weeks' worth of counties, and finally, checking all counties that have been visited
 * to determine if an exposure warning needs to be created for that county.
 */
public class DailyCovidTrendUpdateWorker extends Worker {
    private LocationDatabase locDb;
    private LocationDao locDao;
    private LocalDate twoWeeksAgoDate;
    private Repository repo;
    private Context context;
    private volatile ArrayList<String> fipsToNotifyList;
    private volatile int counter;
    private Boolean isDone;
    private static final String TAG = DailyCovidTrendUpdateWorker.class.getSimpleName();
    private static final String PAST_LOCATION_CHANNEL_ID = "Past Location";
    private static final int GOTO_PAST_LOCATION_PENDING_INTENT_ID = 259;
    private static final int NOTIFICATION_ID = 2937;

    public DailyCovidTrendUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        JodaTimeAndroid.init(context);
        this.locDb = LocationDatabase.getDatabase(getApplicationContext());
        this.locDao = locDb.getLocationDao();
        this.twoWeeksAgoDate = DateTime.now().minusDays(14).toLocalDate();
        this.repo = new Repository(getApplicationContext());
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        cleanDatabaseRecordsOlderThan(twoWeeksAgoDate);

        updateCovidReportsForAllLocationsSince(twoWeeksAgoDate);

        ArrayList<String> fipsVisitedLastTwoWeeks = getRecentFipsNotNotified();
        createFipsToNotifyList(repo, fipsVisitedLastTwoWeeks);

        return Result.success();
    }

    @NotNull
    private ArrayList<String> getRecentFipsNotNotified() {
        ArrayList<String> fipsVisitedLastTwoWeeks = getsFipsVisitedOn(getDatesSince(twoWeeksAgoDate));
        ArrayList<String> fipsNotified = (ArrayList<String>) locDao.getAllNotifiedFips();
        fipsVisitedLastTwoWeeks.removeAll(fipsNotified);
        return fipsVisitedLastTwoWeeks;
    }

    /**
     * @param date which is used to form a date range (today to date)
     *
     * @return all the dates in this date range
     */
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

    /**
     * @param dateList which contains the dates to search through for saved fips codes
     *
     * @return a list of all the fips codes which were saved on the dates in the dateList
     */
    private ArrayList<String> getsFipsVisitedOn(LocalDate[] dateList) {
        List<PastLocation> pastLocations = locDao.loadAllByDates(dateList);
        ArrayList<String> pastFips = new ArrayList<String>();
        for (PastLocation location : pastLocations) {
            pastFips.add(location.fips);
        }
        return pastFips;
    }

    /**
     * The method gets the covid data from the api to determine if there is a
     * positive covid trend by comparing the weekly average for the past two
     * weeks. If there is a positive trend, then the fips code is saved to
     * notify the user of a rising trend
     *
     * @param repo     the repository which holds the api
     * @param pastFips all the fips codes for the past 14 days
     */
    public void createFipsToNotifyList(Repository repo, ArrayList<String> pastFips) {
        for (String fips : pastFips) {
            repo.getPosTests(fips, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    double week1 = 0;
                    double week2 = 0;
                    String countyName = "";
                    try {
                        week1 = response.getDouble("week_1_rolling_avg_per_100k_people");
                        week2 = response.getDouble("week_2_rolling_avg_per_100k_people");
                        countyName = response.getString("county");
                    } catch (JSONException e) {
                        Log.e(TAG, "Expected percent_change_14_days to be a string" + e.getMessage());
                    }
                    if ((int) Math.round(week2) > (int) Math.round(week1)) {
                        fipsToNotifyList.add(countyName);
                        synchronized (DailyCovidTrendUpdateWorker.class) {
                            counter = counter + 1;
                            if (counter == pastFips.size()) {
                                finalizeWorker();
                            }
                        }

                    }
                }
            });

        }
    }

    private void finalizeWorker() {
        // Create a notification to notify the user that they were exposed to X locations
        // X locations is defined in fipsToNotifyList
        if (fipsToNotifyList.size() > 0)
            createWarningNotificationForFips(fipsToNotifyList);

        scheduleNextWorker();
    }

    private void createWarningNotificationForFips(ArrayList<String> countiesToNotify) {
        String title = "COVID-19 spread in your area";
        String message;
        if (countiesToNotify.size() < 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("You recently visited :\n");
            for (int i = 0; i < countiesToNotify.size(); i++) {
                notifiedLocation = new NotifiedLocation();
                notifiedLocation.fips = countiesToNotify.get(i);
                notifiedLocation.date = DateTime.now().toLocalDate();
                LocationDatabase.databaseWriteExecutor.execute(() -> locDao.insertNotifiedLocations(notifiedLocation));
                sb.append(countiesToNotify.get(i));
            }
            message = sb.toString();
        }
        else {
            // Lots of county, simply open the app plz.
            message = "You recently visited several counties with COVID spread. " +
                    "Open app for more info";
        }

        createNotificationLinkingToPastLocationActivity(title, message);
    }

    private void createNotificationLinkingToPastLocationActivity(String title, String message) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = getPendingIntentTo(PastLocationActivity.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PAST_LOCATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_trend_up)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDeleteIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private PendingIntent getPendingIntentTo(Class<? extends Activity> activity) {
        Intent gotToPastLocationIntent = new Intent(context, activity);

        PendingIntent goToActivityIntent = PendingIntent.getActivity(
                context,
                GOTO_PAST_LOCATION_PENDING_INTENT_ID,
                gotToPastLocationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                                                                    );
        return goToActivityIntent;
    }

    private void scheduleNextWorker() {
        /* Create next instance of the worker, ~12 hours from now! */
//        long delay = GeneralUtilities.getSecondsUntilHour(8);
        long delay = 15 * 60;
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendUpdateWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        mWorkManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, CovidRequest);
    }

    private void updateCovidReportsForAllLocationsSince(LocalDate date) {
        /* Updates the reports for a location every day */
        List<String> fipsVistedLastTwoWeeks = getsFipsVisitedOn(getDatesSince(date));
        for (String fip : fipsVistedLastTwoWeeks) {
            repo.getPosTests(fip, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ActiveCases activeCases = new ActiveCases();
                    activeCases.fips = fip;
                    try {
                        activeCases.activeCases = response.getInt("active_cases_est");
                    } catch (JSONException e) {
                        activeCases.activeCases = -1;
                    }
                    activeCases.reportDate = LocalDate.now();
                    locDao.insertReports(activeCases);
                }
            });
        }
    }

    private void cleanDatabaseRecordsOlderThan(LocalDate date) {
        // Cleans database of old records
        LocationDatabase.databaseWriteExecutor.execute(() -> locDao.cleanRecordsOlderThan(date));
    }
}

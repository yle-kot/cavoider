package com.operationcodify.cavoid.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.operationcodify.cavoid.database.LocationDao;
import com.operationcodify.cavoid.database.LocationDatabase;
import com.operationcodify.cavoid.database.PastLocation;
import com.operationcodify.cavoid.utilities.GeneralUtilities;
import com.operationcodify.cavoid.workers.DailyCovidTrendUpdateWorker;
import com.operationcodify.cavoid.workers.GetWorker;
import com.operationcodify.cavoid.workers.RegularLocationSaveWorker;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    private static final String PRIMARY_CHANNEL_ID = "Priority";
    private static final String PAST_LOCATION_CHANNEL_ID = "Past Location";
    private static final int REQUEST_ACCESS_BACKGROUND_LOCATION_STATE = 227;
    private static final int REQUEST_ACCESS_COARSE_LOCATION_STATE = 228;
    private Intent changeScreenIntent;

    public static final String NIGHT_MODE = "NIGHT_MODE";
    private boolean isNightModeEnabled = false;
    private static LoadingActivity singleton = null;
    public static LoadingActivity getInstance() {

        if(singleton == null)
        {
            singleton = new LoadingActivity();
        }
        return singleton;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        singleton = this;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false);

        createNotificationChannel();
        changeScreenIntent = new Intent(LoadingActivity.this, DashboardActivity.class);

        AlertDialog permAlert;

        showPermission(Manifest.permission.ACCESS_COARSE_LOCATION, "Permission to read location",
                "CAVOIDER is based upon knowing your location in order to let you know if you visit somewhere with high spread",
                REQUEST_ACCESS_COARSE_LOCATION_STATE);

        if(android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.Q) {
            showPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    "Background Location",
                    "The application works by reading your location throughout the day." +
                            " This allows us to notify you even if the app is in the background!",
                    REQUEST_ACCESS_BACKGROUND_LOCATION_STATE);
        }
    }

    private void showPermission(String permission, String explanationTitle, String explanationMessage, int permissionState) {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, permission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                showExplanation(explanationTitle, explanationMessage,
                        permission, permissionState);
            } else {
                requestPermission(permission, permissionState);
            }
        } else {
            Toast.makeText(LoadingActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            changeToMainScreen();
        }
    }


    /*
    This creates a one-time worker. The one time worker is set with an initial delay that is the number
    of milliseconds until the trigger time (currently 8am). The trigger time `delay` will be set
    to 8am today, if the scheduler runs before 7am, or it will run at 8am tomorrow.

    Each WorkRequest will reschedule the next call to 8am(ish) using the same technique
     */



    public void createNotificationChannel() {

        // Create a notification manager object.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Alert",
                            NotificationManager.IMPORTANCE_HIGH);

            NotificationChannel pastLocationChannel = new NotificationChannel
                    (PAST_LOCATION_CHANNEL_ID,
                            "Community Spread Alert",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies the user of a newly found exposure to community spread");
            mNotificationManager.createNotificationChannel(notificationChannel);
            mNotificationManager.createNotificationChannel(pastLocationChannel);
        }
    }

    private void changeToMainScreen(){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q ||
                ActivityCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            createWorkers(GeneralUtilities.getSecondsUntilHour(8));
            startActivity(changeScreenIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoadingActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoadingActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            case REQUEST_ACCESS_BACKGROUND_LOCATION_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoadingActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoadingActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    showQuitAlert();
                }
        }
        changeToMainScreen();
    }

    private void showQuitAlert(){
        new AlertDialog.Builder(LoadingActivity.this)
                .setTitle("We're sorry to hear it")
                .setMessage("Unfortunately, the app is useless without this permission. \n\n" +
                        "You can enable it at any point by going into your settings. The app will not run without it.")
                .setPositiveButton("Close CAVOIDER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create().show();
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    protected void createWorkers(long delay) {
        WorkManager mWorkManager = WorkManager.getInstance(this);
        OneTimeWorkRequest GetRequest = new OneTimeWorkRequest.Builder(GetWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendUpdateWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        PeriodicWorkRequest SaveLocationRequest = new PeriodicWorkRequest.Builder(RegularLocationSaveWorker.class, 15, TimeUnit.MINUTES).build();
        mWorkManager.enqueueUniqueWork(GetWorker.class.getName(), ExistingWorkPolicy.REPLACE, GetRequest);
        mWorkManager.enqueueUniqueWork(DailyCovidTrendUpdateWorker.class.getName(), ExistingWorkPolicy.REPLACE, CovidRequest);
        mWorkManager.enqueueUniquePeriodicWork(RegularLocationSaveWorker.class.getName(), ExistingPeriodicWorkPolicy.REPLACE, SaveLocationRequest);
    }
    private String readLastLocation(LocalDate date[]){
        LocationDatabase locDb = LocationDatabase.getDatabase(getApplicationContext());
        LocationDao dao = locDb.getLocationDao();
        List<PastLocation> record = dao.loadAllByDates(date);
        return record.get(0).fips;
    }
}
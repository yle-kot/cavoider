package com.example.cavoid.activities;
import com.example.cavoid.workers.DailyCovidTrendUpdateWorker;
import com.example.cavoid.workers.RegularLocationSaveWorker;
import com.example.cavoid.workers.GetWorker;
import com.example.cavoid.utilities.GeneralUtilities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;
import com.example.cavoid.utilities.GeneralUtilities;
import com.example.cavoid.workers.DailyCovidTrendUpdateWorker;
import com.example.cavoid.workers.GetWorker;
import com.example.cavoid.workers.RegularLocationSaveWorker;

import org.joda.time.LocalDate;

import java.util.List;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    private static final String PRIMARY_CHANNEL_ID = "Priority";
    private static final int REQUEST_ACCESS_BACKGROUND_LOCATION_STATE = 227;
    private static final int REQUEST_ACCESS_COARSE_LOCATION_STATE = 228;
    private Intent changeScreenIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        changeScreenIntent = new Intent(LoadingActivity.this, MapsActivity.class);

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
                            "Community Spread Alert",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies the user of a newly found exposure to community spread");
            mNotificationManager.createNotificationChannel(notificationChannel);
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
        mWorkManager.enqueue(GetRequest);
        mWorkManager.enqueue(CovidRequest);
        mWorkManager.enqueue(SaveLocationRequest);
    }
    private String readLastLocation(LocalDate date[]){
        LocationDatabase db = Room.databaseBuilder(getApplicationContext(), LocationDatabase.class, "PastLocations").build();
        LocationDao locationDao = db.getLocationDao();
        List<PastLocation> record = locationDao.loadAllByDates(date);
        return record.get(0).fips;
    }
}
package com.example.cavoid.activities;
import com.example.cavoid.database.DatabaseClient;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;
import com.example.cavoid.workers.DailyCovidTrendWorker;
import com.example.cavoid.workers.DatabaseWorker;
import com.example.cavoid.workers.GetWorker;
import com.example.cavoid.utilities.GeneralUtilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback{

    private static final String PRIMARY_CHANNEL_ID = "Priority";
    private Intent changeScreenIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        createWorkers(GeneralUtilities.getSecondsUntilHour(8));

        changeScreenIntent = new Intent(LoadingActivity.this, MapsActivity.class);

        AlertDialog permAlert;
        if (
                ActivityCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_DENIED
                        && ActivityCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED
        ) {
            AlertDialog.Builder permAlertBuilder = new AlertDialog.Builder(LoadingActivity.this);
            permAlertBuilder.setTitle("Why we need your location");
            permAlertBuilder.setMessage("CAVOIDER requires user permissions in order to function");
            permAlertBuilder.setPositiveButton("No problem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            });
            permAlertBuilder.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            permAlert = permAlertBuilder.create();
            permAlert.show();
        } else {
            startActivity(changeScreenIntent);
        }
    }


    /*
    This creates a one-time worker. The one time worker is set with an initial delay that is the number
    of milliseconds until the trigger time (currently 8am). The trigger time `delay` will be set
    to 8am today, if the scheduler runs before 7am, or it will run at 8am tomorrow.

    Each WorkRequest will reschedule the next call to 8am(ish) using the same technique
     */
    protected void createWorkers(long delay){
        WorkManager mWorkManager = WorkManager.getInstance(this);
        OneTimeWorkRequest GetRequest = new OneTimeWorkRequest.Builder(GetWorker.class)
                .setInitialDelay(delay,TimeUnit.SECONDS)
                .build();
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendWorker.class)
                .setInitialDelay(delay,TimeUnit.SECONDS)
                .build();
        PeriodicWorkRequest SaveLocationRequest = new PeriodicWorkRequest.Builder(DatabaseWorker.class, 15, TimeUnit.MINUTES).build();


        mWorkManager.enqueue(GetRequest);
        mWorkManager.enqueue(CovidRequest);
        mWorkManager.enqueue(SaveLocationRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingActivity.this, String.format("%s is required for app to function", permissions[i]), Toast.LENGTH_LONG).show();
                finishAffinity();
                return;
            }
        }
        startActivity(changeScreenIntent);
    }

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
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
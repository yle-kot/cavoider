package com.example.cavoid.activities;
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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback{

    private static final String PRIMARY_CHANNEL_ID = "Priority";
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private static final int[] PERMISSION_CODES = {26, 10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Request Permissions!!! */
        for (int i = 0; i < REQUIRED_PERMISSIONS.length; i++){
            String permission = REQUIRED_PERMISSIONS[i];
            int permissionCode = PERMISSION_CODES[i];
            final Activity context = LoadingActivity.this;
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{permission}, permissionCode);
            }
        }

        /*
        This creates a one-time worker. The one time worker is set with an initial delay that is the number
        of milliseconds until the trigger time (currently 8am). The trigger time `delay` will be set
        to 8am today, if the scheduler runs before 7am, or it will run at 8am tomorrow.

        Each WorkRequest will reschedule the next call to 8am(ish) using the same technique
         */
        long delay = GeneralUtilities.getSecondsUntilHour(8);
        WorkManager mWorkManager = WorkManager.getInstance(this);
        OneTimeWorkRequest GetRequest = new OneTimeWorkRequest.Builder(GetWorker.class)
                .setInitialDelay(delay,TimeUnit.SECONDS)
                .build();
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendWorker.class)
                .setInitialDelay(delay,TimeUnit.SECONDS)
                .build();
        PeriodicWorkRequest SaveLocationRequest = new PeriodicWorkRequest.Builder(DatabaseWorker.class, 15, TimeUnit.MINUTES).build();
        // TODO How can we schedule this to run *every morning at 7am?*


        mWorkManager.enqueue(GetRequest);
        mWorkManager.enqueue(CovidRequest);
        mWorkManager.enqueue(SaveLocationRequest);


        createNotificationChannel();



        Intent changeScreenIntent = new Intent(LoadingActivity.this, MapsActivity.class);
        startActivity(changeScreenIntent);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingActivity.this, String.format("%s is required for app to function"), Toast.LENGTH_LONG).show();
                this.finishAffinity();
            }
        }
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
package com.example.cavoid.activities;
import com.example.cavoid.workers.DailyCovidTrendWorker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "Priority";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WorkManager mWorkManager = WorkManager.getInstance(this);

        // TODO How can we schedule this to run *every morning at 7am?
        PeriodicWorkRequest DailyCovidTrendWorker = new PeriodicWorkRequest.Builder(DailyCovidTrendWorker.class, 1, TimeUnit.DAYS)
                // Set initial delay to now + (desired time - now) hours
                // Or schedule chained daily requests that each run schedules the next
                .build();

        mWorkManager.enqueue(DailyCovidTrendWorker);

        createNotificationChannel();



        Intent changeScreenIntent = new Intent(LoadingActivity.this, MapsActivity.class);
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
package com.example.cavoid.activities;

import com.example.cavoid.workers.DailyCovidTrendWorker;
import com.example.cavoid.workers.DatabaseWorker;
import com.example.cavoid.workers.GetWorker;


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
        //Load notification stuff and Work Manager
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WorkManager mWorkManager = WorkManager.getInstance(this);
        PeriodicWorkRequest GetRequest = new PeriodicWorkRequest.Builder(GetWorker.class,12, TimeUnit.HOURS ).build();
        PeriodicWorkRequest SaveLocationRequest = new PeriodicWorkRequest.Builder(DatabaseWorker.class, 15, TimeUnit.MINUTES).build();
        // TODO How can we schedule this to run *every morning at 7am?*
        PeriodicWorkRequest CovidRequest = new PeriodicWorkRequest.Builder(DailyCovidTrendWorker.class, 1, TimeUnit.DAYS)
                .build();
        mWorkManager.enqueue(GetRequest);
        mWorkManager.enqueue(SaveLocationRequest);
        mWorkManager.enqueue(CovidRequest);

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
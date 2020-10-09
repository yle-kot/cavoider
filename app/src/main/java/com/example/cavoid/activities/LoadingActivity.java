package com.example.cavoid.activities;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.cavoid.utilities.GeneralUtilities;
import com.example.cavoid.workers.DailyCovidTrendWorker;
import com.example.cavoid.workers.DatabaseWorker;
import com.example.cavoid.workers.GetWorker;

import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity {

    //This is my first change

    //This another change

    //This is my third change

    private static final String PRIMARY_CHANNEL_ID = "Priority";
    private long delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
//        OneTimeWorkRequest SaveLocationRequest = new OneTimeWorkRequest.Builder(DatabaseWorker.class).build();
        PeriodicWorkRequest SaveLocationRequest = new PeriodicWorkRequest.Builder(DatabaseWorker.class, 15, TimeUnit.MINUTES).build();
        // TODO How can we schedule this to run *every morning at 7am?*


        mWorkManager.enqueue(GetRequest);
        mWorkManager.enqueue(CovidRequest);
        mWorkManager.enqueue(SaveLocationRequest);


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
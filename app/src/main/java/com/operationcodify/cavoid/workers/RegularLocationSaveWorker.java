package com.operationcodify.cavoid.workers;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.operationcodify.cavoid.R;
import com.operationcodify.cavoid.activities.PastLocationActivity;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.LocationDao;
import com.operationcodify.cavoid.database.LocationDatabase;
import com.operationcodify.cavoid.database.PastLocation;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RegularLocationSaveWorker extends Worker {

    private Context context;
    private String CURRENT_LOCATION_CHANNEL_ID = "Current Location";
    private int NOTIFICATION_ID = 2938;
    private int GOTO_CURRENT_LOCATION_PENDING_INTENT_ID = 260;

    public RegularLocationSaveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String TAG = RegularLocationSaveWorker.class.getName();
        ArrayList<String> pastLocations;

        LocationDatabase locDb = LocationDatabase.getDatabase(getApplicationContext());
        LocationDao dao = locDb.getLocationDao();
        pastLocations = (ArrayList<String>) dao.getAllDistinctFips();
        Repository repo = new Repository(getApplicationContext());

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return Result.failure();
        }



        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(2);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);


        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location == null) {
                    Log.w(TAG, "Could not find user's location!");
                    return;
                }

                Log.i(TAG, "Saving location: " + location.toString());
                LocalDate date = LocalDate.now();
                try {
                    repo.getFipsCodeFromCurrentLocation(location, new Response.Listener<JSONObject>(){

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                PastLocation pastLocation = new PastLocation();
                                pastLocation.fips = response.getJSONArray("results").getJSONObject(0).getString("county_fips");
                                pastLocation.countyName = response.getJSONArray("results").getJSONObject(0).getString("county_name");
                                pastLocation.date = date;
                                LocationDatabase.databaseWriteExecutor.execute(() -> dao.insertLocations(pastLocation));

                                if(!pastLocations.contains(pastLocation.fips)){
                                    createWarningNotificationForCurrent(pastLocation.countyName);

                                }
                                Log.i(TAG, "Saved location: " + pastLocation.fips);

                            } catch (JSONException e) {
                                Log.w(TAG, "Could not fetch current fips...\n" + e.toString());

                            }

                        }
                    });
                } catch (IOException e) {
                    Log.w(TAG, e.toString());
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        return Result.success();
    }

    private void createWarningNotificationForCurrent(String fips) {
        String title = "COVID-19 spread in your area";
        String message;

        message= "You've entered " + fips + " which notable spread of COVID-19";
        createNotificationForCurrentActivity(title, message);
    }

    private void createNotificationForCurrentActivity(String title, String message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = getPendingIntentTo(PastLocationActivity.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CURRENT_LOCATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_trend_up)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDeleteIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    private PendingIntent getPendingIntentTo(Class<? extends Activity> activity){
        Intent gotToCurrentLocationIntent = new Intent(context, activity);

        PendingIntent goToActivityIntent = PendingIntent.getActivity(
                context,
                GOTO_CURRENT_LOCATION_PENDING_INTENT_ID,
                gotToCurrentLocationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        return goToActivityIntent;
    }
}

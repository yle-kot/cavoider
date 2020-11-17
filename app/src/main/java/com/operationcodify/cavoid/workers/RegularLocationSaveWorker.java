package com.operationcodify.cavoid.workers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.LocationDao;
import com.operationcodify.cavoid.database.LocationDatabase;
import com.operationcodify.cavoid.database.PastLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import net.danlew.android.joda.JodaTimeAndroid;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegularLocationSaveWorker extends Worker {
    private static final String TAG = RegularLocationSaveWorker.class.getSimpleName();
    private final LocationDao locDao;
    private final Repository repo;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    public RegularLocationSaveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        JodaTimeAndroid.init(context);
        LocationDatabase locDb = LocationDatabase.getDatabase(getApplicationContext());
        locDao = locDb.getLocationDao();
        repo = new Repository(getApplicationContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        ;
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        if (isMissingPermissions())
            return Result.failure();

        LocationRequest locationRequest = getLocationRequest();
        LocationCallback locationCallback = getLocationCallback();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        return Result.success();
    }

    @NotNull
    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                    .setNumUpdates(1)
                    .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                    .setInterval(10);
    }

    @NotNull
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location == null) {
                        Log.w(TAG, "Could not find user's location!");
                        return;
                    }

                    Log.i(TAG, "Saving location: " + location.toString());
                    try {
                        repo.getFipsCodeFromCurrentLocation(location, savePastLocationOnFipsCallback());
                    } catch (IOException e) {
                        Log.w(TAG, e.toString());
                    }
                }
            };
    }

    private boolean isMissingPermissions() {
        if (
                (
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                )
                        || // OR
                (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                )
        ) {
            return true;
        }
        return false;
    }

    @NotNull
    private Response.Listener<JSONObject> savePastLocationOnFipsCallback() {
        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    LocalDate date = LocalDate.now();
                    PastLocation pastLocation = new PastLocation();

                    pastLocation.fips = response.getJSONArray("results").getJSONObject(0).getString("county_fips");
                    pastLocation.countyName = response.getJSONArray("results").getJSONObject(0).getString("county_name");
                    pastLocation.date = date;

                    LocationDatabase.databaseWriteExecutor.execute(() -> locDao.insertLocations(pastLocation));
                    // TODO Notify user if new location && trend > 0
                    Log.i(TAG, "Saved location: " + pastLocation.fips);
                } catch (JSONException e) {
                    try {
                        if (response.getString("status").equals("error")){
                            Log.w(TAG, "Invalid location! Outside of US?");
                        }
                    } catch (JSONException ex2){
                        Log.w(TAG, "Unknown FCC API Error: " + response.toString());
                    }

                }

            }
        };
    }
}

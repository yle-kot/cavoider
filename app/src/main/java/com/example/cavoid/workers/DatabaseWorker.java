package com.example.cavoid.workers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.JsonReader;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.api.Utilities;
import com.example.cavoid.utilities.AppNotificationHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;

public class DatabaseWorker extends Worker {
    public DatabaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    try {
                        Repository.getCurrentLocationFromFipsCode(getApplicationContext(), latitude, longitude, new Response.Listener<JSONObject>(){

                            @Override
                            public void onResponse(JSONObject response) {
                                String fips = null;
                                try {
                                    fips = response.getString("county_fips");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Repository.getPosTests(getApplicationContext(), fips , new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        AppNotificationHandler.deliverNotification(getApplicationContext(),"Test Title", response.toString());
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch( RuntimeException e){
                        e.printStackTrace();
                    }

                }
            }
        });
        return Result.success();
    }
}

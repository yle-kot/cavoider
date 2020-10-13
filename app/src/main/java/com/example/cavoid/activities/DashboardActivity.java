package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.ListenableWorker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.android.volley.Response;
import com.example.cavoid.R;
import com.example.cavoid.api.Repository;
import com.example.cavoid.workers.RegularLocationSaveWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DashboardActivity extends AppCompatActivity {
    //String countyName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getCountyName();
    }

    public ListenableWorker.Result getCountyName(){
        TextView currentCounty = (TextView) findViewById(R.id.greetingTextView);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        String TAG = RegularLocationSaveWorker.class.getName();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ListenableWorker.Result.failure();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return ListenableWorker.Result.failure();
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null){
                Log.w(TAG, "Could not find user's location!");
            }
            else {
                Log.i(TAG, "Saving location: " + location.toString());
                LocalDate date = LocalDate.now();
                try {
                    Repository.getFipsCodeFromCurrentLocation(getApplicationContext(), location, new Response.Listener<JSONObject>(){

                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject data = response;
                            try {
                                String countyName = response.getJSONArray("results").getJSONObject(0).getString("county_name");
                                currentCounty.setText("Here are the covid statistics for " + countyName);
                            }
                            catch(JSONException e) {

                            }

                        }
                    });
                } catch (IOException e) {
                    Log.w(TAG, e.toString());
                }

            }


        });
        return ListenableWorker.Result.success();
    }
    }


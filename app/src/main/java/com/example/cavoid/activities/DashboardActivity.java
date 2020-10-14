package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.ListenableWorker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.Response;
import com.example.cavoid.R;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.ExposureCheck;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;
import com.example.cavoid.workers.RegularLocationSaveWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private String newCaseNumber;
    private String newDeathNumber;
    private String activeCases;
    private String totalCases;
    private String totalDeaths;
    private String caseMessage;
    private String deathMessage;
    private ArrayList<String> pastLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Button notificationButton = (Button) findViewById(R.id.notificationButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button pastLocationButton = (Button) findViewById(R.id.pastLocationButton);
        //For when the NotificationActivity is created
//        notificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent notificationIntent = new Intent(DashboardActivity.this, notificationActivity.class);
//                startActivity(notificationIntent);
//            }
//        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(DashboardActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
        });
        pastLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pastLocationIntent = new Intent(DashboardActivity.this, PastLocationActivity.class);
                startActivity(pastLocationIntent);
            }
        });
        updateDashBoard();

    }
    public Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    public String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(yesterday());
    }

    public ListenableWorker.Result updateDashBoard(){
        TextView currentCounty = (TextView) findViewById(R.id.greetingTextView);
        TextView cases = (TextView) findViewById(R.id.casesTextView);
        TextView deaths = (TextView) findViewById(R.id.deathsTextView);
        TextView pastLocationCases =(TextView) findViewById(R.id.pastCasesTextView);
        TextView pastLocationDeaths = (TextView) findViewById(R.id.pastDeathsTextView);


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
                                String countyfips = response.getJSONArray("results").getJSONObject(0).getString("county_fips");
                                String yesterday = getYesterdayDateString();

                                currentCounty.setText("Here are the covid statistics for " + countyName);

                                //LocationDao locDao = LocationDatabase.getLocationDao();

                                //This gets the statistics for the current county and sets the first card
                                Repository.getPosTests(getApplicationContext(), countyfips, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            //String reportDate = response.getString("report_date");
                                            newCaseNumber = response.getString("new_daily_cases");
                                            newDeathNumber = response.getString("new_daily_deaths");
                                            activeCases = response.getString("active_cases_est");
                                            totalCases = response.getString("cases");
                                            totalDeaths = response.getString("deaths");
                                            caseMessage =  "  " + yesterday +  " New cases: " + newCaseNumber
                                                    + " Active cases: " + activeCases + " Total cases: " + totalCases + "  ";
                                            deathMessage = "  " + yesterday +  " New deaths: " + newDeathNumber
                                                    + " Total deaths: " + totalDeaths + "  ";
                                            cases.setText(caseMessage);
                                            deaths.setText(deathMessage);
                                        } catch (JSONException e) {

                                        }
                                    }
                                });

                                //Go through the database of past locations then for each fips get statistics for that county
                                //update pastLocationCases and pastLocationDeaths with the current text + next past location stats
//                                pastLocationList = ExposureCheck.getPastFips(getApplicationContext());
//                                for(String p:pastLocationList){
//                                    Repository.getPosTests(getApplicationContext(), p, new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            try {
//                                                //String reportDate = response.getString("report_date");
//                                                newCaseNumber = response.getString("new_daily_cases");
//                                                newDeathNumber = response.getString("new_daily_deaths");
//                                                activeCases = response.getString("active_cases_est");
//                                                totalCases = response.getString("cases");
//                                                totalDeaths = response.getString("deaths");
//                                                caseMessage = caseMessage + countyfips + ":  New cases on " + yesterday + ": " + newCaseNumber
//                                                        + " Active Cases: " + activeCases + " Total Cases: " + totalCases + "  ";
//                                                deathMessage = deathMessage + "  New deaths on " + yesterday + ": " + newDeathNumber
//                                                        + " Total deaths: " + totalDeaths + "  ";
//                                                pastLocationCases.setText(caseMessage);
//                                                pastLocationDeaths.setText(deathMessage);
//                                            } catch (JSONException e) {
//
//                                            }
//                                        }
//                                    });
//                                }


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


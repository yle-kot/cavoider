package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.android.gms.maps.model.Dash;

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

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
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

        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button pastLocationButton = (Button) findViewById(R.id.pastLocationButton);

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

    public boolean updateDashBoard() {
        TextView currentCounty = (TextView) findViewById(R.id.greetingTextView);
        TextView cases = (TextView) findViewById(R.id.casesTextView);
        TextView deaths = (TextView) findViewById(R.id.deathsTextView);
        TextView pastLocationCases = (TextView) findViewById(R.id.pastCasesTextView);
        TextView pastLocationDeaths = (TextView) findViewById(R.id.pastDeathsTextView);


        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        String TAG = DashboardActivity.class.getName();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        LocationDao locDb = LocationDatabase.getDatabase(DashboardActivity.this).getLocationDao();
        PastLocation latestLocation = locDb.getLatestLocation();
        if (latestLocation == null) {
            Log.w(TAG, "No saved locations returned from db!");
            return false;
        }

        String countyfips = latestLocation.fips;
        String countyName = latestLocation.countyName;
        String yesterday = getYesterdayDateString();

        currentCounty.setText(String.format("Here are the covid statistics for %s", countyName));


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
                    caseMessage = "  " + yesterday + " New cases: " + newCaseNumber
                            + " Active cases: " + activeCases + " Total cases: " + totalCases + "  ";
                    deathMessage = "  " + yesterday + " New deaths: " + newDeathNumber
                            + " Total deaths: " + totalDeaths + "  ";
                    cases.setText(caseMessage);
                    deaths.setText(deathMessage);

                } catch (JSONException e) {
                    Log.w(TAG, "Could not get data from JSON response!");
                    e.printStackTrace();
                }
            }
        });


        return true;
    }
}


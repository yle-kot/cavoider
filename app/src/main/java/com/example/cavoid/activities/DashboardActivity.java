package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ListenableWorker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cavoid.R;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {

    private String newCaseNumber;
    private String newDeathNumber;
    private String activeCases;
    private String totalCases;
    private String totalDeaths;
    private String caseMessage;
    private String deathMessage;
    private ArrayList<String> pastLocationList;
    private Repository repo;
    private DashboardActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        viewModel = ViewModelProviders.of(this).get(DashboardActivityViewModel.class);


        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button pastLocationButton = (Button) findViewById(R.id.pastLocationButton);
        repo = new Repository(this);

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

        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateDashBoard();
            }
        });
    }

    public Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MMM d");
        return dateFormat.format(yesterday());
    }

    public boolean updateDashBoard() {
        TextView currentCounty = (TextView) findViewById(R.id.greetingTextView);
        TextView cases = (TextView) findViewById(R.id.casesTextView);
        TextView deaths = (TextView) findViewById(R.id.deathsTextView);
        TextView pastLocationCases = (TextView) findViewById(R.id.pastCasesTextView);
        TextView pastLocationDeaths = (TextView) findViewById(R.id.pastDeathsTextView);


        String yesterday = getYesterdayDateString();

        currentCounty.setText(String.format("Here are the covid statistics for %s", viewModel.countyName));

        return true;
    }
}


package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.cavoid.R;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.ExposureCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PastLocationActivity extends AppCompatActivity {

    private String newCaseNumber;
    private String newDeathNumber;
    private String activeCases;
    private String totalCases;
    private String totalDeaths;
    private String caseMessage;
    private String deathMessage;
    private ArrayList<String> pastLocationList;

    public Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    public String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(yesterday());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_location);
        String yesterday = getYesterdayDateString();
        TextView pastLocationCases =(TextView) findViewById(R.id.pastCasesTextView);
        TextView pastLocationDeaths = (TextView) findViewById(R.id.pastDeathsTextView);

        Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button notificationButton = (Button) findViewById(R.id.notificationButton);
        pastLocationList = ExposureCheck.getPastFips(getApplicationContext());
        for(String p:pastLocationList){
            Repository.getPosTests(getApplicationContext(), p, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //String reportDate = response.getString("report_date");
                        newCaseNumber = response.getString("new_daily_cases");
                        newDeathNumber = response.getString("new_daily_deaths");
                        activeCases = response.getString("active_cases_est");
                        totalCases = response.getString("cases");
                        totalDeaths = response.getString("deaths");
                        caseMessage = caseMessage + p + ":  New cases on " + yesterday + ": " + newCaseNumber
                                + " Active Cases: " + activeCases + " Total Cases: " + totalCases + "  ";
                        deathMessage = deathMessage + "  New deaths on " + yesterday + ": " + newDeathNumber
                                + " Total deaths: " + totalDeaths + "  ";
                        pastLocationCases.setText(caseMessage);
                        pastLocationDeaths.setText(deathMessage);
                    } catch (JSONException e) {

                    }
                }
            });
        }

        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboardIntent = new Intent(PastLocationActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(PastLocationActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
        });
        //For when the notification activity is created
//        notificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent notificationIntent = new Intent(PastLocationActivity.this, NotificationActivity.class);
//                startActivity(notificationIntent);
//            }
//        });
    }
}
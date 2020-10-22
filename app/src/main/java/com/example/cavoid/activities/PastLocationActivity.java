package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.cavoid.database.PastLocation;
import com.example.cavoid.utilities.PastLocationAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PastLocationActivity extends AppCompatActivity {

    //TODO:Finish implementing recycleview with hunter

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
    private ArrayList<String> pastLocationsList;
    public ArrayList<String> messages;


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
        pastLocationsList = ExposureCheck.getPastFips(getApplicationContext());
        String yesterday = getYesterdayDateString();
        Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);

        //TODO:Asynchronously get data from the api to display

        recyclerView = (RecyclerView) findViewById(R.id.pastLocationsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        mAdapter = new PastLocationAdapter(this,pastLocationsList);
        recyclerView.setAdapter(mAdapter);


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
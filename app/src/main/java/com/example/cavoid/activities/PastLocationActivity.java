package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.example.cavoid.R;

public class PastLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_location);
        Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button notificationButton = (Button) findViewById(R.id.notificationButton);

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
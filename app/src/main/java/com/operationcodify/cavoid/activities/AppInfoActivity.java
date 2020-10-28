package com.operationcodify.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.operationcodify.cavoid.R;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboardIntent = new Intent(AppInfoActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
            }
        });

        Button pastLocationButton = (Button) findViewById(R.id.pastLocationButton);
        pastLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pastLocationIntent = new Intent(AppInfoActivity.this, PastLocationActivity.class);
                startActivity(pastLocationIntent);
            }
        });

        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(AppInfoActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(AppInfoActivity.this, SettingsActivity.class);
                //Log.d(DashboardActivity.class.getName(), "Intent didn't start" + settingsIntent);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_appInfo:
                Intent appInfoIntent = new Intent(AppInfoActivity.this, AppInfoActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
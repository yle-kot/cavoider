package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.cavoid.R;
import com.example.cavoid.database.PastLocation;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button notificationButton = (Button) findViewById(R.id.communitySpreadNotificationButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                notificationIntent.putExtra(Settings.EXTRA_CHANNEL_ID, "Priority");
                startActivity(notificationIntent);
            }
        });

        Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboardIntent = new Intent(SettingsActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
            }
        });

        Button pastLocationButton = (Button) findViewById(R.id.pastLocationButton);
        pastLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pastLocationIntent = new Intent(SettingsActivity.this, PastLocationActivity.class);
                startActivity(pastLocationIntent);
            }
        });

        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(SettingsActivity.this, MapsActivity.class);
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
                Intent settingsIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                //Log.d(DashboardActivity.class.getName(), "Intent didn't start" + settingsIntent);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_appInfo:
                Intent appInfoIntent = new Intent(SettingsActivity.this, AppInfoActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.operationcodify.cavoid.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button notificationButton = (Button) findViewById(R.id.communitySpreadNotificationButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                notificationIntent.putExtra(Settings.EXTRA_CHANNEL_ID, "Past Location");
                startActivity(notificationIntent);
            }
        });
        Button CommunitySpreadButton = (Button) findViewById(R.id.currentLocationNotificationButton);
        CommunitySpreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                notificationIntent.putExtra(Settings.EXTRA_CHANNEL_ID, "Priority");
                startActivity(notificationIntent);
            }
        });
    }


    /**
     * displays the menu in the top toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * switches to the corresponding activity based on the activity selected by the user in the menu
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_covidDataInfo:
                Intent appInfoIntent = new Intent(SettingsActivity.this, CovidDataActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
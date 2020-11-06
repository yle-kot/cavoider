package com.operationcodify.cavoid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.operationcodify.cavoid.R;

public class SettingsActivity extends AppCompatActivity {

    String PREFS_NAME = "theme_prefs";
    String KEY_THEME = "prefs.theme";
    final int THEME_UNDEFINED = -1;
    final int THEME_LIGHT = 0;
    final int THEME_DARK = 1;
    final int THEME_SYSTEM = 2;
    final int THEME_BATTERY = 3;
    SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (LoadingActivity.getInstance().isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Switch switchCompat = findViewById(R.id.switchCompat);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this, R.style.amu_Bubble_TextAppearance_Light)
                        .setTitle("Title")
                        .setMessage("Message")
                        .show();
            }
        });

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            switchCompat.setChecked(true);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LoadingActivity.getInstance().setIsNightModeEnabled(true);
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);

                } else {
                    LoadingActivity.getInstance().setIsNightModeEnabled(false);
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                }


            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.invisibleBottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboardBottomMenu:
                        Intent dashboardIntent = new Intent(SettingsActivity.this, DashboardActivity.class);
                        startActivity(dashboardIntent);
                        break;
                    case R.id.pastLocationBottomMenu:
                        Intent pastLocationIntent = new Intent(SettingsActivity.this, PastLocationActivity.class);
                        startActivity(pastLocationIntent);
                        break;
                    case R.id.mapBottomMenu:
                        Intent mapIntent = new Intent(SettingsActivity.this, MapsActivity.class);
                        startActivity(mapIntent);
                        break;
                }
                return false;
            }
        });

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


    public void setTheme(int themeMode, int prefsMode){
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }


    public void saveTheme(int theme){
        sharedPrefs.edit().putInt(KEY_THEME, theme).apply();

    }

    public int getSavedTheme(){
        return sharedPrefs.getInt(KEY_THEME,THEME_UNDEFINED);
    }
}
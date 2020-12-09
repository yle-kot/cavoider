package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.operationcodify.cavoid.R;

public class CovidDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_data_info);

        getSupportActionBar().setTitle("COVID-19 Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                Intent settingsIntent = new Intent(CovidDataActivity.this, SettingsActivity.class);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_covidDataInfo:
                Intent appInfoIntent = new Intent(CovidDataActivity.this, CovidDataActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
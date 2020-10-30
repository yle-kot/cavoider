package com.operationcodify.cavoid.activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.operationcodify.cavoid.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private String newCaseNumber;
    private String newDeathNumber;
    private String activeCases;
    private String totalCases;
    private String totalDeaths;
    private String caseMessage;
    private String deathMessage;
    private DashboardActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        viewModel = new ViewModelProvider(this).get(DashboardActivityViewModel.class);
        Button mapButton = findViewById(R.id.mapButton);
        Button pastLocationButton = findViewById(R.id.pastLocationButton);
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
    public void updateDashBoard() {
        String yesterday = getYesterdayString();
        TextView currentCounty = findViewById(R.id.greetingTextView);
        TextView cases = findViewById(R.id.casesTextView);
        TextView deaths = findViewById(R.id.deathsTextView);
        TextView pastLocationCases = findViewById(R.id.pastCasesTextView);
        TextView pastLocationDeaths = findViewById(R.id.pastDeathsTextView);
        currentCounty.setText(String.format("COVID-19 statistics for %s, %s on %s.", viewModel.countyName, viewModel.state, yesterday));
        cases.setText(String.format("New Cases: %s Active Cases: %s",viewModel.newCaseNumber,viewModel.activeCasesEst));
        deaths.setText(String.format("New Deaths: %s Total Deaths: %s",viewModel.newDeathNumber, viewModel.totalDeaths));
    }

    public String getYesterdayString() {
        String suffix = "";
        Calendar cal = Calendar.getInstance();
       //  cal.add(Calendar.DATE, -1);
        int day = Calendar.DAY_OF_MONTH;
        Instant now = Instant.now();
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMM. d");
        if (day == 12) {
            suffix = "th";
        }
        switch (day % 10) {
            case 1:  suffix = "st";
            case 2:  suffix = "nd";
            case 3:  suffix = "rd";
            default: suffix = "th";
        }
        return dateFormat.format(cal.getTime()) + suffix;
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
                Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                //Log.d(DashboardActivity.class.getName(), “Intent didnt start” + settingsIntent);
                 this.startActivity(settingsIntent);
                 break;
                 case R.id.action_appInfo:
                     Intent appInfoIntent = new Intent(DashboardActivity.this, AppInfoActivity.class);
                    this.startActivity(appInfoIntent);
                    break;
        }
                return super.onOptionsItemSelected(item);
    }
}
package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.operationcodify.cavoid.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private DashboardActivityViewModel viewModel;
    public SimpleDateFormat dateFormat;
    public String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.dashboardBottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pastLocationBottomMenu:
                        Intent pastLocationIntent = new Intent(DashboardActivity.this, PastLocationActivity.class);
                        startActivity(pastLocationIntent);
                        break;
                    case R.id.mapBottomMenu:
                        Intent mapIntent = new Intent(DashboardActivity.this, MapsActivity.class);
                        startActivity(mapIntent);
                        break;
                }
                return true;
            }
        });
        viewModel = new ViewModelProvider(this).get(DashboardActivityViewModel.class);
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
        currentCounty.setText(String.format("COVID-19 statistics for %s, %s on %s.", viewModel.countyName, viewModel.state, yesterday));

        TextView totalCases = findViewById(R.id.TotalcasesTextView);
        totalCases.setText("Total Cases:");

        TextView totalCasesNum = findViewById(R.id.TotalcasesNum);
        totalCasesNum.setText(viewModel.totalCases);

        TextView totalDeaths = findViewById(R.id.TotalDeathTextView);
        totalDeaths.setText("Total Deaths:");

        TextView totalDeathsNum = findViewById(R.id.TotalDeathNum);
        totalDeathsNum.setText(viewModel.totalDeaths);

        TextView newCases = findViewById(R.id.casesTextView);
        newCases.setText("New Cases:");

        TextView newCasesNum = findViewById(R.id.casesNum);
        newCasesNum.setText(viewModel.newCaseNumber);

        TextView newDeaths = findViewById(R.id.deathsTextView);
        newDeaths.setText("New Deaths");

        TextView newDeathsNum = findViewById(R.id.deathsNum);
        newDeathsNum.setText(viewModel.newDeathNumber);

        TextView activeCasesEst = findViewById(R.id.EstCasesTextView);
        activeCasesEst.setText("Estimated Active Cases:");

        TextView activeCasesEstNum = findViewById(R.id.EstCasesNum);
        activeCasesEstNum.setText(viewModel.activeCasesEst);

        TextView casesPer100K = findViewById(R.id.casesPerTextView);
        casesPer100K.setText("Cases per 100K People:");

        TextView casesPer100KNum = findViewById(R.id.casesPerNum);
        casesPer100KNum.setText(viewModel.casesPer100K);

        TextView caseFatality = findViewById(R.id.CaseFatalityTextView);
        caseFatality.setText("Case Fatality:");

        TextView caseFatalityNum = findViewById(R.id.CaseFatalityNum);
        caseFatalityNum.setText(viewModel.caseFatality);

        TextView deathPer100K = findViewById(R.id.DeathPerTextView);
        deathPer100K.setText("New Deaths");

        TextView deathPer100KNum = findViewById(R.id.DeathPerNum);
        deathPer100KNum.setText(viewModel.deathsPer100K);
    }
    // Todo: Return a string of correct date. Ex: "Nov. 8th"

    public String getYesterdayString() {
        String suffix = "";
        int day = 0;
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MMM. d");
        date = dateFormat.format(cal.getTime());
        if (day == 12) {
            suffix = "th";
        }
        switch (day % 10) {
            case 1:  suffix = "st";
            case 2:  suffix = "nd";
            case 3:  suffix = "rd";
            default: suffix = "th";
        }
        return suffix;
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
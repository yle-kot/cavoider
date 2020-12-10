package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.operationcodify.cavoid.R;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;

public class DashboardActivity extends AppCompatActivity {

    public SimpleDateFormat dateFormat;
    public String date;
    public BottomNavigationView bottomNavigationView;
    private DashboardActivityViewModel viewModel;


    /**
     * Gives functionality to views and view models in the dashboard
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Check if we need to display our OnboardingSupportFragment
        if (!sharedPreferences.getBoolean(MyOnboardingSupportFragment.preferences, false)) {
            // The user hasn't seen the OnboardingSupportFragment yet, so show it
            startActivity(new Intent(this, OnboardingActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setTitle("Main Dashboard");

        bottomNavigationView = addBottomMenu();


        viewModel = new ViewModelProvider(this).get(DashboardActivityViewModel.class);
        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateDashBoard();
            }
        });
    }

    public BottomNavigationView addBottomMenu() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.dashboardBottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pastLocationBottomMenu:
                        Intent pastLocationIntent = new Intent(DashboardActivity.this, PastLocationActivity.class);
                        startActivity(pastLocationIntent);
                        break;
                    case R.id.graphBottomMenu:
                        Intent mapIntent = new Intent(DashboardActivity.this, GraphActivity.class);
                        startActivity(mapIntent);
                        break;
                    case R.id.generalInfoBottomMenu:
                        Intent generalInfoIntent = new Intent(DashboardActivity.this, GeneralInformationActivity.class);
                        startActivity(generalInfoIntent);
                        break;
                }
                return true;
            }
        });
        return bottomNavigationView;
    }

    @Override
    public void onResume() {
        bottomNavigationView.setSelectedItemId(R.id.dashboardBottomMenu);
        super.onResume();
    }

    /**
     * Sets the text of textvies
     * updates values of COVID stats
     * @return void
     */
    public void updateDashBoard() {
        String yesterday = getYesterdayString();

        TextView currentCounty = findViewById(R.id.greetingTextView);
        currentCounty.setText(String.format("COVID-19 statistics for %s, %s on %s.", viewModel.countyName, viewModel.state, yesterday));

        TextView totalCases = findViewById(R.id.TotalcasesTextView);
        totalCases.setText("Total Cases:");

        TextView totalCasesNum = findViewById(R.id.TotalcasesNum);
        totalCasesNum.setText(viewModel.totalCases2);

        TextView totalDeaths = findViewById(R.id.TotalDeathTextView);
        totalDeaths.setText("Total Deaths:");

        TextView totalDeathsNum = findViewById(R.id.TotalDeathNum);
        totalDeathsNum.setText(viewModel.totalDeaths2);

        TextView newCases = findViewById(R.id.casesTextView);
        newCases.setText("New Cases:");

        TextView newCasesNum = findViewById(R.id.casesNum);
        newCasesNum.setText(viewModel.newCaseNumber2);

        TextView newDeaths = findViewById(R.id.deathsTextView);
        newDeaths.setText("New Deaths:");

        TextView newDeathsNum = findViewById(R.id.deathsNum);
        newDeathsNum.setText(viewModel.newDeathNumber2);

        TextView activeCasesEst = findViewById(R.id.EstCasesTextView);
        activeCasesEst.setText("Estimated Active Cases:");

        TextView activeCasesEstNum = findViewById(R.id.EstCasesNum);
        activeCasesEstNum.setText(viewModel.activeCasesEst2);

        TextView casesPer100K = findViewById(R.id.casesPerTextView);
        casesPer100K.setText("Cases per 100K People:");

        TextView casesPer100KNum = findViewById(R.id.casesPerNum);
        casesPer100KNum.setText(viewModel.casesPer100K2);

        TextView caseFatality = findViewById(R.id.CaseFatalityTextView);
        caseFatality.setText("Case Fatality:");

        TextView caseFatalityNum = findViewById(R.id.CaseFatalityNum);
        caseFatalityNum.setText(viewModel.caseFatality2);

        TextView deathPer100K = findViewById(R.id.DeathPerTextView);
        deathPer100K.setText("New Deaths:");

        TextView deathPer100KNum = findViewById(R.id.DeathPerNum);
        deathPer100KNum.setText(viewModel.deathsPer100K2);
    }

    public String getYesterdayString() {
        String suffix = "";
        DateTime now = new Instant().toDateTime();
        int day = now.getDayOfMonth();
        String month = now.monthOfYear().getAsText();
        if (day == 12) {
            suffix = "th";
        }
        switch (day % 10) {
            case 1:
                suffix = "st";
            case 2:
                suffix = "nd";
            case 3:
                suffix = "rd";
            default:
                suffix = "th";
        }
        return String.format("%s %d%s", month, day, suffix);
    }

}
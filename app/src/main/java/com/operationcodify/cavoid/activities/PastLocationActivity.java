package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.operationcodify.cavoid.R;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.ExposureCheckViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//The past location activity shows the user past locations they have visited in the order of most recently notified locations

public class PastLocationActivity extends AppCompatActivity {

    private static final String TAG = PastLocationActivity.class.getSimpleName();
    public ArrayList<ParsedPastLocationReport> reports;
    public BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private PastLocationAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String newCaseNumber;
    private String newDeathNumber;
    private String activeCases;
    private String totalCases;
    private String totalDeaths;
    private String caseMessage;
    private String deathMessage;
    private ExposureCheckViewModel exposureCheck;
    private ArrayList<String> pastLocationsList;
    private Repository repo;
    private PastLocationActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_location);

        getSupportActionBar().setTitle("Past Location Dashboard");

        repo = new Repository(getApplicationContext());
        exposureCheck = new ExposureCheckViewModel(getApplication(), repo);
        pastLocationsList = exposureCheck.getAllFipsFromLastTwoWeeks();
        viewModel = new ViewModelProvider(this).get(PastLocationActivityViewModel.class);
        bottomNavigationView = createBottomNavigationView();

        String yesterday = getYesterdayDateString();

        setupRecyclerView();
        createBottomNavigationView();

        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    return;
                }
                ParsedPastLocationReport report = reports.get(integer - 1);
                Log.d(TAG, "Adding report to view: " + report.countyName);
                mAdapter.add(report);
            }
        });
    }

    public Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(yesterday());
    }


    private BottomNavigationView createBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.pastLocationBottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboardBottomMenu:
                        Intent dashboardIntent = new Intent(PastLocationActivity.this, DashboardActivity.class);
                        startActivity(dashboardIntent);
                        break;
                    case R.id.graphBottomMenu:
                        Intent mapIntent = new Intent(PastLocationActivity.this, GraphActivity.class);
                        startActivity(mapIntent);
                        break;
                    case R.id.generalInfoBottomMenu:
                        Intent generalInfoIntent = new Intent(PastLocationActivity.this, GeneralInformationActivity.class);
                        startActivity(generalInfoIntent);
                        break;
                }
                return true;
            }
        });

        return bottomNavigationView;
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.pastLocationsRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        reports = viewModel.getReports();
        mAdapter = new PastLocationAdapter(this, reports);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        bottomNavigationView.setSelectedItemId(R.id.pastLocationBottomMenu);
        super.onResume();
    }


}
package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cavoid.R;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.ExposureCheckViewModel;
import com.example.cavoid.utilities.PastLocationAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PastLocationActivity extends AppCompatActivity {

    //TODO:Finish implementing recycleview with hunter

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
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
    public ArrayList<String> messages;
    private PastLocationActivityViewModel viewModel;


    public Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    public String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(yesterday());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_location);
        repo = new Repository(getApplicationContext());
        exposureCheck = new ExposureCheckViewModel(getApplication(),repo);
        pastLocationsList = exposureCheck.getAllFipsFromLastTwoWeeks();
        viewModel = new ViewModelProvider(this).get(PastLocationActivityViewModel.class);
        String yesterday = getYesterdayDateString();
        Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        recyclerView = (RecyclerView) findViewById(R.id.pastLocationsRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new PastLocationAdapter(this,messages);
        messages = viewModel.messages;
        recyclerView.setAdapter(mAdapter);
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
        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                messages = viewModel.messages;
                for(int i = 0; i < messages.size();i++){
                    Log.d("Poop", messages.get(i));
                }
                mAdapter = new PastLocationAdapter(getApplicationContext(),messages);
                recyclerView.setAdapter(mAdapter);
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
                Intent settingsIntent = new Intent(PastLocationActivity.this, SettingsActivity.class);
                //Log.d(DashboardActivity.class.getName(), "Intent didn't start" + settingsIntent);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_appInfo:
                Intent appInfoIntent = new Intent(PastLocationActivity.this, AppInfoActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.operationcodify.cavoid.R;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.ExposureCheckViewModel;
import com.operationcodify.cavoid.utilities.PastLocationAdapter;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private ExposureCheckViewModel exposureCheck;
    private ArrayList<String> pastLocationsList;
    private Repository repo;
    public ArrayList<String> messages;
    private GraphActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.graphBottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboardBottomMenu:
                        Intent dashboardIntent = new Intent(MapsActivity.this, DashboardActivity.class);
                        startActivity(dashboardIntent);
                        break;
                    case R.id.pastLocationBottomMenu:
                        Intent pastLocationIntent = new Intent(MapsActivity.this, PastLocationActivity.class);
                        startActivity(pastLocationIntent);
                        break;
                }
                return true;
            }
        });

        repo = new Repository(getApplicationContext());
        exposureCheck = new ExposureCheckViewModel(getApplication(),repo);
        pastLocationsList = exposureCheck.getAllFipsFromLastTwoWeeks();
        viewModel = new ViewModelProvider(this).get(GraphActivityViewModel.class);
        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateList();
            }
        });
    }

    public void updateList() {
        ArrayList<String> activeCasesEst = viewModel.activeCasesEst;
        List<BarEntry> activeCasesEntries = new ArrayList<>();
        if (activeCasesEst != null) {
            for (int i = 0; i < activeCasesEst.size(); i++) {
                int cases = 0;
                try {
                    cases = Integer.getInteger(activeCasesEst.get(i));
                } catch (NumberFormatException ex) {
                }
                activeCasesEntries.add(new BarEntry(i, cases));
                if (i == 9) {
                    break;
                }
            }
        }
        activeCasesEntries.add(new BarEntry(0, 0));
        BarChart pastLocationActiveCases = (BarChart) findViewById(R.id.pastLocationChart);
        BarDataSet set = new BarDataSet(activeCasesEntries, "BarDataSet");
        BarData data = new BarData(set);
        pastLocationActiveCases.setData(data);
        pastLocationActiveCases.setFitBars(true);
        pastLocationActiveCases.invalidate();
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
                Intent settingsIntent = new Intent(MapsActivity.this, SettingsActivity.class);
                //Log.d(DashboardActivity.class.getName(), "Intent didn't start" + settingsIntent);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_appInfo:
                Intent appInfoIntent = new Intent(MapsActivity.this, AppInfoActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
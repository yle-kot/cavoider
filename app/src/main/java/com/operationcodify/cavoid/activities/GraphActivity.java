package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.operationcodify.cavoid.R;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.ExposureCheckViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class GraphActivity extends AppCompatActivity {

    private ExposureCheckViewModel exposureCheck;
    private ArrayList<String> pastLocationsList;
    private Repository repo;
    private GraphActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.graphBottomMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboardBottomMenu:
                        Intent dashboardIntent = new Intent(GraphActivity.this, DashboardActivity.class);
                        startActivity(dashboardIntent);
                        break;
                    case R.id.pastLocationBottomMenu:
                        Intent pastLocationIntent = new Intent(GraphActivity.this, PastLocationActivity.class);
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
                updateGraph();
            }
        });
    }

    public void updateGraph() {
        PriorityQueue<GraphActivityViewModel.ChartData> rollingAvg = viewModel.rollingAvg;
        ArrayList<BarEntry> rollingAvgEntries = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<Float> rollingAvgState = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        float highestValue = 10;
        if (!rollingAvg.isEmpty()) {
            int rollingAvgSize = rollingAvg.size();
            for (int i = 0; i < rollingAvgSize; i++) {
                GraphActivityViewModel.ChartData chartData = rollingAvg.poll();
                float casesCounty =  (float) chartData.getWeek2RollingAvgCounty();
                if (i == (rollingAvgSize - 1)) {
                    highestValue = casesCounty + 10;
                }
                float casesState = (float) chartData.getWeek2RollingAvgState();
                String county = chartData.getCounty();
                String state = chartData.getState();
                rollingAvgEntries.add(new BarEntry(i, casesCounty));
                xAxisLabel.add(county);
                if (!states.contains(state)) {
                    rollingAvgState.add(casesState);
                    states.add(state);
                }
            }
        }
        else {
            rollingAvgEntries.add(new BarEntry(0, 0));
        }
        BarChart pastLocationChart = (BarChart) findViewById(R.id.pastLocationChart);
        Description description = pastLocationChart.getDescription();
        description.setEnabled(false);
        YAxis yAxisRight = pastLocationChart.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = pastLocationChart.getAxisLeft();
        if (states.size() > 0) {
            ArrayList<String> addedStates = new ArrayList<>();
            for (int i = 0; i < states.size(); i++) {
                float rollingAvgStateValue = rollingAvgState.get(i);
                String stateName = states.get(i);
                if ((rollingAvgStateValue + 10) > highestValue) {
                    highestValue = rollingAvgStateValue + 10;
                }
                if (!addedStates.contains(stateName)) {
                    LimitLine stateLine = new LimitLine(rollingAvgStateValue, stateName + " Average New Cases");
                    stateLine.setLineColor(Color.BLACK);
                    stateLine.setLineWidth(2f);
                    stateLine.enableDashedLine(30, 25, 0);
                    yAxisLeft.addLimitLine(stateLine);
                }
                addedStates.add(stateName);
            }
        }
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum(highestValue);
        XAxis xAxis = pastLocationChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-60f);
        BarDataSet set = new BarDataSet(rollingAvgEntries, "Average New Cases");
        BarData data = new BarData(set);
        pastLocationChart.setDrawValueAboveBar(false);
        pastLocationChart.setData(data);
        pastLocationChart.setFitBars(true);
        pastLocationChart.invalidate();
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
                Intent settingsIntent = new Intent(GraphActivity.this, SettingsActivity.class);
                //Log.d(DashboardActivity.class.getName(), "Intent didn't start" + settingsIntent);
                this.startActivity(settingsIntent);
                break;
            case R.id.action_appInfo:
                Intent appInfoIntent = new Intent(GraphActivity.this, AppInfoActivity.class);
                this.startActivity(appInfoIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
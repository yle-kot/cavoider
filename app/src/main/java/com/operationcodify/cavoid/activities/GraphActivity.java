package com.operationcodify.cavoid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.operationcodify.cavoid.utilities.ChartData;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    private ExposureCheckViewModel exposureCheck;
    private ArrayList<String> pastLocationsList;
    private Repository repo;
    private GraphActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().setTitle("Graph");


        bottomNavigationView = addBottomMenu();
        repo = new Repository(getApplicationContext());
        exposureCheck = new ExposureCheckViewModel(getApplication(), repo);
        pastLocationsList = exposureCheck.getAllFipsFromLastTwoWeeks();
        viewModel = new ViewModelProvider(this).get(GraphActivityViewModel.class);
        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateGraph();
            }
        });
    }

    /**
     * switches to the corresponding activity based on which icon is selected in the bottom menu
     */
    public BottomNavigationView addBottomMenu() {
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
                    case R.id.generalInfoBottomMenu:
                        Intent generalInfoIntent = new Intent(GraphActivity.this, GeneralInformationActivity.class);
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
        bottomNavigationView.setSelectedItemId(R.id.graphBottomMenu);
        super.onResume();
    }


    /**
     * processes data from the view model to update the graph
     */
    public void updateGraph() {
        ArrayList<ChartData> rollingAvg = new ArrayList<ChartData>(Arrays.asList(viewModel.rollingAvg.toArray(new ChartData[0])));
        ArrayList<BarEntry> rollingAvgEntries = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<Float> rollingAvgState = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        float highestValue = 10;
        if (!rollingAvg.isEmpty()) {
            int rollingAvgSize = rollingAvg.size();
            for (int i = 0; i < rollingAvgSize; i++) {
                ChartData chartData = rollingAvg.get(i);
                float casesCounty = (float) chartData.getWeek2RollingAvgCounty();
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
        BarChart pastLocationChart = (BarChart) findViewById(R.id.pastLocationChart);
        formatRightYAxis(pastLocationChart);
        formatLeftYAxis(pastLocationChart, states, rollingAvgState, highestValue);
        formatXAxis(pastLocationChart, xAxisLabel);
        addDataToBarChart(pastLocationChart, rollingAvgEntries);
        formatBarChart(pastLocationChart);
    }

    /**
     * Disables the y axis on the right side so there aren't duplicate axes
     *
     * @param pastLocationChart bar chart which displays the rolling average of new cases for counties
     */
    public void formatRightYAxis(BarChart pastLocationChart) {
        YAxis yAxisRight = pastLocationChart.getAxisRight();
        yAxisRight.setEnabled(false);
    }

    /**
     * Formats the y axis to properly scale based on the data, and  to include a line for
     * each of the states across the bar graph
     *
     * @param pastLocationChart bar chart which displays the rolling average of new cases for counties
     * @param states            the states for each of the selected counties
     * @param rollingAvgState   the rolling average for each of the states
     * @param highestValue      the highest recorded value which is used to set the y axis
     */
    public void formatLeftYAxis(BarChart pastLocationChart, ArrayList<String> states,
                                ArrayList<Float> rollingAvgState, float highestValue) {
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
                    LimitLine stateLine = new LimitLine(rollingAvgStateValue,
                                                        stateName + " Average New Cases");
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
    }

    /**
     * formats the x axis lables and adds the county names as labels
     *
     * @param pastLocationChart bar chart which displays the rolling average of new cases for counties
     * @param xAxisLabel        the names of the counties for the x axis label
     */
    public void formatXAxis(BarChart pastLocationChart, ArrayList<String> xAxisLabel) {
        XAxis xAxis = pastLocationChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-60f);
    }

    /**
     * adds the data to the bar chart
     *
     * @param pastLocationChart bar chart which displays the rolling average of new cases for counties
     * @param rollingAvgEntries all the entries for the bar chart based on the county rolling average
     */
    public void addDataToBarChart(BarChart pastLocationChart, ArrayList<BarEntry> rollingAvgEntries) {
        BarDataSet set = new BarDataSet(rollingAvgEntries, "Average New Cases");
        set.setColor(getColor(R.color.colorPrimary));
        BarData data = new BarData(set);
        data.setDrawValues(false);
        pastLocationChart.setData(data);
    }

    /**
     * formats the bar chart to auto scale the bars in the chart based on the number of bars
     *
     * @param pastLocationChart bar chart which displays the rolling average of new cases for counties
     */
    public void formatBarChart(BarChart pastLocationChart) {
        Description description = pastLocationChart.getDescription();
        description.setEnabled(false);
        pastLocationChart.setNoDataText("Currently there appear to be no locations recorded.");
        pastLocationChart.setNoDataTextColor(getColor(R.color.lb_default_brand_color));
        pastLocationChart.setFitBars(true);
        pastLocationChart.invalidate();
    }


}
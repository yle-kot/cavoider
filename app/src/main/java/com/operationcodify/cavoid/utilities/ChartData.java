package com.operationcodify.cavoid.utilities;

import com.github.mikephil.charting.charts.Chart;

public class ChartData implements Comparable<ChartData> {
    double week2RollingAvgCounty;
    double week2RollingAvgState;
    String county;
    String state;

    public ChartData(double week2RollingAvgCounty, double week2RollingAvgState, String county, String state) {
        this.week2RollingAvgCounty = week2RollingAvgCounty;
        this.week2RollingAvgState = week2RollingAvgState;
        this.county = county;
        this.state = state;
    }

    public ChartData(double week2RollingAvgCounty, String county) {
        this.week2RollingAvgCounty = week2RollingAvgCounty;
        this.county = county;
    }

    public double getWeek2RollingAvgCounty() {
        return week2RollingAvgCounty;
    }

    public double getWeek2RollingAvgState() {
        return week2RollingAvgState;
    }

    public String getCounty() {
        return county;
    }

    public String getState() {
        return state;
    }

    @Override
    public int compareTo(ChartData o) {
        if (this.getWeek2RollingAvgCounty() > o.getWeek2RollingAvgCounty()) {
            return 1;
        }
        else if (this.getWeek2RollingAvgCounty() == o.getWeek2RollingAvgCounty()) {
            return 0;
        }
        else {
            return -1;
        }
    }
}

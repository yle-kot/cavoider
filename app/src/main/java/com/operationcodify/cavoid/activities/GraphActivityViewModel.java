package com.operationcodify.cavoid.activities;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.LocationDao;
import com.operationcodify.cavoid.database.LocationDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GraphActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;
    public PriorityQueue<ChartData> rollingAvg;
    public String fips;
    public String TAG;
    public int i;
    private Repository repo;
    private MutableLiveData<Integer> counter;
    private ArrayList<String> pastLocations;
    private CasesCompare casesCompare = new CasesCompare();

    public GraphActivityViewModel(@NonNull Application application){
        super(application);
        locDb = LocationDatabase.getDatabase(getApplication().getApplicationContext());
        locDao = locDb.getLocationDao();
        pastLocations = (ArrayList<String>) locDao.getAllDistinctFips();
        rollingAvg = new PriorityQueue<>(casesCompare);
        TAG = DashboardActivityViewModel.class.getName();
        repo = new Repository(application.getApplicationContext());
        updateDataForChart();
    }

    /**
     * @return a counter which is used to determine if all of the threads have
     * finished and the api call is complete for all fips codes
     */
    public MutableLiveData<Integer> getCounter() {
        if(counter == null){
            counter = new MutableLiveData<Integer>();
            counter.setValue(0);
        }
        return counter;
    }

    /**
     * Creates an api call which gets the data to create a Chart Data object which is added to a
     *      priority queue to ensures that only the top 8 highest counties are added
     * The priority queue saves Chart Data objects which are used by the GraphActivity to
     *      add data to the graph
     */
    public void updateDataForChart(){
        for(i = 0; i < pastLocations.size();i++) {
            repo.getPosTests(pastLocations.get(i), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        double rollingAvgForCounty = (double) response.getDouble("week_2_rolling_avg_per_100k_people");
                        double rollingAvgForState = (double) response.getDouble("state_week_2_rolling_avg_per_100k_people");
                        String county = response.getString("county");
                        String state = response.getString("state_abbreviation");

                        ChartData chartData = new ChartData(rollingAvgForCounty, rollingAvgForState, county, state);
                        if (rollingAvg.size() < 8) {
                            rollingAvg.add(chartData);
                        }
                        else {
                            ChartData lowest = rollingAvg.peek();
                            if (casesCompare.compare(chartData, lowest) > 0) {
                                rollingAvg.poll();
                                rollingAvg.add(chartData);
                            }
                        }
                        counter.setValue(counter.getValue() + 1);
                    }
                    catch(JSONException j){
                        Log.i(TAG,"Could not get a response!");
                    }
                }
            });
        }
    }

    public static class ChartData {
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
    }

    /**
     * comparator to properly add the counties to the priority queue based on the Chart Data object
     */
    public static class CasesCompare implements Comparator<ChartData> {
        public int compare(ChartData chartData1, ChartData chartData2) {
            if (chartData1.getWeek2RollingAvgCounty() > chartData2.getWeek2RollingAvgCounty()) {
                return 1;
            }
            else if (chartData1.getWeek2RollingAvgCounty() == chartData2.getWeek2RollingAvgCounty()) {
                return 0;
            }
            else {
                return -1;
            }
        }
    }

}

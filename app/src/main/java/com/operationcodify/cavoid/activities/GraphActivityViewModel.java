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
import com.operationcodify.cavoid.utilities.ChartData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class GraphActivityViewModel extends AndroidViewModel {
    public PriorityQueue<ChartData> rollingAvg;
    public String fips;
    public String TAG;
    public int i;
    private LocationDatabase locDb;
    private LocationDao locDao;
    private Repository repo;
    private MutableLiveData<Integer> counter;
    private ArrayList<String> pastLocations;

    public GraphActivityViewModel(@NonNull Application application) {
        super(application);
        locDb = LocationDatabase.getDatabase(getApplication().getApplicationContext());
        locDao = locDb.getLocationDao();
        pastLocations = (ArrayList<String>) locDao.getAllDistinctFips();
        rollingAvg = new PriorityQueue<>();
        TAG = DashboardActivityViewModel.class.getSimpleName();
        repo = new Repository(application.getApplicationContext());
        updateDataForChart();
    }

    /**
     * @return a counter which is used to determine if all of the threads have
     * finished and the api call is complete for all fips codes
     */
    public MutableLiveData<Integer> getCounter() {
        if (counter == null) {
            counter = new MutableLiveData<Integer>();
            counter.setValue(0);
        }
        return counter;
    }

    /**
     * Creates an api call which gets the data to create a Chart Data object which is added to a
     * priority queue to ensures that only the top 8 highest counties are added
     * The priority queue saves Chart Data objects which are used by the GraphActivity to
     * add data to the graph
     */
    public void updateDataForChart() {
        for (i = 0; i < pastLocations.size(); i++) {
            repo.getPosTests(pastLocations.get(i), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        createChartDataFromResponse(response);
                        counter.setValue(counter.getValue() + 1);
                    } catch (JSONException j) {
                        Log.i(TAG, "Could not get a response!");
                    }
                }
            });
        }
    }

    private void createChartDataFromResponse(JSONObject response) throws JSONException {
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
            if (chartData.compareTo(lowest) > 0) {
                rollingAvg.poll();
                rollingAvg.add(chartData);
            }
        }
    }

}

package com.example.cavoid.activities;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;
    private PastLocation mostRecentLocation;
    public String newCaseNumber;
    public String newDeathNumber;
    public String activeCases;
    public String totalCases;
    public String countyName;
    public String totalDeaths;
    private Repository repository;
    public String TAG;
    private MutableLiveData<Integer> counter;

    public DashboardActivityViewModel(@NonNull Application application) {
        super(application);
        locDb = LocationDatabase.getDatabase(getApplication().getApplicationContext());
        locDao = locDb.getLocationDao();
        mostRecentLocation = locDao.getLatestLocation();
        if (mostRecentLocation == null) {
            Log.w(TAG, "No saved locations returned from db!");
        }
        repository = new Repository(getApplication().getApplicationContext());
        TAG = DashboardActivityViewModel.class.getName();
        updateDailyValues();
    }

    public MutableLiveData<Integer> getCounter() {
        if(counter == null){
            counter = new MutableLiveData<Integer>();
            counter.setValue(0);
        }
        return counter;
    }

    public void updateDailyValues(){
        repository.getPosTests(mostRecentLocation.fips, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //String reportDate = response.getString("report_date");
                    newCaseNumber = response.getString("new_daily_cases");
                    newDeathNumber = response.getString("new_daily_deaths");
                    activeCases = response.getString("active_cases_est");
                    totalCases = response.getString("cases");
                    totalDeaths = response.getString("deaths");
                    countyName = response.getString("county_name");

                    counter.setValue(counter.getValue() + 1);

                } catch (JSONException e) {
                    Log.w(TAG, "Could not get data from JSON response!");
                    e.printStackTrace();
                }
            }
        });
    }
}

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
import com.operationcodify.cavoid.database.PastLocation;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;
    private PastLocation mostRecentLocation;
    public String activeCasesEst;
    public String caseFatality;
    public String deathsPer100K;
    public String state;
    public String fips;
    public String casesPer100K;
    public String reportDate;
    public String newCaseNumber;
    public String newDeathNumber;
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
        if (mostRecentLocation == null){
            return;
        }
        repository.getPosTests(mostRecentLocation.fips, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    activeCasesEst = response.getString("active_cases_est");
                    caseFatality = response.getString("case_fatality");
                    totalCases = response.getString("cases");
                    casesPer100K = response.getString("cases_per_100k_people");
                    deathsPer100K = response.getString("deaths_per_100k_people");
                    newCaseNumber = response.getString("new_daily_cases");
                    newDeathNumber = response.getString("new_daily_deaths");
                    totalDeaths = response.getString("deaths");
                    countyName = response.getString("county");
                    fips = response.getString("fips");
                    reportDate = response.getString("report_date");
                    state = response.getString("state");

                    counter.setValue(counter.getValue() + 1);

                } catch (JSONException e) {
                    Log.w(TAG, "Could not get data from JSON response!");
                    e.printStackTrace();
                }
            }
        });
    }
}

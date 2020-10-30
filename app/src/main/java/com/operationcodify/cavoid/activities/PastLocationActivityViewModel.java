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

import java.util.ArrayList;

public class PastLocationActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;
    public String activeCasesEst;
    public String caseFatality;
    public String deathsPer100K;
    public String state;
    public String fips;
    public String casesPer100K;
    public String newCaseNumber;
    public String newDeathNumber;
    public String totalCases;
    public String countyName;
    public String totalDeaths;
    public String TAG;
    public String reportDate;
    public int i;
    private Repository repo;
    private MutableLiveData<Integer> counter;
    private ArrayList<String> pastLocations;
    public ArrayList<String> messages;

    public PastLocationActivityViewModel(@NonNull Application application){
        super(application);
        locDb = LocationDatabase.getDatabase(getApplication().getApplicationContext());
        locDao = locDb.getLocationDao();
        pastLocations = (ArrayList<String>) locDao.getAllDistinctFips();
        messages = new ArrayList<String>();
        TAG = DashboardActivityViewModel.class.getName();
        repo = new Repository(application.getApplicationContext());
        updatePastLocationMessages();
    }

    public MutableLiveData<Integer> getCounter() {
        if(counter == null){
            counter = new MutableLiveData<Integer>();
            counter.setValue(0);
        }
        return counter;
    }

    public void updatePastLocationMessages(){
        for(i = 0; i < pastLocations.size();i++) {
            repo.getPosTests(pastLocations.get(i), new Response.Listener<JSONObject>() {
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
                        messages.add(countyName + " New cases: " + newCaseNumber + "  Active Cases: " + activeCasesEst + " Total Cases: " + totalCases);
                        messages.add(countyName + " New deaths: " + newDeathNumber + " Total Deaths: " + totalDeaths);
                        counter.setValue(counter.getValue() + 1);
                    }
                    catch(JSONException j){
                        Log.i(TAG,"Could not get a response!");
                    }
                }
            });
        }
    }
}

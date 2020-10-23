package com.example.cavoid.activities;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.database.LocationDao;
import com.example.cavoid.database.LocationDatabase;
import com.example.cavoid.database.PastLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//TODO Implement live data for the pastlocationactivity
public class PastLocationActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;
    private PastLocation mostRecentLocation;
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
    private Repository repo;
    private MutableLiveData<Integer> counter;
    private ArrayList<PastLocation> pastLocations;
    private MutableLiveData<ArrayList<String>> messages;

    public PastLocationActivityViewModel(@NonNull Application application){
        super(application);
        locDb = LocationDatabase.getDatabase(getApplication().getApplicationContext());
        locDao = locDb.getLocationDao();
        pastLocations = (ArrayList<PastLocation>) locDao.getAll();
        TAG = DashboardActivityViewModel.class.getName();
        repo = new Repository(application.getApplicationContext());
        updatePastLocations();
    }

    //TODO:Make this method update past locations then in PastLocationAdapter update when the data changes using an observer(DashboardActivity, android tutorial page)
    //TODO:Make sure this works
    public void updatePastLocations(){
        for(PastLocation p: pastLocations) {
            repo.getPosTests(p.fips, new Response.Listener<JSONObject>() {
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
                        //reportDate = response.getString("report_date");
                        state = response.getString("state");
                    }
                    catch(JSONException j){

                    }
                }
            });
        }
    }
}

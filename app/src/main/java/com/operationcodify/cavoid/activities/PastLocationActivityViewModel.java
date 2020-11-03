package com.operationcodify.cavoid.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.operationcodify.cavoid.api.Repository;
import com.operationcodify.cavoid.database.LocationDao;
import com.operationcodify.cavoid.database.LocationDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeSet;

public class PastLocationActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;

    public String TAG;
    public int i;
    private Repository repo;
    private MutableLiveData<Integer> counter;
    private ArrayList<String> pastLocations;
    private TreeSet<ParsedPastLocationReport> reports;

    public PastLocationActivityViewModel(@NonNull Application application){
        super(application);
        locDb = LocationDatabase.getDatabase(getApplication().getApplicationContext());
        locDao = locDb.getLocationDao();
        pastLocations = (ArrayList<String>) locDao.getAllDistinctFips();
        reports = new TreeSet<>();
        TAG = DashboardActivityViewModel.class.getName();
        repo = new Repository(application.getApplicationContext());
        updatePastLocationMessages();
    }

    public TreeSet<ParsedPastLocationReport> getReports(){
        return this.reports;
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
                    ParsedPastLocationReport newReport = new ParsedPastLocationReport(response, locDao);
                    reports.add(newReport);
                    counter.setValue(counter.getValue() + 1);
                }
            });
        }
    }
}


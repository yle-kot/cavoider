package com.operationcodify.cavoid.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.operationcodify.cavoid.api.Repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExposureCheckViewModel extends AndroidViewModel {
    private Repository APIRepository;
    private ArrayList<String> allFipsFromLastTwoWeeks;
    private volatile int counter;
    private volatile ArrayList<String> fipsToNotify;
    private volatile MutableLiveData<Boolean> isDone;


    public ExposureCheckViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.APIRepository = repository;
        this.fipsToNotify = new ArrayList<String>();
        this.allFipsFromLastTwoWeeks = getPastFips(getApplication().getApplicationContext());
        this.isDone = new MutableLiveData<>();
        this.isDone.setValue(false);
        fipsToNotify(getApplication().getApplicationContext(), APIRepository, allFipsFromLastTwoWeeks);
    }

    public ArrayList<String> getAllFipsFromLastTwoWeeks(){
        return allFipsFromLastTwoWeeks;
    }

    public MutableLiveData<Boolean> getIsDone(){
        return isDone;
    }


    private ArrayList<String> getPastFips(Context context) {
        LocationDatabase locDb = LocationDatabase.getDatabase(context.getApplicationContext());
        LocationDao dao = locDb.getLocationDao();
        LocalDate startDate = DateTime.now().toLocalDate();
        LocalDate[] dateList = new LocalDate[15];
        int arrayIndex = 0;
        for (int i = 1; i <= 14; i++) {
            LocalDate prevDay = startDate.minusDays(i);
            dateList[arrayIndex] = prevDay;
            arrayIndex++;
        }
        List<PastLocation> pastLocations = dao.loadAllByDates(dateList);
        ArrayList<String> pastFips = new ArrayList<String>();
        for (PastLocation location : pastLocations) {
            pastFips.add(location.fips);
        }
        return pastFips;
    }

    public void fipsToNotify(Context context, Repository repo, ArrayList<String> pastLocations) {
        for (String location : pastLocations) {
            repo.getPosTests(location, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    double week1 = 0;
                    double week2 = 0;
                    String countyName = "";
                    try {
                        week1 = response.getDouble("week_1_rolling_avg_per_100k_people");
                        week2 = response.getDouble("week_2_rolling_avg_per_100k_people");
                        countyName = response.getString("county");
                    } catch (JSONException e) {
                        Log.e("fipsToNotify", "Expected week_1_rolling_avg_per_100k_people & week_2_rolling_avg_per_100k_people to be a string" + e.getMessage());
                    }
                    if ((int)Math.round(week2) > (int)Math.round(week1)) {
                        fipsToNotify.add(countyName);
                        synchronized (ExposureCheckViewModel.class){
                            counter = counter + 1;
                            isDone.setValue(counter == pastLocations.size());
                        }
                    }
                }
            });
        }
    }
}

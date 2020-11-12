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

import java.math.BigDecimal;
import java.math.MathContext;

public class DashboardActivityViewModel extends AndroidViewModel {
    private LocationDatabase locDb;
    private LocationDao locDao;
    private PastLocation mostRecentLocation;
    public double activeCasesEst;
    public double caseFatality;
    public double deathsPer100K;
    public double casesPer100K;
    public double newCaseNumber;
    public double newDeathNumber;
    public double totalCases;
    public double totalDeaths;
    public String activeCasesEst2;
    public String caseFatality2;
    public String  deathsPer100K2;
    public String casesPer100K2;
    public String newCaseNumber2;
    public String newDeathNumber2;
    public String totalCases2;
    public String totalDeaths2;
    public String reportDate;
    public String state;
    public String countyName;
    public String fips;
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

                    activeCasesEst = Double.parseDouble(response.getString("active_cases_est"));
                    BigDecimal  ACEdec = new BigDecimal(activeCasesEst);
                    ACEdec = ACEdec.round(new MathContext(7));
                    activeCasesEst2 = ACEdec.toString();

                    caseFatality = Double.parseDouble(response.getString("case_fatality"));
                    BigDecimal  CFdec = new BigDecimal(caseFatality);
                    CFdec = CFdec.round(new MathContext(2));
                    caseFatality2 = CFdec.toString();
                    caseFatality2 = "%" + caseFatality2;

                    totalCases = Double.parseDouble(response.getString("cases"));
                    BigDecimal  TCdec = new BigDecimal(totalCases);
                    TCdec = TCdec.round(new MathContext(6));
                    totalCases2 = TCdec.toString();

                    casesPer100K = Double.parseDouble(response.getString("cases_per_100k_people"));
                    BigDecimal  CPdec = new BigDecimal(casesPer100K);
                    CPdec = CPdec.round(new MathContext(6));
                    casesPer100K2 = CPdec.toString();

                    deathsPer100K = Double.parseDouble(response.getString("deaths_per_100k_people"));
                    BigDecimal  DPdec = new BigDecimal(deathsPer100K);
                    DPdec = DPdec.round(new MathContext(3));
                    deathsPer100K2 = DPdec.toString();

                    newCaseNumber = Double.parseDouble(response.getString("new_daily_cases"));
                    BigDecimal  NCdec = new BigDecimal(newDeathNumber);
                    NCdec = NCdec.round(new MathContext(3));
                    newCaseNumber2 = NCdec.toString();

                    newDeathNumber = Double.parseDouble(response.getString("new_daily_deaths"));
                    BigDecimal NDdec = new BigDecimal(newDeathNumber);
                    NDdec = NDdec.round(new MathContext(3));
                    newDeathNumber2 = NDdec.toString();

                    totalDeaths = Double.parseDouble(response.getString("deaths"));
                    BigDecimal  TDdec = new BigDecimal(totalDeaths);
                    TDdec = TDdec.round(new MathContext(3));
                    totalDeaths2 = TDdec.toString();

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

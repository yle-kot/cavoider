package com.example.cavoid.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.example.cavoid.api.Repository;
import com.example.cavoid.utilities.AppNotificationHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class DailyCovidTrendWorker extends Worker {
    public DailyCovidTrendWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        notifyOfCurrentCovidTrend(getApplicationContext());
        return Result.success();
    }

    private void notifyOfCurrentCovidTrend(final Context context){
        Repository repository = new Repository();
        repository.getPosTests(context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject data = response;
                String posTests;
                //Saves the positive case number from JSON file to string in application
                try{
                    posTests = data.getString("case_trend_14_days");
                }catch (JSONException e){
                    posTests = "ERR";
                }

                String title = "Daily COVID Trend Alert";
                StringBuilder message = new StringBuilder("COVID is trending");
                try {
                    message.append(Float.parseFloat(posTests) > 0 ? "upwards" : "downwards");
                }
                catch (NumberFormatException ex) {
                    message.append("flat");
                }
                message.append(" in your area.");
                AppNotificationHandler.deliverNotification(context, title,message.toString());
            }
        });
    }
}

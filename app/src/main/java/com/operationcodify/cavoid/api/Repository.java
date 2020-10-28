package com.operationcodify.cavoid.api;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;

public class Repository {

    private String posTests = "";
    private Context context;

    public Repository(Context context){
        this.context = context;
    }

    public void getPosTests(String fips, Response.Listener<JSONObject> callback){
        RequestQueue queue = Volley.newRequestQueue(context);

        //Saves url as string to be searched on the web
        String url = String.format("https://powerful-anchorage-13412.herokuapp.com/latest/%s",fips);

        //Object request gets the JSON object from the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, callback, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("Our API Handler", "No response from API");
                        Log.w("Our API Handler", error);

                    }
                });
        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    public void getFipsCodeFromCurrentLocation(Location location, Response.Listener<JSONObject> callback) throws IOException {
        String baseUrl = "https://geo.fcc.gov/api/census/area?";
        String latitude = "lat="+location.getLatitude()+"&";//37.549550,-77.451244
        String longitude = "lon="+location.getLongitude();
        baseUrl += latitude+longitude;
        String fips = "";
        //Might need countyName string for dashboard
        String countyName = "";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, callback, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);

    }


}

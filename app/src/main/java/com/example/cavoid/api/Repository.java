package com.example.cavoid.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Repository {
    String posTests = "";
    public static void getPosTests(Context context, String fips, Response.Listener<JSONObject> callback){
        RequestQueue queue = Volley.newRequestQueue(context);

        //Saves url as string to be searched on the web
        String url = "https://api.covidtracking.com/v1/states/va/20200918.json";

        //Object request gets the JSON object from the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, callback, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    public static void getCurrentLocationFromFipsCode(Context context, double lat, double lon, Response.Listener<JSONObject> callback) throws IOException {
        String baseUrl = "https://geo.fcc.gov/api/census/area?";
        String latitude = "lat="+lat+"&";
        String longitude = "lon="+lon;
        baseUrl += latitude+longitude;
        String fips = "";
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

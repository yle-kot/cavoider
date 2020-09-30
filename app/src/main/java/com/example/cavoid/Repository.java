package com.example.cavoid;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Repository {
    String posTests = "";
    public String getPosTests(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);

        //Saves url as string to be searched on the web
        String url = "https://api.covidtracking.com/v1/states/va/20200918.json";

        //Object request gets the JSON object from the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = response;
                        //Saves the positive case number from JSON file to string in application
                        try{
                            posTests = data.getString("Positive");
                        }catch (JSONException e){
                            posTests = "0";
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
        return posTests;
    }
}

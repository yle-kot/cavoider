package com.example.cavoid.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utilities {
    public static String getCurrentLocationFromFipsCode(String lat, String lon) throws IOException, MalformedURLException {
        String baseUrl = "https://geo.fcc.gov/api/census/area?";
        String latitude = "lat="+lat+"&";
        String longitude = "lon="+lon;
        baseUrl += latitude+longitude;
        String fips = "";
        URL url = new URL(baseUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        for(int i = 0;i<response.length();i++){
            if(response.substring(i,i+11).equals("county_fips")){
                fips = response.substring(i+14,i+19);
                break;
            }
        }
        return fips;
    }
}

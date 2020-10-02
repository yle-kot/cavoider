package com.example.cavoid.utilities;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PolygonUtils {
    /*
  Gets the county line coordinates then creates a polygon object to return them
 */
    public static Polygon createCountyPolygon(Context context, GoogleMap mMap){
        Polygon polygon = null;
        polygon = mMap.addPolygon(new PolygonOptions()
                .addAll(getCountyLines(context, "51760")));
        return polygon;
    }

    //Returns an arraylist of county lines from a passed in fips code
    private static ArrayList<LatLng> getCountyLines(Context context, String fips){
        ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
        //create a json object and parse it
        try {
            JSONObject countyJson = new JSONObject(loadJSONFromAsset(context));
            String coordinatesString = countyJson.getString(fips);
            //Enhanced for Loop to split coordinate string by spaces then take that string and split it by commas
            for (String coord: coordinatesString.split(" ")){
                //after spliting the string by commas put the first two indexes of the new array into lat and lng strings
                //then parse them for doubles and add them to coordinates
                String [] coordinateparts = coord.split(",");
                //the latitude and longitude are reversed in the fips.json file
                String Lng = coordinateparts[0];
                String Lat = coordinateparts[1];
                coordinates.add(new LatLng(Double.parseDouble(Lat),Double.parseDouble(Lng)));
            }
            return coordinates;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        catch(NullPointerException n){
            return null;
        }
    }

    //Opens and reads the fips.json file returns a string to create the JSONObject
    private static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            //goes into the assets folder opens the file
            InputStream in = context.getAssets().open("fips.json");
            //is.available returns the number of bytes that can be read
            //then create a byte array of that size to rea then read it close the file and pass back the json string
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            json = new String(buffer, "UTF-8");
            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }



}

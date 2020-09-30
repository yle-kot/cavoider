package com.example.cavoid;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Declare local variables
    private GoogleMap mMap;
    private ArrayList<LatLng> coordinates;
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button notificationTrigger = findViewById(R.id.notificationTrigger);

        notificationTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Repository repository = new Repository();
                repository.getPosTests(MapsActivity.this, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = response;
                        String posTests;
                        //Saves the positive case number from JSON file to string in application
                        try {
                            posTests = data.getString("positive");
                        } catch (JSONException e) {
                            posTests = "ERR";
                        }

                        String title = "Positive Test Alert";
                        String message = posTests;
                        AppNotificationHandler.deliverNotification(MapsActivity.this, title, message);
                    }
                });

            }
        });

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Polygon polygon = createCountyPolygon();
        polygon.setClickable(true);
        polygon.setVisible(true);
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
           @Override
            public void onPolygonClick(Polygon polygon){
                polygon.setFillColor(Color.RED);

            }
        });

        // Add a marker in Sydney and move the camera
        LatLng ashland = new LatLng(37.75, -77.85);
        mMap.addMarker(new MarkerOptions().position(ashland).title("Marker in Ashland sort of"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ashland));
    }


    //Gets the county line coordinates then creates a polygon object to return them
    private Polygon createCountyPolygon(){
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .strokeWidth((float) 5.0)
                .strokeColor(Color.BLACK)
                //01001 is the tester fips code
                .addAll(getCountyLines("01001")));
        return polygon;
    }

    //Returns an arraylist of county lines from a passed in fips code
    private ArrayList<LatLng> getCountyLines(String fips){
        coordinates = new ArrayList<LatLng>();
        //create a json object and parse it
        try {
            JSONObject countyJson = new JSONObject(loadJSONFromAsset());
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
    private String loadJSONFromAsset() {
        String json = null;
        try {
            //goes into the assets folder opens the file
            InputStream in = getAssets().open("fips.json");
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

    private String getCurrentLocationFipsCode(String lat, String lon) throws IOException, MalformedURLException {
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
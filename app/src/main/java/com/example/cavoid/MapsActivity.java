package com.example.cavoid;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Response;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import org.json.JSONArray;
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
import java.util.Calendar;
import java.util.Scanner;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
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
                                try{
                                    posTests = data.getString("positive");
                                }catch (JSONException e){
                                    posTests = "ERR";
                                }

                                String title = "Positive Test Alert";
                                String message = posTests;
                                AppNotificationHandler.deliverNotification(MapsActivity.this,title,message);
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
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon){
                System.out.println("click");
                polygon.setFillColor(Color.RED);
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng ashland = new LatLng(37.75, -77.85);
        mMap.addMarker(new MarkerOptions().position(ashland).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ashland));


    }

    /*
      Gets the county line coordinates then creates a polygon object to return them
     */
    private Polygon createCountyPolygon(){
        Polygon polygon = null;
        polygon = mMap.addPolygon(new PolygonOptions()
                .addAll(getCountyLines("01001")));
        return polygon;
    }

    private ArrayList<LatLng> getCountyLines(String fips){
        ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
        //create a json object and parse it
        try {
            JSONObject county = new JSONObject(loadJSONFromAsset());
            String coordinatesString = county.getString(fips);
            System.out.println(coordinatesString);
            String Lat;
            String Lng;
            Scanner in = new Scanner(coordinatesString);
            in.useDelimiter(",");
            //return county.get(fips);
            for(int i = 0; i+1 < coordinatesString.length();i++){
                Lat = in.next();
                Lng = in.next();
                coordinates.add(new LatLng(Double.parseDouble(Lat),Double.parseDouble(Lng)));
            }
            return coordinates;
        }
        catch (JSONException e) {
            e.printStackTrace();
            coordinates.add(new LatLng(41, -109));
            coordinates.add(new LatLng(41, -102));
            coordinates.add(new LatLng(37, -102));
            coordinates.add(new LatLng(37, -109));
            return coordinates;
        }
        catch(NullPointerException n){
            coordinates.add(new LatLng(41, -109));
            coordinates.add(new LatLng(41, -102));
            coordinates.add(new LatLng(37, -102));
            coordinates.add(new LatLng(37, -109));
            return coordinates;
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("fips.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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
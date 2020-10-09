package com.example.cavoid.activities;

import com.example.cavoid.api.Utilities;
import com.example.cavoid.utilities.PolygonUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Response;
import com.example.cavoid.utilities.AppNotificationHandler;
import com.example.cavoid.R;
import com.example.cavoid.api.Repository;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    protected FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    public OnSuccessListener<Location> onLatestLocationSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String fips = null;
                try {
                    Repository.getCurrentLocationFromFipsCode(MapsActivity.this, latitude, longitude, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            String fips = "";

                            try {
                                fips = response.getString("county_fips");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Repository.getPosTests(getApplicationContext(), fips, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    AppNotificationHandler.deliverNotification(getApplicationContext(), "Test Title", response.toString());
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        setContentView(R.layout.activity_maps);
        Button notificationTrigger = findViewById(R.id.notificationTrigger);
        notificationTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (
                        ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_DENIED
                        && ActivityCompat.checkSelfPermission(
                                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_DENIED
                ) {
                    String[] requiredPermissions = {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };
                    ArrayList<String> missingPermissions = new ArrayList<String>();
                    for (int i = 0; i < requiredPermissions.length; i++){
                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, requiredPermissions[i]) != PackageManager.PERMISSION_GRANTED){
                            missingPermissions.add(requiredPermissions[i]);
                        }
                    }

                    if (missingPermissions.size() > 0){
                            ActivityCompat.requestPermissions(MapsActivity.this, (String []) missingPermissions.toArray(), 1);
                    }
                }
                else{
                    fusedLocationClient.getLastLocation().addOnSuccessListener(MapsActivity.this, onLatestLocationSuccessListener);
                }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){
        boolean anyGranted = false;
        if (requestCode != 1)
            return;

        if (permissions.length == 0 || permissions[0] == null){
            Toast.makeText(MapsActivity.this,"Cannot perform action without location permissions!", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            anyGranted |= grantResults[i] == PackageManager.PERMISSION_GRANTED;
        }

        if (anyGranted){
            fusedLocationClient.getLastLocation().addOnSuccessListener(MapsActivity.this, onLatestLocationSuccessListener);
        }
        else{
            Toast.makeText(MapsActivity.this,"Cannot perform action without location permissions!", Toast.LENGTH_LONG).show();
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

//        Polygon polygon = PolygonUtils.createCountyPolygon(MapsActivity.this, mMap);
//        polygon.setClickable(true);
//        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
//            @Override
//            public void onPolygonClick(Polygon polygon){
//                System.out.println("click");
//                polygon.setFillColor(Color.RED);
//            }
//        });

    }



}
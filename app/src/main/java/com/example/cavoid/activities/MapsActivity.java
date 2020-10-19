package com.example.cavoid.activities;

import android.app.NotificationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.cavoid.R;
import com.example.cavoid.utilities.GeneralUtilities;
import com.example.cavoid.workers.DailyCovidTrendUpdateWorker;
import com.example.cavoid.workers.GetWorker;
import com.example.cavoid.workers.RegularLocationSaveWorker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.concurrent.TimeUnit;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createWorkers(GeneralUtilities.getSecondsUntilHour(8));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        Button notificationTrigger = findViewById(R.id.notificationTrigger);
        notificationTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Implement settings screen
                Toast.makeText(MapsActivity.this, "Button was pressed lol", Toast.LENGTH_SHORT).show();
            }
        });

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }

    }


    protected void createWorkers(long delay) {
        WorkManager mWorkManager = WorkManager.getInstance(this);
        OneTimeWorkRequest GetRequest = new OneTimeWorkRequest.Builder(GetWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        OneTimeWorkRequest CovidRequest = new OneTimeWorkRequest.Builder(DailyCovidTrendUpdateWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        PeriodicWorkRequest SaveLocationRequest = new PeriodicWorkRequest.Builder(RegularLocationSaveWorker.class, 20, TimeUnit.MINUTES).build();


        mWorkManager.enqueueUniquePeriodicWork(GetRequest.getClass().getName(), ExistingPeriodicWorkPolicy.REPLACE, SaveLocationRequest);
        mWorkManager.enqueue(CovidRequest);
        mWorkManager.enqueue(GetRequest);
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
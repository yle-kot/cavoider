package com.example.cavoid;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
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
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .addAll(getCountyLines("51760")));
        return polygon;
    }

    private ArrayList<LatLng> getCountyLines(String fips){
        ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
        try {
            JSONObject county = new JSONObject(loadJSONFromAsset());
            JSONArray countyLines = new JSONArray(fips);
            for(int i = 0; i < countyLines.length(); i++) {

            }
            return coordinates;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //coordinates.add(new LatLng(41, -109));
        //coordinates.add(new LatLng(41, -102));
        //coordinates.add(new LatLng(37, -102));
        //coordinates.add(new LatLng(37, -109));
        return coordinates;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("users_list.json");
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


}
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.kml.*;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static com.example.cavoid.R.raw.cb_2018_us_county_500k;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String APIKEY = BuildConfig.ApiKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        KmlLayer layer = null;
        try {
            layer = new KmlLayer(mMap, cb_2018_us_county_500k, getApplicationContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        layer.addLayerToMap();

        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(41, -109), new LatLng(41, -102), new LatLng(37, -103), new LatLng(37, -109))
                .strokeColor(Color.BLACK)
                .fillColor(Color.BLUE)
                .clickable(true));

        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon){
                polygon.setFillColor(Color.RED);
                polygon.setVisible(false);
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng ashland = new LatLng(37.75, -77.85);
        mMap.addMarker(new MarkerOptions().position(ashland).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ashland));
    }
}
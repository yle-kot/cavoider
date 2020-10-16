package com.example.cavoid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.cavoid.R;
import com.example.cavoid.database.ExposureCheck;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ArrayList<String> pastFips = new ArrayList<String>();
        pastFips.add("51760");
        ArrayList<String> fips = ExposureCheck.fipsToNotify(getApplicationContext(), pastFips);
        final TextView fips_code = (TextView) findViewById(R.id.fips_code);
        if (fips.size() > 0) {
            fips_code.setText(fips.get(0));
        }
    }
}
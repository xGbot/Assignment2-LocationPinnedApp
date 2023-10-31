package com.example.locationpinnedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button search = findViewById(R.id.button_search);
        TextView result = findViewById(R.id.textview_result);
        EditText lat = findViewById(R.id.edittext_latitude);
        EditText lon = findViewById(R.id.edittext_longitude);

        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String address = "900 McGill Road";
            try {
                List<Address> ls= geocoder.getFromLocationName(address, 5);
                for (Address addr: ls) {
                    double latitude = addr.getLatitude();
                    double longitude = addr.getLongitude();
                    System.out.println(addr);
                }
            } catch (IOException e) {

            }
        }



    }
}
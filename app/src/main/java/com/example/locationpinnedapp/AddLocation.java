package com.example.locationpinnedapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class AddLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);
        
        // Get views
        Button cancelButton = findViewById(R.id.button_cancel);
        Button addButton = findViewById(R.id.button_add);
        EditText latitudeET = findViewById(R.id.edittext_latitude);
        EditText longitudeET = findViewById(R.id.edittext_longitude);
        
        // Return to main screen
        cancelButton.setOnClickListener(v -> finish());
        
        // Add new address
        addButton.setOnClickListener(v -> {
            String latitude = latitudeET.getText().toString();
            String longitude = longitudeET.getText().toString();
            insertAddress(latitude, longitude);
        });
    }
    
    public void insertAddress(String latitude, String longitude) {
        Geocoder geocoder = new Geocoder(this);
        DatabaseHelper db = new DatabaseHelper(this);
        db.getWritableDatabase();

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude),
                    Double.parseDouble(longitude), 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                
                if (db.findAddress(fullAddress).getCount() > 0) {
                    Toast.makeText(this, "Address already exists", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
                    values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
                    values.put(DatabaseHelper.COLUMN_ADDRESS, fullAddress);
                    long rowID = db.insert(values);

                    if (rowID == -1) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        finish();
                    }
                }
            }    
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        
        
    }
    
}
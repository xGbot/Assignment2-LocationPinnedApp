package com.example.locationpinnedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class EditAddress extends AppCompatActivity {

    private TextView address;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        // Get bundle to retrieve info
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();

        // Get views
        address = findViewById(R.id.textview_address);
        progress = findViewById(R.id.progress_finding);
        EditText latitude = findViewById(R.id.edittext_latitude);
        EditText longitude = findViewById(R.id.edittext_longitude);
        Button cancel = findViewById(R.id.button_cancel);
        Button search = findViewById(R.id.button_search);
        Button save = findViewById(R.id.button_save);

        // Put bundle info into views
        address.setText(bundle.getString(DatabaseHelper.COLUMN_ADDRESS));
        latitude.setText(bundle.getString(DatabaseHelper.COLUMN_LATITUDE));
        longitude.setText(bundle.getString(DatabaseHelper.COLUMN_LONGITUDE));
        String id = bundle.getString(DatabaseHelper.COLUMN_ID);

        // Go back to main screen
        cancel.setOnClickListener(v -> finish());

        // Search new latitude and longitude
        search.setOnClickListener(v -> {
             Runnable runnable = ()
                     -> runOnUiThread(()
                     -> searchAddress(latitude.getText().toString(), longitude.getText().toString()));

            Thread thread = new Thread(runnable);
            progress.setVisibility(View.VISIBLE);
            address.setVisibility(View.INVISIBLE);
            thread.start();
        });

        // Save new address
        save.setOnClickListener(v -> {
            String newLat = latitude.getText().toString();
            String newLong = longitude.getText().toString();
            String newAdd = address.getText().toString();
            saveAddress(newLat, newLong, newAdd, id);
        });
    }

    // Search new address from users input
    public void searchAddress(String latitude, String longitude) {
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude),
                    Double.parseDouble(longitude), 1);

            if (addresses != null && addresses.size() > 0) {
                Address location = addresses.get(0);
                String fullAddress = location.getAddressLine(0);
                address.setText(fullAddress);
                progress.setVisibility(View.GONE);
                address.setVisibility(View.VISIBLE);
            } else {
                address.setText("Not Found");
                progress.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    // Save new address
    public void saveAddress(String latitude, String longitude, String address, String id) {
        DatabaseHelper db = new DatabaseHelper(this);
        SQLiteDatabase sdb = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
        values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
        values.put(DatabaseHelper.COLUMN_ADDRESS, address);

        sdb.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_ID + " LIKE ?",
                new String[]{id});

        db.close();
        sdb.close();
        finish();
    }

}
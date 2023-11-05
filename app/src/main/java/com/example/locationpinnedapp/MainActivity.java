package com.example.locationpinnedapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper db = new DatabaseHelper(this);
        db.getReadableDatabase();

        // Get views
        Button search = findViewById(R.id.button_search);
        EditText address = findViewById(R.id.edittext_address);
        FloatingActionButton fab = findViewById(R.id.fab);

        // Add new location
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), AddLocation.class);
            startActivity(intent);
        });


        // Search for address in db
        search.setOnClickListener(v -> {
            String query = address.getText().toString();
            Cursor result = db.findAddress(query);

            if (result.getCount() == 0) {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            } else if (result.getCount() > 1){
                Toast.makeText(this, result.getCount() + " Results found, be more specific",
                        Toast.LENGTH_SHORT).show();
            } else {
                result.moveToNext();

                String searchedAddress = result.getString(result.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                String searchedLatitude = result.getString(result.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
                String searchedLongitude = result.getString(result.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));
                String searchedId = result.getString(result.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));

                Intent intent = new Intent(this, EditAddress.class);
                Bundle bundle = new Bundle();

                bundle.putString(DatabaseHelper.COLUMN_ADDRESS, searchedAddress);
                bundle.putString(DatabaseHelper.COLUMN_LATITUDE, searchedLatitude);
                bundle.putString(DatabaseHelper.COLUMN_LONGITUDE, searchedLongitude);
                bundle.putString(DatabaseHelper.COLUMN_ID, searchedId);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    // Used once to open file and save latitudes and longitudes into db
    public void insertData() {
        Geocoder geocoder = new Geocoder(this);
        DatabaseHelper db = new DatabaseHelper(this);
        db.getWritableDatabase();

        // Read input
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(getAssets().open("locations.txt")));

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {

                    String latitude = String.valueOf(parts[0]);
                    String longitude = String.valueOf(parts[1]);

                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude),
                            Double.parseDouble(longitude), 1);

                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String fullAddress = address.getAddressLine(0);

                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
                        values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
                        values.put(DatabaseHelper.COLUMN_ADDRESS, fullAddress);
                        long rowID = db.insert(values);
                    }
                }
            }
            in.close();
            db.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
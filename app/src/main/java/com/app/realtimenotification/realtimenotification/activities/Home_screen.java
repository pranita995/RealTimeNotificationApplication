package com.app.realtimenotification.realtimenotification.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.realtimenotification.realtimenotification.R;
import com.app.realtimenotification.realtimenotification.services.AppLocationService;
import com.app.realtimenotification.realtimenotification.services.LocationAddress;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class Home_screen extends AppCompatActivity {

    PlaceAutocompleteFragment autocompleteFragment;
    EditText mEdCurrentLocation;
    Button mButtonStart, mButtonStop;
    AppLocationService appLocationService;
    String lat, lng, latNLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initializedView();
        getView();


        appLocationService = new AppLocationService(Home_screen.this);

        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        } else {
            showSettingsAlert();
        }

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Home_screen.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Home_screen.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void initializedView() {
        mEdCurrentLocation = (EditText) findViewById(R.id.edit_current_location);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mButtonStart = (Button) findViewById(R.id.button_start);
        mButtonStop = (Button) findViewById(R.id.button_stop);
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    lat=bundle.getString("Lat").trim();
                    lng=bundle.getString("Lng").trim();
                    break;
                default:
                    locationAddress = null;
            }
            mEdCurrentLocation.setText(locationAddress);
        }
    }


    public void getView() {
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                autocompleteFragment.setText(place.getName());
               latNLng=place.getLatLng().toString().trim();
                Log.d("++++++++++++++", latNLng);
            }

            @Override
            public void onError(Status status) {
                autocompleteFragment.setText(status.toString());
            }
        });

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Location mylocation = new Location("");
                Location dest_location = new Location("");
                dest_location.setLatitude(Double.parseDouble(lat));
                dest_location.setLongitude(Double.parseDouble(lng));

                mylocation.setLatitude(Double.parseDouble(latNLng));
                mylocation.setLongitude(Double.parseDouble(latNLng));
                Double distance = Double.valueOf(mylocation.distanceTo(dest_location)/1000);
                Toast.makeText(getApplicationContext(), "Distance" + Double.toString(distance), Toast.LENGTH_LONG).show();
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
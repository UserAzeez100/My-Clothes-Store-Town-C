package com.example.towncenterstore.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ActivityLocationBinding;
import com.example.towncenterstore.general.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    GoogleMap mMap;
    ActivityLocationBinding binding;
    Intent intent;
    String mapLat;
    String mapLong;

    private LocationRequest locationRequest;

    public static Activity locationActivity;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getDetailsAddress();
        init();
    }

    private void getDetailsAddress() {
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etLocationDetails.getText().toString().isEmpty() && mapLat != null && mapLong != null) {
                    String detailsTitle = binding.etLocationDetails.getText().toString();
                    HomeActivity.homeActivity.finish();
                    intent = new Intent(LocationActivity.this, HomeActivity.class);
                    intent.putExtra(Constants.DETAILS_TITLE, detailsTitle);
                    intent.putExtra(Constants.STATUS_INTENT_LOCATION, true);
                    intent.putExtra(Constants.MAP_LAT, mapLat);
                    intent.putExtra(Constants.MAP_LONG, mapLong);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LocationActivity.this, getResources().getString(R.string.complete_data), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        locationActivity = this;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentLocation);
        mapFragment.getMapAsync(LocationActivity.this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentLocation();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMap.clear();
                mapLat = String.valueOf(latLng.latitude);
                mapLong = String.valueOf(latLng.longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getResources().getString(R.string.your_current_location)));
            }
        });

    }



    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, getResources().getString(R.string.your_current_location)+"\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isGPSEnabled()) {
                        getCurrentLocation();

                    } else {
                        turnOnGPS();
                    }
                } else {
                    Toast.makeText(LocationActivity.this, getResources().getString(R.string.location_permissions_must_be_given), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }else {
                Toast.makeText(LocationActivity.this, getResources().getString(R.string.location_permissions_must_be_given), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (isGPSEnabled()) {

                LocationServices.getFusedLocationProviderClient(LocationActivity.this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                LocationServices.getFusedLocationProviderClient(LocationActivity.this)
                                        .removeLocationUpdates(this);

                                if (locationResult.getLocations().size() > 0) {

                                    int index = locationResult.getLocations().size() - 1;
                                    double latitude = locationResult.getLocations().get(index).getLatitude();
                                    double longitude = locationResult.getLocations().get(index).getLongitude();

                                    MarkerOptions markerOptions = new MarkerOptions().position(
                                            new LatLng(latitude, longitude)).title(getResources().getString(R.string.your_current_location));
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    mMap.addMarker(markerOptions);

                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

                                    latLng = new LatLng(latitude, longitude);
                                    mapLat = String.valueOf(latitude);
                                    mapLong = String.valueOf(longitude);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getMaxZoomLevel() - 2));
                                }
                            }
                        }, Looper.getMainLooper());
            } else {
                turnOnGPS();
            }

        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LocationActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager;
        boolean isEnabled;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
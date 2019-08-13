package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import Utilities.User;
import Utilities.utils;

public class GetLocation extends AppCompatActivity {
    private final String className = "GetLocation.java";

    private FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_LOCATION_PERMISSION_TRACKER = 21223;

    ProgressBar getLocationProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        if (!User.isUserLoggedIn()) {
            utils.moveToActivity(this, MainActivity.class);
        }

        getLocationProgressBar = (ProgressBar) findViewById(R.id.getLocationProgressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getUserLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!User.isUserLoggedIn()) {
            utils.moveToActivity(this, MainActivity.class);
        }

        getLocationProgressBar = (ProgressBar) findViewById(R.id.getLocationProgressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getUserLocation();
    }

    @Override
    protected void onStart(){
        super.onStart();

        if (!User.isUserLoggedIn()) {
            utils.moveToActivity(this, MainActivity.class);
        }

        getLocationProgressBar = (ProgressBar) findViewById(R.id.getLocationProgressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getUserLocation();
    }

    private Boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(GetLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(GetLocation.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION_TRACKER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_TRACKER: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    utils.logInfo("Requested permission granted", className);
                    getUserLocation();
                } else {
                    // permission denied
                    utils.logError("Requested permission not granted", className);
                }
                return;
            }
        }
    }

    private static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    private void getUserLocation() {

        if (!isLocationEnabled(GetLocation.this)){
            AlertDialog alertDialog = new AlertDialog.Builder(GetLocation.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Location Service Disabled")
                    .setMessage("Please enable location services in settings")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .show();
        } else {
            if (checkPermission()) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    try {
                                        User thisUser = new User();
                                        thisUser.setLocation(location);
                                        thisUser.saveGeoLocation();

                                        // Show success page
                                        utils.moveToActivity(GetLocation.this, VerificationSuccess.class);
                                    } catch (Exception e) {
                                        utils.logError(e.getMessage(), className);
                                        showAlertLocationError("Could not get your location");
                                    }

                                    utils.logInfo(String.valueOf(location.getLatitude()), className);
                                    utils.logInfo(String.valueOf(location.getLongitude()), className);
                                } else {
                                    getLocationProgressBar.setVisibility(View.INVISIBLE);
                                    utils.logError("Location is null", className);
                                    showAlertLocationError("Could not get your location");
                                }
                            }
                        });
            } else {
                utils.logError("Permission not granted. Requesting now...", className);
                requestPermission();
            }
        }
    }


    private void showAlertLocationError(String errorMsg) {
        AlertDialog alertDialog = new AlertDialog.Builder(GetLocation.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error")
                .setMessage(errorMsg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        utils.moveToActivity(GetLocation.this, Dashboard.class);
                    }
                })
                .show();
    }
}

package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

        if (!User.isUserLoggedIn()){
            utils.moveToActivity(this, MainActivity.class);
        }

        getLocationProgressBar = (ProgressBar) findViewById(R.id.getLocationProgressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getUserLocation();
    }

    private Boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(GetLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPermission(){
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
                    utils.alertError("Could not get your location", GetLocation.this);
                    utils.logError("Requested permission not granted", className);
                    utils.moveToActivity(GetLocation.this, Dashboard.class);
                }
                return;
            }
        }
    }

    private void getUserLocation(){
        if (checkPermission()){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                try{
                                    User thisUser = new User();
                                    thisUser.setLocation(location);
                                    thisUser.saveGeoLocation();

                                    // Show success page
                                    utils.moveToActivity(GetLocation.this, VerificationSuccess.class);
                                } catch (Exception e){
                                    utils.alertError("Could not save your location", GetLocation.this);
                                    utils.logError(e.getMessage(), className);
                                    utils.moveToActivity(GetLocation.this, Dashboard.class);
                                }

                                utils.logInfo(String.valueOf(location.getLatitude()), className);
                                utils.logInfo(String.valueOf(location.getLongitude()), className);
                            } else {
                                getLocationProgressBar.setVisibility(View.INVISIBLE);
                                utils.alertError("Could not get your location", GetLocation.this);
                                utils.logError("Location is null", className);
                                utils.moveToActivity(GetLocation.this, Dashboard.class);
                            }
                        }
                    });
        } else {
            utils.logError("Permission not granted. Requesting now...", className);
            requestPermission();
        }
    }
}

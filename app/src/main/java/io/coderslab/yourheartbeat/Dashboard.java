package io.coderslab.yourheartbeat;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import Utilities.User;
import Utilities.utils;

public class Dashboard extends AppCompatActivity implements User.FetchUserData{

    private String className = "Dashboard.java";

    private TextView phoneNumberTextView;
    private TextView bloodGroupTextView;
    private TextView userLocationTextView;

    private User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        // Reference components
        phoneNumberTextView = (TextView) findViewById(R.id.dashboardPhoneNumberTextView);
        bloodGroupTextView = (TextView) findViewById(R.id.dashboardBloodGroupTextView);
        userLocationTextView = (TextView) findViewById(R.id.dashboardUserLocationTextView);


        if (User.isUserLoggedIn()) {
            currentUser.setFetchUserDataListener(this);
            // Fetch user data from remote
            try {
                currentUser.fetchFromRemote();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            utils.alertError("You're not logged in", this);
            utils.moveToActivity(getApplicationContext(), MainActivity.class);
            finish();
        }
    }

    @Override
    public void remoteUserFetchSuccess() {
        updateBloodGroupView(currentUser.getBloodGroup());
        updatePhoneNumberView(currentUser.getPhoneNumber());
        updateUserLocationView(currentUser.getUserLocality(Dashboard.this) + ", " + currentUser.getUserCountryName(Dashboard.this));
    }

    private void updatePhoneNumberView(String number){
        phoneNumberTextView.setText(number);
    }

    private void updateBloodGroupView(String bloodgrp){
        bloodGroupTextView.setText(bloodgrp);
    }

    private void updateUserLocationView(String userLocationString){
        userLocationTextView.setText(userLocationString);
    }
}

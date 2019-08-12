package io.coderslab.yourheartbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import Utilities.User;
import Utilities.utils;

public class Dashboard extends AppCompatActivity implements User.FetchUserData{

    private String className = "Dashboard.java";

    private TextView phoneNumberTextView;
    private TextView bloodGroupTextView;
    private TextView userLocationTextView;

    private User currentUser = new User();

    private ConstraintLayout constraintLayoutContainer;

    private static final int EDIT_USER_ACTIVITY_REQUEST_CODE = 23523;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        // Reference components
        phoneNumberTextView = (TextView) findViewById(R.id.editUserPhn);
        bloodGroupTextView = (TextView) findViewById(R.id.editUserBloodGrp);
        userLocationTextView = (TextView) findViewById(R.id.editUserLocation);
        constraintLayoutContainer = (ConstraintLayout) findViewById(R.id.constraintContainer);

        constraintLayoutContainer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditUserProfile.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Dashboard.this, constraintLayoutContainer, ViewCompat.getTransitionName(constraintLayoutContainer));
                startActivityForResult(intent, EDIT_USER_ACTIVITY_REQUEST_CODE, options.toBundle());
            }
        });

        if (User.isUserLoggedIn()) {
            currentUser.setFetchUserDataListener(this);
            // Fetch user data from remote
            try {
                currentUser.fetchFromRemote();
            } catch (Exception e) {
                utils.logError(e.getMessage(), className);
                e.printStackTrace();
            }
        } else {
            utils.alertError("You're not logged in", this);
            utils.moveToActivity(getApplicationContext(), MainActivity.class);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        if (requestCode == EDIT_USER_ACTIVITY_REQUEST_CODE){
            utils.logInfo("Got it", className);
            try {
                currentUser.fetchFromRemote();
            } catch (Exception e) {
                utils.logError(e.getMessage(), className);
                e.printStackTrace();
            }
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

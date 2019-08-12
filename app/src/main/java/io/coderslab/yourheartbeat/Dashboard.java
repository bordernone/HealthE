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
                startActivity(intent, options.toBundle());
            }
        });

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

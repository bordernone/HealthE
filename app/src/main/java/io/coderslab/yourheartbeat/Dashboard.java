package io.coderslab.yourheartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Utilities.User;
import Utilities.utils;

public class Dashboard extends AppCompatActivity implements User.FetchUserData{

    private String className = "Dashboard.java";

    private TextView phoneNumberTextView;
    private TextView bloodGroupTextView;

    private User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        // Reference components
        phoneNumberTextView = (TextView) findViewById(R.id.dashboardPhoneNumberTextView);
        bloodGroupTextView = (TextView) findViewById(R.id.dashboardBloodGroupTextView);



        if (User.isUserLoggedIn()) {
            utils.alertInfo("Logged in", this);

            currentUser.setFetchUserDataListener(this);
            // Fetch user data from remote
            try {
                currentUser.fetchFromRemote();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            utils.alertError("Not logged in", this);
        }
    }

    @Override
    public void remoteUserFetchSuccess() {
        updateBloodGroupView(currentUser.getBloodGroup());
        updatePhoneNumberView(currentUser.getPhoneNumber());
    }

    private void updatePhoneNumberView(String number){
        phoneNumberTextView.setText(number);
    }

    private void updateBloodGroupView(String bloodgrp){
        bloodGroupTextView.setText(bloodgrp);
    }
}

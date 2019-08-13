package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import CustomComponents.CustomLoadingButton;
import Utilities.User;
import Utilities.utils;

public class EditUserProfile extends AppCompatActivity implements User.FetchUserData, CustomLoadingButton.ButtonClickListener {
    private String className = "EditUserProfile.java";

    private User currentUser;

    private TextView phoneNumberTextView;
    private TextView bloodGroupTextView;
    private TextView userLocationTextView;
    private Spinner bloodGrpSpinner;
    private Button updateLocationBtn;
    private ConstraintLayout closeEditUserProfileConstraintLayout;

    private CustomLoadingButton updateBloodGrpBtn;

    private Integer bloodGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        if (!User.isUserLoggedIn()) {
            utils.moveToActivity(getApplicationContext(), MainActivity.class);
            finish();
        }
        // referencing components
        phoneNumberTextView = (TextView) findViewById(R.id.editUserPhn);
        bloodGroupTextView = (TextView) findViewById(R.id.editUserBloodGrp);
        userLocationTextView = (TextView) findViewById(R.id.editUserLocation);
        bloodGrpSpinner = (Spinner) findViewById(R.id.spinner_blood_grp);
        updateLocationBtn = (Button) findViewById(R.id.editUserUpdateLocationBtn);
        updateBloodGrpBtn = (CustomLoadingButton) findViewById(R.id.customLoadingButtonUpdateBloodGrp);
        closeEditUserProfileConstraintLayout = (ConstraintLayout) findViewById(R.id.closeEditUserProfileConstraintLayout);


        updateBloodGrpBtn.setButtonClickListener(this);

        currentUser = new User();
        currentUser.setFetchUserDataListener(this);

        try {
            currentUser.fetchFromRemote();
        } catch (Exception e) {
            utils.logError(e.getMessage(), className);
        }


        // populating Blood group spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_reg, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrpSpinner.setAdapter(adapter);


        // Update location
        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.moveToActivity(EditUserProfile.this, GetLocation.class);
            }
        });


        // Close edit activity
        closeEditUserProfileConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.simulateBackBtnClick(EditUserProfile.this);
            }
        });

    }

    @Override
    public void remoteUserFetchSuccess() {
        updateBloodGroupView(currentUser.getBloodGroup());
        updatePhoneNumberView(currentUser.getPhoneNumber());
        updateUserLocationView(currentUser.getUserLocality(EditUserProfile.this) + ", " + currentUser.getUserCountryName(EditUserProfile.this));
    }


    private void updatePhoneNumberView(String number) {
        phoneNumberTextView.setText(number);
    }

    private void updateBloodGroupView(String bloodgrp) {
        bloodGroupTextView.setText(bloodgrp);
    }

    private void updateUserLocationView(String userLocationString) {
        userLocationTextView.setText(userLocationString);
    }

    @Override
    public void onButtonClickListener() {
        bloodGroupId = Integer.valueOf(String.valueOf(bloodGrpSpinner.getSelectedItemId()));

        if (!User.isValidBloodGrpId(bloodGroupId, EditUserProfile.this)) {
            utils.alertError("Please select a blood group", EditUserProfile.this);
        } else {
            try {
                currentUser.setBloodGroupId(bloodGroupId, EditUserProfile.this);
                currentUser.saveBloodGroup(EditUserProfile.this);
                utils.alertInfo("Updated!", EditUserProfile.this);
                updateBloodGroupView(currentUser.getBloodGroup());
            } catch (Exception e) {
                utils.logError(e.getMessage(), className);
            }
        }
    }
}

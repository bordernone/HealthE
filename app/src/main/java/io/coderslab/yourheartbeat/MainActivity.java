package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import Utilities.User;
import Utilities.utils;

public class MainActivity extends AppCompatActivity {
    // variables to hold input values
    private String phoneNumber;
    private String bloodGroup;
    private Integer bloodGroupId;

    // input fields
    private Spinner bloodGrpSpinner;
    private EditText phoneInputField;

    // Layouts
    private ConstraintLayout registerWrapper;

    // ProgressBars
    private ProgressBar registerBtnProgressBar;
    private Boolean isRegisterBtnProgressBarVisible = false;

    // Buttons
    private Button requestDonationBtn;
    private Button registerBtn;

    // helpers and data
    private Boolean isLoading = false;
    private String[] bloodGrpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if user is already logged in, take them to Dashboard
        if (User.isUserLoggedIn()){
            utils.moveToActivity(MainActivity.this, Dashboard.class);
        }

        // Reference components
        phoneInputField = (EditText) findViewById(R.id.phoneInputField);
        bloodGrpSpinner = (Spinner) findViewById(R.id.spinner_blood_grp);
        registerBtnProgressBar = (ProgressBar) findViewById(R.id.registerBtnProgressBar);

        // Populate blood group list
        bloodGrpList = getResources().getStringArray(R.array.blood_grp_array_reg);

        // populating Blood group spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_reg, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrpSpinner.setAdapter(adapter);

        // Animation for the container
        registerWrapper = (ConstraintLayout) findViewById(R.id.registerformbg);
        registerWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()){
                    setElevation(v, 0);
                }
                if (MotionEvent.ACTION_UP == event.getAction()){
                    setElevation(v, 12);
                }
                return true;
            }
        });

        // Request donation button
        requestDonationBtn = (Button) findViewById(R.id.registerReqDonationBtn);
        requestDonationBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                utils.moveToActivity(MainActivity.this, RequestDonation.class);
            }
        });

        // Participate btn
        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handleRegisterBtnClick();
            }
        });
    }

    public void setElevation(View v, int units){
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 21){
            v.setElevation(units);
        }
    }

    private void handleRegisterBtnClick(){
        phoneNumber = phoneInputField.getText().toString().replaceAll("[^0-9]", "");
        bloodGroupId = Integer.valueOf(String.valueOf(bloodGrpSpinner.getSelectedItemId()));

        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        if (User.isValidBloodGrpId(bloodGroupId, MainActivity.this) == false){
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please select a blood group.");
            alertDialog.setPositiveButton("Okay",null);
            alertDialog.show();
        } else {
            if (utils.isNumberValid(phoneNumber) == true){
                // Everything is OK; Move to registration

                Intent verifyMobile = new Intent(MainActivity.this, VerifyMobile.class);
                verifyMobile.putExtra("phoneNumber", phoneNumber);
                verifyMobile.putExtra("bloodGroupId", String.valueOf(bloodGroupId));
                startActivity(verifyMobile);
            } else {
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Please enter a valid number");
                alertDialog.setPositiveButton("Okay",null);
                alertDialog.show();
            }
        }
    }

    private void toggleRegisterBtnLoad(){
        if (registerBtnProgressBar.getVisibility() == View.INVISIBLE){
            registerBtn.setTextScaleX(0); // make button text invisible
            registerBtnProgressBar.setVisibility(View.VISIBLE);
        } else {
            registerBtnProgressBar.setVisibility(View.INVISIBLE);
            registerBtn.setTextScaleX(1); // make button text visible
        }
    }
}
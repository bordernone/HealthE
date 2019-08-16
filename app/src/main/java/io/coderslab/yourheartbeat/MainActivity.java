package io.coderslab.yourheartbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import Utilities.User;
import Utilities.utils;

public class MainActivity extends AppCompatActivity {
    private String phoneNumber;
    private Integer bloodGroupId;


    private Spinner bloodGrpSpinner;
    private EditText phoneInputField;
    private Button requestDonationBtn;
    private Button registerBtn;


    private String[] bloodGrpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if user is already logged in, take them to Dashboard
        if (User.isUserLoggedIn()){
            utils.moveToActivity(MainActivity.this, Dashboard.class);
            finish();
        }

        // Reference components
        phoneInputField = (EditText) findViewById(R.id.phoneInputFieldReqDonation);
        bloodGrpSpinner = (Spinner) findViewById(R.id.spinner_blood_grp_req_donation);

        // Populate blood group list
        bloodGrpList = getResources().getStringArray(R.array.blood_grp_array_reg);

        // populating Blood group spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_reg, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrpSpinner.setAdapter(adapter);

        // Request donation button
        requestDonationBtn = (Button) findViewById(R.id.registerReqDonationBtn);
        requestDonationBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                utils.moveToActivity(MainActivity.this, RequestDonation.class);
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            }
        });

        // Participate btn / register
        registerBtn = (Button) findViewById(R.id.requestDonationBtn);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handleRegisterBtnClick();
            }
        });
    }


    private void handleRegisterBtnClick(){
        phoneNumber = phoneInputField.getText().toString().replaceAll("[^0-9+]", "");
        bloodGroupId = Integer.valueOf(String.valueOf(bloodGrpSpinner.getSelectedItemId()));

        if (!User.isValidBloodGrpId(bloodGroupId, MainActivity.this)){
            utils.alertError("Please select a blood group", MainActivity.this);
        } else {
            if (User.isNumberValid(phoneNumber)){
                // Everything is OK; Move to registration

                Intent verifyMobile = new Intent(MainActivity.this, VerifyMobile.class);
                verifyMobile.putExtra("phoneNumber", phoneNumber);
                verifyMobile.putExtra("bloodGroupId", String.valueOf(bloodGroupId));
                startActivity(verifyMobile);
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            } else {
                utils.alertError("Please enter a valid number", MainActivity.this);
            }
        }
    }
}
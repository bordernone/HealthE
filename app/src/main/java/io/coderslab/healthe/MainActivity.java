package io.coderslab.healthe;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import Utilities.User;
import Utilities.utils;

public class MainActivity extends AppCompatActivity {
    private String phoneNumber;
    private Integer bloodGroupId;


    private Spinner bloodGrpSpinner;
    private EditText phoneInputField;
    private Button requestDonationBtn;
    private Button registerBtn;
    private CountryCodePicker countryCodePicker;


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
        phoneInputField = findViewById(R.id.editText_carrierNumber);
        bloodGrpSpinner = findViewById(R.id.spinner_blood_grp_req_donation);
        countryCodePicker = findViewById(R.id.country_code_picker_req_donation_activity);

        // Link country code picker with phone number text view
        countryCodePicker.registerCarrierNumberEditText(phoneInputField);

        // Populate blood group list
        bloodGrpList = getResources().getStringArray(R.array.blood_grp_array_reg);

        // populating Blood group spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_reg, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrpSpinner.setAdapter(adapter);

        // Request donation button
        requestDonationBtn = findViewById(R.id.registerReqDonationBtn);
        requestDonationBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                utils.moveToActivity(MainActivity.this, RequestDonation.class);
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            }
        });

        // Participate btn / register
        registerBtn = findViewById(R.id.requestDonationBtnRequestDonationActivity);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handleRegisterBtnClick();
            }
        });

        // format phone number input
        phoneInputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }


    private void handleRegisterBtnClick(){
        phoneNumber = countryCodePicker.getFullNumberWithPlus();
        bloodGroupId = Integer.valueOf(String.valueOf(bloodGrpSpinner.getSelectedItemId()));

        if (!User.isValidBloodGrpId(bloodGroupId, MainActivity.this)){
            utils.alertError("Please select a blood group", MainActivity.this);
        } else {
            if (User.isNumberValid(countryCodePicker)){
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
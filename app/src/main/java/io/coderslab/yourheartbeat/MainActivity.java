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

        // DELETE THIS WHEN DONE TESTING
        Intent myIntent = new Intent(this, VerifyMobile.class);
        startActivity(myIntent);

        // Reference input fields
        phoneInputField = (EditText) findViewById(R.id.phoneInputField);
        bloodGrpSpinner = (Spinner) findViewById(R.id.spinner_blood_grp);

        // Reference Progressbars
        registerBtnProgressBar = (ProgressBar) findViewById(R.id.registerBtnProgressBar);

        // Populate blood group list
        bloodGrpList = getResources().getStringArray(R.array.blood_grp_array_reg);

        // populating Blood group spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_reg, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrpSpinner.setAdapter(adapter);

        // Subtle Animation for the container
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
                Intent moveToRequest = new Intent(MainActivity.this, RequestDonation.class);
                startActivity(moveToRequest);
            }
        });

        // Participate btn
        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signUp();
            }
        });
    }

    public void setElevation(View v, int units){
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 21){
            v.setElevation(units);
        }
    }

    private void signUp(){
        if (!isLoading){
            toggleRegisterBtnLoad(); // set button loading visible

            // process sign up
            phoneNumber = phoneInputField.getText().toString();
            bloodGroupId = Integer.valueOf(String.valueOf(bloodGrpSpinner.getSelectedItemId()));

            if (bloodGroupId == 0){
                // alert to select a blood group
            } else {

            }
            Log.i("MYAPP", bloodGrpList[1]);
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
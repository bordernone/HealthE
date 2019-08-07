package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import Utilities.User;
import Utilities.utils;

public class VerificationSuccess extends AppCompatActivity {
    // Buttons
    private Button verificationSuccessBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_success);

        // if user is already logged in, take them to Dashboard
        if (User.isUserLoggedIn()){
            utils.moveToActivity(VerificationSuccess.this, Dashboard.class);
        }

        // reference components
        verificationSuccessBtn = (Button) findViewById(R.id.verificationSuccessContinueBtn);

        // on continue button press
        verificationSuccessBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                utils.moveToActivity(VerificationSuccess.this, Dashboard.class);
            }
        });

    }
}

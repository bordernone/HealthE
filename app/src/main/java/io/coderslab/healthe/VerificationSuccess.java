package io.coderslab.healthe;

import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import Utilities.utils;

public class VerificationSuccess extends AppCompatActivity {
    // Buttons
    private Button verificationSuccessBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_success);

        ConstraintLayout verificationSuccessBox = (ConstraintLayout) findViewById(R.id.verificationSuccessBox);
        verificationSuccessBox.playSoundEffect(SoundEffectConstants.CLICK);

        // reference components
        verificationSuccessBtn = (Button) findViewById(R.id.requestDonationSuccessContinueBtn);

        // on continue button press
        verificationSuccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.moveToActivity(VerificationSuccess.this, Dashboard.class);
                finish();
                VerificationSuccess.this.overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            }
        });
    }

    @Override
    public void onBackPressed(){

    }
}

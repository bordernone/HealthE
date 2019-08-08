package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import Utilities.User;
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
        verificationSuccessBtn = (Button) findViewById(R.id.verificationSuccessContinueBtn);

        // on continue button press
        verificationSuccessBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                utils.moveToActivity(VerificationSuccess.this, Dashboard.class);
                finish();
            }
        });

    }
}

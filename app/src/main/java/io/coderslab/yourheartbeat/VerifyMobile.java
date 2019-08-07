package io.coderslab.yourheartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import CustomComponents.CustomLoadingButton;
import Utilities.User;
import Utilities.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyMobile extends AppCompatActivity implements CustomLoadingButton.ButtonClickListener {
    private final String className = "VerifyMobile.class";

    // variables
    private String phoneNumber;
    private Integer bloodGroupId;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private String verificationCode;

    // Input fields
    private EditText verificationCodeInputField;

    // Buttons
    private CustomLoadingButton verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        // if user is already logged in, take them to Dashboard
        if (User.isUserLoggedIn()){
            utils.moveToActivity(VerifyMobile.this, Dashboard.class);
        }

        // Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // referencing components
        verificationCodeInputField = (EditText) findViewById(R.id.verificationCodeEditText);
        verifyButton = (CustomLoadingButton) findViewById(R.id.verifyBtn);

        if (setValuesPhoneNumberBloodGroup(savedInstanceState) == true) {
            sendVerificationCode(phoneNumber);
        } else {
            utils.alertError("Something went wrong. Please try again.", VerifyMobile.this);
            utils.moveToActivity(VerifyMobile.this, MainActivity.class);
            utils.logError("4. Can't retrieve phone number or blood group", className);
        }

        // handle button click
        verifyButton.setButtonClickListener(this);
    }

    private void handleVerifyBtnClick() {
        utils.logError("Unable", className);
        verifyButton.setLoadingState(true);
        try {
            verificationCode = verificationCodeInputField.getText().toString();
            verifyVerificationCode(verificationCode);
        } catch (Exception e) {
            verifyButton.setLoadingState(false);
            utils.alertError("Something went wrong. Please try again.", VerifyMobile.this);
            utils.logError("6. " + e.getMessage(), className);
        }
    }

    private boolean setValuesPhoneNumberBloodGroup(Bundle savedInstanceState) {
        Boolean valuesSet = true;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                valuesSet = false;
            } else {
                // get the values
                phoneNumber = extras.getString("phoneNumber");
                Boolean temp = false;
                try {
                    bloodGroupId = Integer.valueOf(extras.getString("bloodGroupId"));
                    temp = true;
                } catch (Exception e) {
                    // produce error or return to main activity
                    valuesSet = false;
                    utils.logInfo("1. Can't get integer value of blood group id: " + extras.getString("bloodGroupId"), className);
                }
                if (temp) {
                    if (utils.isNumberValid(phoneNumber) != true || User.isValidBloodGrpId(bloodGroupId, VerifyMobile.this) == false) {
                        // produce error or return to main activity
                        valuesSet = false;
                        utils.logInfo("2. Can't get integer value of blood group id: " + extras.getString("bloodGroupId"), className);
                    }
                }
            }
        } else {
            // get the values
            phoneNumber = (String) savedInstanceState.getSerializable("phoneNumber");
            String temp = "";
            try {
                temp = (String) savedInstanceState.getSerializable("bloodGroupId");
                bloodGroupId = Integer.valueOf((String) savedInstanceState.getSerializable("bloodGroupId"));
            } catch (Exception e) {
                // produce error or return to main activity
                valuesSet = false;
                utils.logInfo("3. Can't get integer value of blood group id: " + temp, className);
            }
        }
        return valuesSet;
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+977" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                verificationCodeInputField.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyMobile.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyMobile.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent verifySuccess = new Intent(VerifyMobile.this, VerificationSuccess.class);
                            verifySuccess.putExtra("bloodGroupId", String.valueOf(bloodGroupId));
                            verifySuccess.putExtra("phoneNumber", phoneNumber);
                            startActivity(verifySuccess);
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon.";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid Code";
                            }
                            utils.alertError(message, VerifyMobile.this);
                            verifyButton.setLoadingState(false);
                        }
                    }
                });
    }

    @Override
    public void onButtonClickListener() {
        handleVerifyBtnClick();
    }
}

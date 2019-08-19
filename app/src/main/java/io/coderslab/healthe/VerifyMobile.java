package io.coderslab.healthe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import CustomComponents.CustomLoadingButton;
import Utilities.User;
import Utilities.utils;

public class VerifyMobile extends AppCompatActivity implements CustomLoadingButton.ButtonClickListener{
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
        if (User.isUserLoggedIn()) {
            utils.moveToActivity(VerifyMobile.this, Dashboard.class);
            finish();
        }

        // Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // referencing components
        verificationCodeInputField = (EditText) findViewById(R.id.verificationCodeEditText);
        verifyButton = (CustomLoadingButton) findViewById(R.id.verifyBtn);

        if (setValuesPhoneNumberBloodGroup(savedInstanceState)) {
            sendVerificationCode(phoneNumber);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(VerifyMobile.this)
                    .setTitle("Error")
                    .setMessage("Something went wrong. Please try again")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            utils.logError("4. Can't retrieve phone number or blood group", className);
                            utils.moveToActivity(VerifyMobile.this, MainActivity.class);
                        }
                    })
                    .show();
        }

        // handle button click
        verifyButton.setButtonClickListener(this);
    }

    private void handleVerifyBtnClick() {
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
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return false;
            } else {
                // get the values
                phoneNumber = extras.getString("phoneNumber");
                try {
                    bloodGroupId = Integer.valueOf(extras.getString("bloodGroupId"));
                } catch (Exception e) {
                    utils.logInfo("1. Can't get integer value of blood group id: " + extras.getString("bloodGroupId"), className);
                    return false;
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
                utils.logInfo("3. Can't get integer value of blood group id: " + temp, className);
                return false;
            }
        }
        if (!User.isValidBloodGrpId(bloodGroupId, VerifyMobile.this)) {
            utils.logError("Validation failed. Check log below", className);
            utils.logInfo("2. Something went wrong. phoneNumber: " + phoneNumber + " bloodGroupId: " + bloodGroupId, className);
            return false;
        } else {
            return true;
        }
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
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
            } else {
                utils.logError("Verification code is null. Need to enter code manually", className);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            utils.logError("Verification failed. Check log below", className);
            utils.logError(e.getMessage(), className);
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
                            saveToDatabase();
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

    public void saveToDatabase(){
        utils.logInfo("Successful user verification. Saving to firestore database...", className);

        // Save to database
        try {
            User user = new User(phoneNumber, bloodGroupId, VerifyMobile.this);
            user.saveBloodGroup(VerifyMobile.this);

            // Get user's location
            Intent getLocation = new Intent(VerifyMobile.this, GetLocation.class);
            startActivity(getLocation);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        } catch (Exception e) {
            utils.alertError("Something went wrong", VerifyMobile.this);
            utils.logError(e.getMessage(), className);
        }
    }
}

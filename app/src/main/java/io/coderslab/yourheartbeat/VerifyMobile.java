package io.coderslab.yourheartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class VerifyMobile extends AppCompatActivity {
    // variables
    private String phoneNumber;
    private String bloodGroup;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private String verificationCode;

    // Input fields
    private EditText verificationCodeInputField;

    // Buttons
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        // Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // referencing components
        verificationCodeInputField = (EditText) findViewById(R.id.verificationCodeEditText);
        verifyButton = (Button) findViewById(R.id.verifyBtn);

        if (setValuesPhoneNumberBloodGroup(savedInstanceState) == true) {
            sendVerificationCode(phoneNumber);
        } else {
            Log.e("YHB", "Can't retrieve phone number or blood group");
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleVerifyBtnClick();
            }
        });
    }

    private void handleVerifyBtnClick(){
        verificationCode = verificationCodeInputField.getText().toString();
        verifyVerificationCode(verificationCode);
    }

    private boolean setValuesPhoneNumberBloodGroup(Bundle savedInstanceState) {
        Boolean valuesSet = true;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                // produce error or return to main activity
                valuesSet = false;
                alertError("Something went wrong. Please try again.");
                moveToMainActivity();
            } else {
                // get the values
                phoneNumber = extras.getString("phoneNumber");
                bloodGroup = extras.getString("bloodGroupId");
            }
        } else {
            // get the values
            phoneNumber = (String) savedInstanceState.getSerializable("phoneNumber");
            bloodGroup = (String) savedInstanceState.getSerializable("bloodGroupId");
        }
        return valuesSet;
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(VerifyMobile.this, MainActivity.class);
        startActivity(intent);
    }

    private void alertError(String errorMsg) {
        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VerifyMobile.this);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(errorMsg);
        alertDialog.setPositiveButton("Okay", null);
        alertDialog.show();
    }

    private void alertInfo(String message) {
        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VerifyMobile.this);
        alertDialog.setTitle("Info");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Okay", null);
        alertDialog.show();
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
                            alertInfo("Success");
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon.";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid Code";
                            }
                            alertError(message);
                        }
                    }
                });
    }
}

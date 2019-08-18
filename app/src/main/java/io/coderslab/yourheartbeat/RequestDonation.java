package io.coderslab.yourheartbeat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctionsException;

import CustomComponents.CustomLoadingButton;
import Utilities.User;
import Utilities.utils;

public class RequestDonation extends AppCompatActivity implements CustomLoadingButton.ButtonClickListener, User.MakeDonationRequest {
    private static final String className = "RequestDonation.class";

    private ConstraintLayout registerWrapper;
    private EditText phoneInputField;
    private Spinner bloodGrpSpinner;
    private EditText additionalInfoInputField;
    private CustomLoadingButton requestDonationBtn;

    private String phoneNumber;
    private String additionalInfo;
    private int bloodGrpId;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_donation);

        if (!User.isUserLoggedIn()){
            userNotLoggedIn();
        }

        currentUser = new User();

        // reference components
        bloodGrpSpinner = findViewById(R.id.spinner_blood_grp_req_donation);
        registerWrapper = findViewById(R.id.registerformbg);
        phoneInputField = findViewById(R.id.phoneInputFieldReqDonation);
        additionalInfoInputField = findViewById(R.id.editTextReqDonationAdditionalInfo);
        requestDonationBtn = findViewById(R.id.requestDonationBtnRequestDonationActivity);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_req, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bloodGrpSpinner.setAdapter(adapter);

        requestDonationBtn.setButtonClickListener(this);

        // format phone number input
        phoneInputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void getInputFieldValues(){
        phoneNumber = phoneInputField.getText().toString();
        additionalInfo = additionalInfoInputField.getText().toString();
        bloodGrpId = Integer.valueOf(String.valueOf(bloodGrpSpinner.getSelectedItemId()));
    }

    private void userNotLoggedIn(){
        AlertDialog alertDialog = new AlertDialog.Builder(RequestDonation.this)
                .setTitle("Login Required")
                .setMessage("You're not logged in")
                .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        utils.moveToActivity(RequestDonation.this, MainActivity.class);
                        finish();
                        overridePendingTransition(R.anim.slide_reverse_enter, R.anim.slide_reverse_exit);
                    }
                })
                .show();
    }

    @Override
    public void onButtonClickListener() {
        requestDonationBtn.setLoadingState(true);
        if (User.isUserLoggedIn()){
            getInputFieldValues();
            if (!User.isNumberValid(phoneNumber)){
                utils.logError("Wrong phone number", className);
                utils.alertError(getResources().getString(R.string.invalid_phone_number), RequestDonation.this);
                requestDonationBtn.setLoadingState(false);
            } else {
                if (!User.isValidBloodGrpId(bloodGrpId, RequestDonation.this)){
                    utils.logError("Please select a valid blood group", className);
                    utils.alertError(getResources().getString(R.string.select_a_blood_group), RequestDonation.this);
                    requestDonationBtn.setLoadingState(false);
                } else {
                    User currentUser = new User();
                    currentUser.setMakeDonationRequestListener(RequestDonation.this);
                    try {
                        currentUser.setBloodGroupId(bloodGrpId, RequestDonation.this);
                        currentUser.makeDonationRequestSave(phoneNumber, currentUser.getBloodGroup(), additionalInfo);
                    } catch (Exception e){
                        utils.logError("Something went wrong.", className);
                        utils.logError(e.getMessage(), className);
                        utils.alertError("Something went wrong", RequestDonation.this);
                        requestDonationBtn.setLoadingState(false);
                    }
                }
            }
        }
    }

    @Override
    public void donationRequestSuccess() {
        utils.sendMail()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                utils.logError(details.toString(), className);
                            }
                            utils.logError(e.getMessage(), className);
                        }
                        utils.moveToActivity(RequestDonation.this, VerificationSuccess.class);
                        finish();
                        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                    }
                });
    }
}
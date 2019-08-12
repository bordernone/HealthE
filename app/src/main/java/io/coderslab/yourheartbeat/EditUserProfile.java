package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import Utilities.User;
import Utilities.utils;

public class EditUserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        if (!User.isUserLoggedIn()){
            utils.moveToActivity(getApplicationContext(), MainActivity.class);
            finish();
        }


    }
}

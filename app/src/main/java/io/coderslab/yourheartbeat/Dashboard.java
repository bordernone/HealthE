package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import Utilities.User;
import Utilities.utils;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (User.isUserLoggedIn()){
            utils.alertInfo("Logged in", this);
        } else {
            utils.alertError("Not logged in", this);
        }
    }
}

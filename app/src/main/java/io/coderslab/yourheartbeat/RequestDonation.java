package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RequestDonation extends AppCompatActivity {

    ConstraintLayout registerWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_donation);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_blood_grp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_req, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        registerWrapper = (ConstraintLayout) findViewById(R.id.registerformbg);

        setOnRegisterWrapperTouchEvent();
    }

    protected void onStop(){
        super.onStop();
        registerWrapper.setOnTouchListener(null);
    }

    protected void onDestroy(){
        super.onDestroy();
        registerWrapper.setOnTouchListener(null);
    }

    public void setElevation(View v, int units){
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 21){
            v.setElevation(units);
        }
    }

    private void setOnRegisterWrapperTouchEvent(){
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
    }
}

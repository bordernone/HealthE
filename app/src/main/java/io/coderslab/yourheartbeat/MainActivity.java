package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Spinner spinner = (Spinner) findViewById(R.id.spinner_blood_grp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_reg, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        ConstraintLayout registerWrapper = (ConstraintLayout) findViewById(R.id.registerformbg);
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

        // Request donation button
        Button button = (Button) findViewById(R.id.registerReqDonationBtn);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent moveToRequest = new Intent(MainActivity.this, RequestDonation.class);
                startActivity(moveToRequest);
            }
        });

        // Participate btn
        Button participateBtn = (Button) findViewById(R.id.register_btn);
        participateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(myIntent);
            }
        });
    }

    public void setElevation(View v, int units){
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 21){
            v.setElevation(units);
        }
    }
}
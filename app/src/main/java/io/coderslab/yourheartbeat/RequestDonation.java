package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RequestDonation extends AppCompatActivity {

    ConstraintLayout registerWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_donation);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_blood_grp_req_donation);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_grp_array_req, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        registerWrapper = (ConstraintLayout) findViewById(R.id.registerformbg);
    }

}

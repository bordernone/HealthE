package io.coderslab.yourheartbeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import Utilities.utils;

public class DisplayUserActivity extends AppCompatActivity {
    private static final String className = "DisplayUserActivity.java";

    private ConstraintLayout closeBtnContainer;
    private TextView titleTextView;
    private TextView descriptionTextView;

    private String title;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);
        referenceComponents();


        closeBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_reverse_enter, R.anim.slide_reverse_exit);
            }
        });

        setContent();
    }

    private void referenceComponents(){
        closeBtnContainer = findViewById(R.id.displayUserActivityCloseBtnContainer);
        titleTextView = findViewById(R.id.displayUserActivityTitleTextView);
        descriptionTextView = findViewById(R.id.displayUserActivityDescTextView);
    }

    private void setContent(){
        Intent intent = getIntent();
        try {
            title = intent.getStringExtra("title");
            description = intent.getStringExtra("description");

            if (title.length() > 0 && description.length() > 0){
                updateComponents();
            } else {
                utils.logError("Unknown error", className);
                utils.logError("title: " + title, className);
                utils.logError("description: " + description, className);
                utils.simulateBackBtnClick(DisplayUserActivity.this);
            }
        } catch (Exception e){
            utils.logError(e.getMessage(), className);
            utils.simulateBackBtnClick(DisplayUserActivity.this);
        }

    }

    private void updateComponents(){
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_reverse_enter, R.anim.slide_reverse_exit);
    }
}

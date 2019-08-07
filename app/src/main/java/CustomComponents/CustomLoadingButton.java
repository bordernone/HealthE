package CustomComponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.StyleableRes;

import io.coderslab.yourheartbeat.R;

public class CustomLoadingButton extends LinearLayout {

    @StyleableRes
    int index0 = 0;
    @StyleableRes
    int index1 = 1;

    Button button;
    ProgressBar progressBar;

    private Boolean loading;
    private CharSequence title;

    public CustomLoadingButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CustomLoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.custom_loading_button, this);
        initComponents();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLoadingButton);

        int count = typedArray.getIndexCount();
        try {
            for (int i = 0; i < count; ++i) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.CustomLoadingButton_title) {
                    title = typedArray.getText(attr);
                    setButtonTitle(title);
                } else if (attr == R.styleable.CustomLoadingButton_isLoading) {
                    loading = typedArray.getBoolean(attr, false);
                    setLoadingState(loading);
                }
            }
        } finally {
            // for reuse
            typedArray.recycle();
        }
    }

    private void initComponents() {
        button = (Button) findViewById(R.id.customLoadingBtn);

        progressBar = (ProgressBar) findViewById(R.id.customLoadingBtnProgressBar);
    }


    public void setButtonTitle(CharSequence value) {
        button.setText(value);
    }

    public void setLoadingState(Boolean loading) {
        if (loading) {
            button.setTextScaleX(0); // make button text invisible
            progressBar.setVisibility(View.VISIBLE);
            button.setClickable(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            button.setTextScaleX(1); // make button text visible
            button.setClickable(true);
        }
    }
}
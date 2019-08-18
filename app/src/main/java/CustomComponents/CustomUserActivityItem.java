package CustomComponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.coderslab.healthe.R;

public class CustomUserActivityItem extends LinearLayout {

    private String title;
    private String description;

    private TextView titleTextView;
    private TextView descriptionTextiew;

    public CustomUserActivityItem(Context context){
        super(context);
        inflate(context, R.layout.custom_user_activity_item, this);
        initComponents();
    }

    public CustomUserActivityItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.custom_user_activity_item, this);
        initComponents();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUserActivityItem);

        int count = typedArray.getIndexCount();
        try {
            for (int i = 0; i < count; ++i) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.CustomUserActivityItem_customUserActivityItemTitle) {
                    title = typedArray.getString(attr);
                    setTitle(title);
                } else if (attr == R.styleable.CustomUserActivityItem_customUserActivityItemDescription) {
                    description = typedArray.getString(attr);
                    setDescription(description);
                }
            }
        } finally {
            // for reuse
            typedArray.recycle();
        }
    }

    private void initComponents(){
        titleTextView = (TextView) findViewById(R.id.customUserActivityItemTitleTextView);
        descriptionTextiew = (TextView) findViewById(R.id.customUserActivityItemDescTextView);
    }

    public void setTitle(String thisTitle){
        titleTextView.setText(thisTitle);
    }

    public void setDescription(String thisDesc){
        descriptionTextiew.setText(thisDesc);
    }
}

package com.materight.streamcorn.ui.customs.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.materight.streamcorn.R;

/**
 * Specializzazione di ExpandableTextView con bottone "espandi"
 */

public class ExpandableDescription extends ConstraintLayout {

    private static final String TAG = "ExpandableDescription";

    private LinearLayout mContainer;
    private ExpandableTextView textView;
    private Button expandButton;

    public ExpandableDescription(Context context) {
        this(context, null);
    }

    public ExpandableDescription(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableDescription(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        CharSequence text = "";

        int[] set = {android.R.attr.text};

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, set, defStyleAttr, 0);
        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == android.R.attr.text) {
                text = a.getText(attr);
            }
        }
        a.recycle();

        initViews();
        textView.setText(text);
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.expandable_description, this, true);
        textView = findViewById(R.id.text);
        expandButton = findViewById(R.id.expand_button);

        textView.setToggleButton(expandButton);
        final OnClickListener toggleClick = v -> {
            if (expandButton.getVisibility() == VISIBLE) {
                expandButton.setText(textView.isExpanded() ? R.string.expand : R.string.collapse);
                textView.toggle();
            }
        };
        this.setOnClickListener(toggleClick);
        expandButton.setOnClickListener(toggleClick);
    }

    public void toggle() {
        textView.toggle();
    }

    public void expand() {
        textView.expand();
    }

    public void collapse() {
        textView.collapse();
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setMaxLines(int maxLines) {
        textView.setMaxLines(maxLines);
    }
}

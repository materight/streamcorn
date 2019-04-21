package com.est.streamcorn.ui.customs.widgets;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.est.streamcorn.R;

/**
 * Created by Matteo on 13/02/2018.
 */

public class ExpandableTextView extends at.blogc.android.views.ExpandableTextView {

    private View mToggleButton;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setInterpolator(new FastOutSlowInInterpolator());
        setAnimationDuration(context.getResources().getInteger(R.integer.description_expand_collapse_duration));
    }

    public void setToggleButton(View mToggleButton) {
        this.mToggleButton = mToggleButton;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mToggleButton != null) {
            if(getLineCount() > getMaxLines()){
                mToggleButton.setVisibility(VISIBLE);
            }
            else {
                mToggleButton.setVisibility(GONE);
            }
        }
    }
}

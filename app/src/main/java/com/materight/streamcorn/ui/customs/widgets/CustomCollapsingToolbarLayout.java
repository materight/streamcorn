package com.materight.streamcorn.ui.customs.widgets;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class CustomCollapsingToolbarLayout extends CollapsingToolbarLayout {

    private Boolean previousShowing = true;

    public interface Listener {
        void onContentScrimAnimationStarted(boolean showing);
    }

    private Listener mListener;

    public CustomCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, animate);
        if (animate && mListener != null && shown != previousShowing) {
            mListener.onContentScrimAnimationStarted(shown);
            previousShowing = shown;
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }
}
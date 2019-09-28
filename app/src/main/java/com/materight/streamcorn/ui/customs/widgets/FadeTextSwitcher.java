package com.materight.streamcorn.ui.customs.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class FadeTextSwitcher extends TextSwitcher {

    public FadeTextSwitcher(Context context) {
        super(context);

        setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        setFactory(new TextViewFactory(context));
    }

    public FadeTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        setFactory(new TextViewFactory(context, attrs));
    }

    public FadeTextSwitcher(Context context, AttributeSet attrs, final int defStyle) {
        this(context, attrs);
    }

    private static class TextViewFactory implements ViewFactory {
        private final Context context;
        private final AttributeSet attrs;

        TextViewFactory(Context context) {
            this.context = context;
            attrs = null;
        }

        TextViewFactory(Context context, AttributeSet attrs) {
            this.context = context;
            this.attrs = attrs;
        }

        @Override
        public View makeView() {
            if (attrs != null)
                return new TextView(context, attrs, attrs.getStyleAttribute());
            else
                return new TextView(context);
        }
    }
}

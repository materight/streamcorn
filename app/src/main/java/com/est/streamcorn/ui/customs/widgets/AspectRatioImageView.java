package com.est.streamcorn.ui.customs.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.est.streamcorn.R;

public class AspectRatioImageView extends androidx.appcompat.widget.AppCompatImageView {

    private float aspectRatio;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView, 0, 0);
        try {
            aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspectRatio, R.dimen.movie_poster_aspect_ratio);
        } finally {
            a.recycle();
        }
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * aspectRatio);
        setMeasuredDimension(width, height);
    }
}

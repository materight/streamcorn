package com.est.streamcorn.adapters.base;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClick(View view, T item);
}

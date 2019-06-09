package com.est.streamcorn.adapters.base;

import android.view.View;

public interface OnItemTitledClickListener<T> {
    void onItemClick(View view, String title, T item);
}

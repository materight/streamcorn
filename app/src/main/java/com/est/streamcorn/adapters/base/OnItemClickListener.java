package com.est.streamcorn.adapters.base;

import android.view.View;

/**
 * Created by Matteo on 29/01/2018.
 */

public interface OnItemClickListener<T> {
    void onItemClick(View view, T item);
}

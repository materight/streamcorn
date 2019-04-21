package com.est.streamcorn.adapters.base;

import android.util.SparseArray;
import android.widget.BaseAdapter;

/**
 * Created by Matteo on 01/02/2018.
 */

public abstract class SparseArrayAdapter<E> extends BaseAdapter {

    private SparseArray<E> mData;

    public void setData(SparseArray<E> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public E getItem(int position) {
        return mData.valueAt(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.keyAt(position);
    }
}
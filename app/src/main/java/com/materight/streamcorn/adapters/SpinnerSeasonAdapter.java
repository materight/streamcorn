package com.materight.streamcorn.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.materight.streamcorn.R;
import com.materight.streamcorn.adapters.base.SparseArrayAdapter;
import com.materight.streamcorn.scrapers.models.Season;

/**
 * Created by Matteo on 01/02/2018.
 */

public class SpinnerSeasonAdapter extends SparseArrayAdapter<Season> {

    private final LayoutInflater mInflater;
    private final Context mContext;

    public SpinnerSeasonAdapter(Context context, SparseArray<Season> data) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        setData(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView result = (TextView) convertView;
        if (result == null) {
            result = (TextView) mInflater.inflate(R.layout.item_spinner_season, parent, false);
        }
        result.setText(mContext.getString(R.string.season_item, getItemId(position)));
        return result;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView result = (TextView) convertView;
        if (result == null) {
            result = (TextView) mInflater.inflate(R.layout.item_spinner_season_dropdown, parent, false);
        }
        result.setText(mContext.getString(R.string.season_item, getItemId(position)));
        return result;
    }
}

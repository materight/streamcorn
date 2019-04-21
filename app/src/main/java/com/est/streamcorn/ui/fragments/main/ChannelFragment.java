package com.est.streamcorn.ui.fragments.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.ChannelAdapter;
import com.est.streamcorn.network.channels.ChannelService;
import com.est.streamcorn.network.channels.Cineblog01;
import com.est.streamcorn.network.channels.FilmSenzaLimiti;

import java.util.ArrayList;

public class ChannelFragment extends Fragment {

    private static final String TAG = "ChannelFragment";

    protected RecyclerView mRecyclerView;
    protected ChannelAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<ChannelService> channelServiceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_channel, container, false);
        rootView.setTag(TAG);

        mRecyclerView = rootView.findViewById(R.id.channels_list);
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mAdapter = new ChannelAdapter(this.getContext(), Glide.with(this), channelServiceList);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }


    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset() {
        channelServiceList = new ArrayList<>();
        channelServiceList.add(new Cineblog01());
        channelServiceList.add(new FilmSenzaLimiti());
    }
}

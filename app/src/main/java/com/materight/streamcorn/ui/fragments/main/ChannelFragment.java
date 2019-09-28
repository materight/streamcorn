package com.materight.streamcorn.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.materight.streamcorn.R;
import com.materight.streamcorn.adapters.ChannelAdapter;
import com.materight.streamcorn.scrapers.channels.Channel;
import com.materight.streamcorn.scrapers.channels.Cineblog01;
import com.materight.streamcorn.scrapers.channels.FilmSenzaLimiti;

import java.util.ArrayList;

public class ChannelFragment extends Fragment {

    private static final String TAG = "ChannelFragment";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Channel> channelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_channel, container, false);
        rootView.setTag(TAG);

        recyclerView = rootView.findViewById(R.id.channels_list);
        layoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        ChannelAdapter adapter = new ChannelAdapter(this.getContext(), Glide.with(this), channelList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }


    private void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset() {
        channelList = new ArrayList<>();
        channelList.add(new Cineblog01());
        channelList.add(new FilmSenzaLimiti());
    }
}

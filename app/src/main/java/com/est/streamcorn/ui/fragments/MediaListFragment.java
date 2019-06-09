package com.est.streamcorn.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaAdapter;
import com.est.streamcorn.scrapers.ChannelService;
import com.est.streamcorn.scrapers.channels.Channel;
import com.est.streamcorn.scrapers.models.Media;
import com.est.streamcorn.scrapers.models.MediaType;
import com.est.streamcorn.ui.activities.MediaDetailActivity;
import com.est.streamcorn.ui.customs.EndlessRecyclerViewScrollListener;
import com.est.streamcorn.utils.Constants;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

import java.util.ArrayList;

public class MediaListFragment extends Fragment {

    private final static String TAG = "MediaListFragment";

    //  TODO: auto columns:number based on screen width
    private static final int COLUMNS_NUMBER = 3;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.medias_list)
    RecyclerView recyclerView;

    private boolean isVisible = false, isStarted = false, isFirstLoad = true;

    private MediaAdapter<Media> mediaAdapter;
    private GridLayoutManager gridLayoutManager;

    private CompositeDisposable compositeDisposable;
    private Channel channel;
    @MediaType
    private int listType;

    private String searchQuery = "";

    public static MediaListFragment newInstance(String channelId, @MediaType int listType) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LIST_TYPE_KEY, listType);
        args.putString(Constants.CHANNEL_KEY, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflateView = inflater.inflate(R.layout.fragment_media_list, container, false);
        ButterKnife.bind(this, inflateView);

        Bundle args = getArguments();
        listType = args.getInt(Constants.LIST_TYPE_KEY);
        channel = ChannelService.getChannelInstance(args.getString(Constants.CHANNEL_KEY));

        compositeDisposable = new CompositeDisposable();

        mediaAdapter = new MediaAdapter<>(Glide.with(this));
        mediaAdapter.setOnItemClickListener((view, item) -> {
            Context context = getActivity();
            if (context != null) {
                Intent intent = new Intent(context, MediaDetailActivity.class);
                intent.putExtra(Constants.MEDIA_KEY, item);
                intent.putExtra(Constants.CHANNEL_KEY, channel.getProperties().getDomain());
                context.startActivity(intent);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Refreshing " + (listType == 0 ? "movies" : "tv series") + " list");
            this.isFirstLoad = true;
            mediaAdapter.clearMedia();
            loadFirst();
            swipeRefreshLayout.setRefreshing(false);
        });

        gridLayoutManager = new GridLayoutManager(getActivity(), COLUMNS_NUMBER);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mediaAdapter.getItemViewType(position)) {
                    case MediaAdapter.VIEW_MEDIA:
                        return 1;
                    case MediaAdapter.VIEW_PROGRESS:
                        return COLUMNS_NUMBER;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mediaAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        return inflateView;
    }

    private void loadFirst() {
        loadMore(0);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!isFirstLoad) loadMore(page);
            }
        });
    }

    private void loadMore(final int page) {
        Log.d(TAG, "Loading page " + page + " of " + (listType == 0 ? "movies" : "tv series") + " list");
        compositeDisposable.add(channelLoadPage(page).subscribe(response -> {
            if (response != null && response.size() > 0) {
                mediaAdapter.addMedia(response);
            } else {
                mediaAdapter.setProgressMessage((mediaAdapter.getItemCount() > 1) ? "" : getString(R.string.no_search_results));
            }
        }, throwable -> {
            Log.e(TAG, "Error loading page " + page + " of " + (listType == 0 ? "movies" : "tv series") + " list", throwable);
            mediaAdapter.setProgressMessage((mediaAdapter.getItemCount() > 1) ? "" : getString(R.string.no_search_results));
        }));
    }

    private Single<ArrayList<Media>> channelLoadPage(int page) {
        switch (listType) {
            case MediaType.MOVIE:
                if (searchQuery == null || searchQuery.isEmpty())
                    return channel.getMovieList(page, getActivity());
                else
                    return channel.searchMovie(searchQuery, page, getActivity());
            case MediaType.TV_SERIES:
                if (searchQuery == null || searchQuery.isEmpty())
                    return channel.getTvSeriesList(page, getActivity());
                else
                    return channel.searchTvSeries(searchQuery, page, getActivity());
        }
        return null;
    }

    public void setSearchQuery(String searchQuery) {
        Log.d(TAG, "Searching " + searchQuery + " in " + (listType == 0 ? "movies" : "tv series") + " list");
        this.searchQuery = searchQuery;
        this.isFirstLoad = true;
        mediaAdapter.clearMedia();
        loadFirst();
    }

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;
        if (isVisible && isFirstLoad) {
            isFirstLoad = false;
            loadFirst();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isStarted = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isStarted && isFirstLoad) {
            isFirstLoad = false;
            loadFirst();
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

}
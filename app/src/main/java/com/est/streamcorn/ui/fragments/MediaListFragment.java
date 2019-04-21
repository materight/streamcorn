package com.est.streamcorn.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaAdapter;
import com.est.streamcorn.models.Media;
import com.est.streamcorn.network.ChannelService;
import com.est.streamcorn.network.channels.Channel;
import com.est.streamcorn.ui.activities.MediaDetailActivity;
import com.est.streamcorn.ui.customs.EndlessRecyclerViewScrollListener;
import com.est.streamcorn.utils.Constants;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import java.util.ArrayList;

public class MediaListFragment extends Fragment {

    private final static String TAG = "MediaListFragment";

    private static final int COLUMNS_NUMBER = 3;

    public static final int MOVIE_LIST = 0;
    public static final int TV_SERIES_LIST = 1;

    @IntDef({MOVIE_LIST, TV_SERIES_LIST})
    public @interface ListType {
    }

    @BindView(R.id.movies_list)
    RecyclerView recyclerView;

    private boolean isVisible = false, isStarted = false, isFirstLoad = true;

    private MediaAdapter mediaAdapter;
    private GridLayoutManager gridLayoutManager;

    private CompositeDisposable compositeDisposable;
    private Channel channel;
    @ListType
    private int listType;

    private String searchQuery = "";

    public static MediaListFragment newInstance(String channelId, @ListType int listType) {
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
        channel = ChannelService.getChannelInstance(args.getString(Constants.CHANNEL_KEY), getActivity());

        compositeDisposable = new CompositeDisposable();

        mediaAdapter = new MediaAdapter(Glide.with(this));
        mediaAdapter.setOnItemClickListener((view, item) -> {
            Context context = getActivity();
            if (context != null) {
                Intent intent = new Intent(context, MediaDetailActivity.class);
                intent.putExtra(Constants.MEDIA_KEY, item);
                intent.putExtra(Constants.CHANNEL_KEY, channel.getProperties().getDomain());
                context.startActivity(intent);
            }
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
                if (!isFirstLoad)
                    loadMore(page);
            }
        });
    }

    private void loadMore(int page) {
        Log.d(TAG, "Loading page " + page + " of " + (listType == 0 ? "movies" : "tv series") + " list");

        final Consumer<ArrayList<Media>> onSuccess = response -> {
            if (response != null && response.size() > 0) {
                mediaAdapter.addMedia(response);
            } else {
                mediaAdapter.setProgressMessage((mediaAdapter.getItemCount() > 1) ? "" : getString(R.string.no_search_results));
            }
        };

        final Consumer<? super Throwable> onError = error -> {
            Log.e(TAG, "onError: ");
            error.printStackTrace();
            mediaAdapter.setProgressMessage((mediaAdapter.getItemCount() > 1) ? "" : getString(R.string.no_search_results));

        };

        compositeDisposable.add(channelLoadPage(page, onSuccess, onError));
    }

    private Disposable channelLoadPage(int page, Consumer<ArrayList<Media>> onSuccess, Consumer<? super Throwable> onError) {
        switch (listType) {
            case MOVIE_LIST:
                if (searchQuery.isEmpty())
                    return channel.getMovieList(page).subscribe(onSuccess, onError);
                else
                    return channel.searchMovie(searchQuery, page).subscribe(onSuccess, onError);
            case TV_SERIES_LIST:
                if (searchQuery.isEmpty())
                    return channel.getTvSeriesList(page).subscribe(onSuccess, onError);
                else
                    return channel.searchTvSeries(searchQuery, page).subscribe(onSuccess, onError);
        }
        return null;
    }

    public void setSearchQuery(String searchQuery) {
        Log.d(TAG, "Searching " + searchQuery + " in " + (listType == 0 ? "movies" : "tv series") + " list");
        this.searchQuery = searchQuery;
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
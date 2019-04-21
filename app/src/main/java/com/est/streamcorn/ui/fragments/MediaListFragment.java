package com.est.streamcorn.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaAdapter;
import com.est.streamcorn.network.channels.Channel;
import com.est.streamcorn.network.channels.ChannelService;
import com.est.streamcorn.models.Media;
import com.est.streamcorn.ui.activities.MediaDetailActivity;
import com.est.streamcorn.ui.customs.EndlessRecyclerViewScrollListener;
import com.est.streamcorn.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MediaListFragment extends Fragment{

    private final static String TAG = "MediaListFragment";

    private static final int COLUMNS_NUMBER = 3;

    public static final int MOVIE_LIST = 0;
    public static final int TV_SERIES_LIST = 1;

    @IntDef({MOVIE_LIST, TV_SERIES_LIST})
    public @interface ListType {}

    @BindView(R.id.movies_list) RecyclerView recyclerView;

    private boolean isVisible = false, isStarted = false, isFirstLoad = true;

    private MediaAdapter mediaAdapter;
    private GridLayoutManager gridLayoutManager;

    private CompositeDisposable compositeDisposable;
    private ChannelService channelService;
    @ListType
    private int listType;

    private String searchQuery = "";

    public static MediaListFragment newInstance(@Channel int channelId, @ListType int listType) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.LIST_TYPE_KEY, listType);
        args.putInt(Constant.CHANNEL_KEY, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflateView = inflater.inflate(R.layout.fragment_media_list, container, false);
        ButterKnife.bind(this, inflateView);

        Bundle args = getArguments();
        listType = args.getInt(Constant.LIST_TYPE_KEY);
        channelService = ChannelService.getInstance(args.getInt(Constant.CHANNEL_KEY, ChannelService.INVALID));

        compositeDisposable = new CompositeDisposable();

        mediaAdapter = new MediaAdapter(Glide.with(this));
        mediaAdapter.setOnItemClickListener((view, item) -> {
            Context context = getActivity();
            if(context != null) {
                Intent intent = new Intent(context, MediaDetailActivity.class);
                intent.putExtra(Constant.MEDIA_KEY, item);
                intent.putExtra(Constant.CHANNEL_KEY, channelService.getChannelId());
                context.startActivity(intent);
            }
        });

        gridLayoutManager = new GridLayoutManager(getActivity(), COLUMNS_NUMBER);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mediaAdapter.getItemViewType(position)){
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
        recyclerView.setDrawingCacheEnabled(true);

        return inflateView;
    }

    private void loadFirst(){
        loadMore(0);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(!isFirstLoad)
                    loadMore(page);
            }
        });
    }

    private void loadMore(int page){
        Log.d(TAG, "type: " + listType + ", loading page " + page);

        final Consumer<ArrayList<Media>> onSuccess = response -> {
            if (response != null && response.size() > 0) {
                mediaAdapter.addMedia(response);
            }
            else {
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

    private Disposable channelLoadPage(int page, Consumer<ArrayList<Media>> onSuccess, Consumer<? super Throwable> onError){
        switch (listType){
            case MOVIE_LIST:
                if(searchQuery.isEmpty())
                    return channelService.getMovieList(page).subscribe(onSuccess, onError);
                else
                    return channelService.searchMovie(searchQuery, page).subscribe(onSuccess, onError);
            case TV_SERIES_LIST:
                if(searchQuery.isEmpty())
                    return channelService.getTvSeriesList(page).subscribe(onSuccess, onError);
                else
                    return channelService.searchTvSeries(searchQuery, page).subscribe(onSuccess, onError);
        }
        return null;
    }

    public void setSearchQuery(String searchQuery){
        Log.d(TAG, listType + " searching: "+searchQuery);
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
        if (isVisible && isStarted && isFirstLoad){
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
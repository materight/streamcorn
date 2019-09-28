package com.materight.streamcorn.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.materight.streamcorn.R;
import com.materight.streamcorn.adapters.MediaAdapter;
import com.materight.streamcorn.persistence.LibraryDatabase;
import com.materight.streamcorn.persistence.models.Media;
import com.materight.streamcorn.scrapers.models.MediaType;
import com.materight.streamcorn.ui.activities.MediaDetailActivity;
import com.materight.streamcorn.utils.Constants;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class LibraryListFragment extends Fragment {

    private final static String TAG = "MediaListFragment";

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.medias_list)
    RecyclerView recyclerView;

    private MediaAdapter<Media> mediaAdapter;

    private CompositeDisposable compositeDisposable;
    @MediaType
    private int listType;

    private String searchQuery = "";

    public static LibraryListFragment newInstance(@MediaType int listType) {
        LibraryListFragment fragment = new LibraryListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LIST_TYPE_KEY, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflateView = inflater.inflate(R.layout.fragment_media_list, container, false);
        ButterKnife.bind(this, inflateView);

        Bundle args = getArguments();
        listType = args.getInt(Constants.LIST_TYPE_KEY);

        compositeDisposable = new CompositeDisposable();

        mediaAdapter = new MediaAdapter<>(Glide.with(this));
        mediaAdapter.setOnItemClickListener((view, item) -> {
            Context context = getActivity();
            if (context != null) {
                Intent intent = new Intent(context, MediaDetailActivity.class);
                intent.putExtra(Constants.MEDIA_KEY, item);
                intent.putExtra(Constants.CHANNEL_KEY, item.channelId);
                context.startActivity(intent);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Refreshing library " + (listType == 0 ? "movies" : "tv series") + " list");
            mediaAdapter.clearMedia();
            load();
            swipeRefreshLayout.setRefreshing(false);
        });

        final int spanCount = getResources().getInteger(R.integer.grid_columns_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mediaAdapter.getItemViewType(position)) {
                    case MediaAdapter.VIEW_MEDIA:
                        return 1;
                    case MediaAdapter.VIEW_PROGRESS:
                        return spanCount;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mediaAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        load();

        return inflateView;
    }

    private void load() {
        Log.d(TAG, "Loading library " + (listType == 0 ? "movies" : "tv series") + " list");
        Single<List<Media>> loadOperation;
        if (listType == MediaType.MOVIE) {
            if (searchQuery == null || searchQuery.isEmpty())
                loadOperation = LibraryDatabase.getLibraryDatabase(getContext()).mediaDao().getAllMovies();
            else
                loadOperation = LibraryDatabase.getLibraryDatabase(getContext()).mediaDao().getMoviesByTitle(searchQuery);
        } else {
            if (searchQuery == null || searchQuery.isEmpty())
                loadOperation = LibraryDatabase.getLibraryDatabase(getContext()).mediaDao().getAllTvSeries();
            else
                loadOperation = LibraryDatabase.getLibraryDatabase(getContext()).mediaDao().getTvSeriesByTitle(searchQuery);
        }
        compositeDisposable.add(loadOperation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaList -> {
                    if (mediaList.size() > 0) {
                        mediaAdapter.addMedia(mediaList, false);
                    } else {
                        mediaAdapter.setProgressMessage(getString(R.string.no_items));
                    }
                }, throwable -> {
                    Log.e(TAG, "Library loading error: ", throwable);
                    Toast.makeText(getContext(), R.string.library_error, Toast.LENGTH_SHORT);
                }));
    }

    public void setSearchQuery(String searchQuery) {
        Log.d(TAG, "Searching " + searchQuery + " in library " + (listType == 0 ? "movies" : "tv series") + " list");
        this.searchQuery = searchQuery;
        mediaAdapter.clearMedia();
        load();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

}
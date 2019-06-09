package com.est.streamcorn.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaDetailAdapter;
import com.est.streamcorn.adapters.SpinnerSeasonAdapter;
import com.est.streamcorn.persistence.LibraryDatabase;
import com.est.streamcorn.scrapers.ChannelService;
import com.est.streamcorn.scrapers.channels.Channel;
import com.est.streamcorn.scrapers.models.MediaInterface;
import com.est.streamcorn.scrapers.models.MediaType;
import com.est.streamcorn.scrapers.models.StreamUrl;
import com.est.streamcorn.tmdb.TmdbClient;
import com.est.streamcorn.tmdb.models.TmdbMovie;
import com.est.streamcorn.tmdb.models.TmdbTvSeries;
import com.est.streamcorn.ui.customs.dialogs.MorphDialog;
import com.est.streamcorn.ui.customs.widgets.CustomCollapsingToolbarLayout;
import com.est.streamcorn.utils.Constants;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

public class MediaDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.collapsing_toolbar)
    CustomCollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.trailer_button)
    AppCompatButton trailerButton;
    @BindView(R.id.trailer_image)
    ImageView trailerImage;
    @BindView(R.id.container)
    RecyclerView containerRecyclerView;

    private static final String TAG = "MediaDetailActivity";
    private static final String TAG_FRAGMENT = "fragment";

    private MediaInterface media;
    private int tmdbId = -1;
    private boolean urlsDownloaded = false;

    protected Channel channel;
    private TmdbClient tmdbClient;

    private CompositeDisposable compositeDisposable;
    private MediaDetailAdapter mediaDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        media = intent.getParcelableExtra(Constants.MEDIA_KEY);

        String channelId = intent.getStringExtra(Constants.CHANNEL_KEY);
        channel = ChannelService.getChannelInstance(channelId);
        tmdbClient = new TmdbClient(this);

        compositeDisposable = new CompositeDisposable();

        mediaDetailAdapter = new MediaDetailAdapter(media, Glide.with(MediaDetailActivity.this));
        containerRecyclerView.setLayoutManager(new LinearLayoutManager(MediaDetailActivity.this));
        containerRecyclerView.setAdapter(mediaDetailAdapter);

        initTheme();

        //  Carico se il Media attuale è già salvato nella libreria
        compositeDisposable.add(LibraryDatabase.getLibraryDatabase(MediaDetailActivity.this).mediaDao()
                .contains(media.getUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(exist -> {
                    mediaDetailAdapter.setAddToLibrarySelected(exist);
                }));

        //  Set ClickListener for "add to library"
        mediaDetailAdapter.setAddToLibraryClickListener(view -> {
            Completable operation;
            int toastMessage;
            final boolean isSelected = view.isSelected();
            if (!isSelected) {
                //  Aggiungo alla libreria
                com.est.streamcorn.persistence.models.Media libraryMedia = new com.est.streamcorn.persistence.models.Media(media.getUrl(), media.getTitle(), media.getImageUrl(), channelId, media.getType());
                operation = LibraryDatabase.getLibraryDatabase(MediaDetailActivity.this).mediaDao().insert(libraryMedia);
                toastMessage = R.string.library_added;
            } else {
                //  Rimuovo dalla libreria
                operation = Completable.defer(() ->
                        //  Workaraound per non eseguire il metodo nel mainthread
                        LibraryDatabase.getLibraryDatabase(MediaDetailActivity.this).mediaDao()
                                .delete(media.getUrl())
                                .subscribeOn(Schedulers.io())
                );
                toastMessage = R.string.library_removed;
            }
            compositeDisposable.add(operation
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        view.setSelected(!isSelected);
                        Toast.makeText(MediaDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                        Toast.makeText(MediaDetailActivity.this, R.string.library_added, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating Room library: ", throwable);
                    }));
        });


        if (media.getType() == MediaType.MOVIE) {
            downloadMovieData();
        } else if (media.getType() == MediaType.TV_SERIES) {
            downloadTvSeriesData();
        }
    }

    private void downloadMovieData() {
        //  Get TMDB details
        compositeDisposable.add(tmdbClient.getMovieDetail(media.getEscapedTitle())
                .subscribe(response -> {
                    tmdbId = response.getId();
                    mediaDetailAdapter.setHeaderDetails(response);
                    setTrailerImage(response.getBackdropPath());
                    if (response.getVideos() != null && response.getVideos().size() > 0)
                        setTrailerVideo(response.getVideos().getFirst().getKey());
                    else
                        setTrailerVideo(null);
                }, throwable -> {
                    mediaDetailAdapter.setHeaderDetails((TmdbMovie) null);
                    Log.e(TAG, "Error while downloading movie details from TMDB, tmdbId: " + tmdbId, throwable);
                }));

        //  Get streaming links
        compositeDisposable.add(channel.getMovie(media.getUrl(), MediaDetailActivity.this)
                .subscribe(response -> {
                    mediaDetailAdapter.setUrlsLoaded();
                    mediaDetailAdapter.setPlayClickListener((view, item) -> {
                        showPlayUrlsList(view, response.getUrls());
                    });
                }, throwable -> {
                    Log.e(TAG, "Error while scraping movie links from url: " + media.getUrl() + ", from channel: " + channel.getProperties().getDomain(), throwable);
                }));
    }

    private void downloadTvSeriesData() {
        //  Get TMDB details
        compositeDisposable.add(tmdbClient.getTvSeriesDetail(media.getEscapedTitle())
                .subscribe(response -> {
                    tmdbId = response.getId();
                    if (urlsDownloaded)
                        downloadSeasonDetails();
                    mediaDetailAdapter.setHeaderDetails(response);
                    setTrailerImage(response.getBackdropPath());
                    if (response.getVideos() != null && response.getVideos().size() > 0)
                        setTrailerVideo(response.getVideos().getFirst().getKey());
                    else
                        setTrailerVideo(null);
                }, throwable -> {
                    mediaDetailAdapter.setHeaderDetails((TmdbTvSeries) null);
                    Log.e(TAG, "Error while downloading tv series details from TMDB, tmdbId: " + tmdbId, throwable);
                }));

        //  Get episodes links
        compositeDisposable.add(channel.getTvSeries(media.getUrl(), MediaDetailActivity.this)
                .subscribe(response -> {
                    urlsDownloaded = true;

                    mediaDetailAdapter.setPlayClickListener(this::showPlayUrlsList);
                    mediaDetailAdapter.setDownloadClickListener(this::showDownloadUrlsList);
                    mediaDetailAdapter.setSeasonSpinnerSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mediaDetailAdapter.swapEpisodes(response.getSeason((int) id).getEpisodes());
                            downloadSeasonDetails((int) id);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    mediaDetailAdapter.setSeasons(new SpinnerSeasonAdapter(this, response.getSeasons()));

                }, throwable -> {
                    Log.e(TAG, "Error while scraping episodes links from url: " + media.getUrl() + ", from channel: " + channel.getProperties().getDomain(), throwable);
                }));
    }

    private void downloadSeasonDetails(final int seasonNumber) {
        if (tmdbId != -1) {
            compositeDisposable.add(tmdbClient.getSeasonDetails(tmdbId, seasonNumber)
                    .subscribe(
                            response -> mediaDetailAdapter.setEpisodesDetails(response.getEpisodes()),
                            throwable -> Log.e(TAG, "Error while downloading episodes details from TMDB, tmdbId: " + tmdbId + ", seasonNumber: " + seasonNumber, throwable)
                    ));
        }
    }

    private void downloadSeasonDetails() {
        downloadSeasonDetails(mediaDetailAdapter.getSelectedSeason());
    }

    private void showPlayUrlsList(View fabView, ArrayList<StreamUrl> urls) {
        MorphDialog.PlayDialog(this)
                .title(media.getTitle())
                .streamUrls(urls)
                .withMorphAnimationFrom(fabView)
                .show();
    }

    private void showDownloadUrlsList(View fabView, ArrayList<StreamUrl> urls) {
        MorphDialog.DownloadDialog(this)
                .title(media.getTitle())
                .streamUrls(urls)
                .withMorphAnimationFrom(fabView)
                .show();
    }

    private void initTheme() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle.setText(media.getTitle());

        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        final int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
        collapsingToolbarLayout.setScrimVisibleHeightTrigger(actionBarHeight + 100);
        collapsingToolbarLayout.setListener(showing -> {
            if (showing) {
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.animate().alpha(1).setDuration(250);
            } else {
                toolbarTitle.setVisibility(View.INVISIBLE);
                toolbarTitle.animate().alpha(0).setDuration(250);
            }
        });
    }

    private void setTrailerImage(@Nullable String url) {
        Glide.with(MediaDetailActivity.this)
                .load(url)
                .apply(new RequestOptions()
                        .fallback(R.drawable.media_backdrop))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(trailerImage);
    }

    private void setTrailerVideo(@Nullable String youtubeKey) {
        if (youtubeKey == null || youtubeKey.isEmpty())
            return;
        trailerButton.setVisibility(View.VISIBLE);
        trailerButton.setOnClickListener(v -> {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey));
            youtubeIntent.putExtra("force_fullscreen", true);
            startActivity(youtubeIntent);
        });
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }


}

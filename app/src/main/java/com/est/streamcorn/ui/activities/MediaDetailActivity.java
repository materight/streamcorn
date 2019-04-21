package com.est.streamcorn.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaDetailAdapter;
import com.est.streamcorn.adapters.SpinnerSeasonAdapter;
import com.est.streamcorn.tmdb.models.TmdbMovie;
import com.est.streamcorn.tmdb.models.TmdbTvSeries;
import com.est.streamcorn.network.channels.Channel;
import com.est.streamcorn.network.channels.ChannelService;
import com.est.streamcorn.models.Media;
import com.est.streamcorn.models.StreamUrl;
import com.est.streamcorn.tmdb.TmdbClient;
import com.est.streamcorn.ui.customs.dialogs.MorphDialog;
import com.est.streamcorn.ui.customs.widgets.CustomCollapsingToolbarLayout;
import com.est.streamcorn.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class MediaDetailActivity extends BaseActivity{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.collapsing_toolbar) CustomCollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.trailer_button) AppCompatButton trailerButton;
    @BindView(R.id.trailer_image) ImageView trailerImage;
    @BindView(R.id.container) RecyclerView containerRecyclerView;

    private static final String TAG = "MediaDetailActivity";
    private static final String TAG_FRAGMENT = "fragment";

    private Media media;
    private int tmdbId = -1;
    private boolean urlsDownloaded = false;

    protected ChannelService channelService;
    private TmdbClient tmdbClient;

    CompositeDisposable compositeDisposable;
    MediaDetailAdapter mediaDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        media = intent.getParcelableExtra(Constant.MEDIA_KEY);

        final @Channel int channelId = intent.getIntExtra(Constant.CHANNEL_KEY, ChannelService.INVALID);
        channelService = ChannelService.getInstance(channelId);
        tmdbClient = new TmdbClient(this);

        compositeDisposable = new CompositeDisposable();

        mediaDetailAdapter = new MediaDetailAdapter(media, Glide.with(this));
        containerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        containerRecyclerView.setAdapter(mediaDetailAdapter);

        initTheme();

        mediaDetailAdapter.setAddToLibraryClickListener(v -> {

        });

        if(media.getType() == Media.MOVIE)
            downloadMovieData();
        else if(media.getType() == Media.TV_SERIES)
            downloadTvSeriesData();
    }

    private void downloadMovieData(){
        //Get TMDB details
        compositeDisposable.add(tmdbClient.getMovieDetail(media.getEscapedTitle()).subscribe(
                response -> {
                    tmdbId = response.getId();
                    mediaDetailAdapter.setHeaderDetails(response);
                    setTrailerImage(response.getBackdropPath());
                    if(response.getVideos() != null && response.getVideos().size() > 0)
                        setTrailerVideo(response.getVideos().getFirst().getKey());
                    else
                        setTrailerVideo(null);
                }, error -> {
                    mediaDetailAdapter.setHeaderDetails((TmdbMovie) null);
                    Log.e(TAG, "Error getting details: ");
                    error.printStackTrace();
                }
        ));

        //Get movies links
        compositeDisposable.add(channelService.getMovie(media.getUrl()).subscribe(
                response -> {
                    mediaDetailAdapter.setPlayClickListener((view, item) -> {
                        showPlayUrlsList(view, response.getUrls());
                    });
                }, error -> {
                    Log.e(TAG, "Error getMovie");
                    error.printStackTrace();
                }
        ));
    }

    private void downloadTvSeriesData(){
        //Get TMDB details
        compositeDisposable.add(tmdbClient.getTvSeriesDetail(media.getEscapedTitle()).subscribe(
                response -> {
                    tmdbId = response.getId();
                    if(urlsDownloaded)
                        downloadSeasonDetails();
                    mediaDetailAdapter.setHeaderDetails(response);
                    setTrailerImage(response.getBackdropPath());
                    if(response.getVideos() != null && response.getVideos().size() > 0)
                        setTrailerVideo(response.getVideos().getFirst().getKey());
                    else
                        setTrailerVideo(null);
                }, error -> {
                    mediaDetailAdapter.setHeaderDetails((TmdbTvSeries) null);
                    Log.e(TAG, "Error getting details: ");
                    error.printStackTrace();
                }
        ));

        //Get episodes links
        compositeDisposable.add(channelService.getTvSeries(media.getUrl()).subscribe(
                response -> {
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
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                    mediaDetailAdapter.setSeasons(new SpinnerSeasonAdapter(this, response.getSeasons()));

                }, error -> {
                    Log.e(TAG, "Error getMovie");
                    error.printStackTrace();
                }
        ));
    }

    private void downloadSeasonDetails(final int seasonNumber){
        if(tmdbId != -1 && seasonNumber != Spinner.INVALID_ROW_ID){
            compositeDisposable.add(tmdbClient.getSeasonDetails(tmdbId, seasonNumber).subscribe(
                    response -> mediaDetailAdapter.setEpisodesDetails(response.getEpisodes()),
                    error -> {
                        Log.e(TAG, "Error episodes details:");
                        error.printStackTrace();
                    }
            ));
        }
    }

    private void downloadSeasonDetails(){
        downloadSeasonDetails(mediaDetailAdapter.getSelectedSeason());
    }

    private void showPlayUrlsList(View fabView, ArrayList<StreamUrl> urls){
        MorphDialog.PlayDialog(this)
                .title(getString(R.string.play_dialog_title))
                .streamUrls(urls)
                .withMorphAnimation(fabView)
                .show();
    }

    private void showDownloadUrlsList(View fabView, ArrayList<StreamUrl> urls){
        MorphDialog.DownloadDialog(this)
                .title(getString(R.string.download_dialog_title))
                .streamUrls(urls)
                .withMorphAnimation(fabView)
                .show();
    }

    private void initTheme(){
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
            if(showing){
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.animate().alpha(1).setDuration(250);
            }
            else{
                toolbarTitle.setVisibility(View.INVISIBLE);
                toolbarTitle.animate().alpha(0).setDuration(250);
            }
        });
    }

    private void setTrailerImage(@Nullable String url){
        Glide.with(MediaDetailActivity.this)
                .load(url)
                .apply(new RequestOptions()
                        .fallback(R.drawable.media_backdrop))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(trailerImage);
    }

    private void setTrailerVideo(@Nullable String youtubeKey){
        if(youtubeKey == null || youtubeKey.isEmpty())
            return;
        trailerButton.setVisibility(View.VISIBLE);
        trailerButton.setOnClickListener(v -> {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey));
            youtubeIntent.putExtra("force_fullscreen",true);
            startActivity(youtubeIntent);
        });
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }






















}

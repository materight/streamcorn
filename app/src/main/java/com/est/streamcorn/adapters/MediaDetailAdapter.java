package com.est.streamcorn.adapters;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.base.OnItemClickListener;
import com.est.streamcorn.scrapers.models.Episode;
import com.est.streamcorn.scrapers.models.Media;
import com.est.streamcorn.scrapers.models.MediaType;
import com.est.streamcorn.scrapers.models.StreamUrl;
import com.est.streamcorn.tmdb.models.TmdbEpisode;
import com.est.streamcorn.tmdb.models.TmdbMovie;
import com.est.streamcorn.tmdb.models.TmdbTvSeries;
import com.est.streamcorn.ui.customs.transitions.SlideInItemAnimator;
import com.est.streamcorn.ui.customs.widgets.ExpandableDescription;
import com.est.streamcorn.ui.customs.widgets.FadeTextSwitcher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matteo on 18/02/2018.
 */

public class MediaDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MediaDetailAdapter";

    private static final int HEADER_TYPE = 0;
    private static final int SPINNER_TYPE = 1;
    private static final int EPISODE_TYPE = 2;

    private static final int NO_POSITION = -1;
    private static final int EXPAND = 1;
    private static final int COLLAPSE = 2;

    private Media media;
    private SpinnerSeasonAdapter spinnerSeasonAdapter;
    private SparseArray<Episode> episodes;
    private SparseArray<TmdbEpisode> episodesDetails;

    private OnItemClickListener<ArrayList<StreamUrl>> downloadClickListener;
    private OnItemClickListener<ArrayList<StreamUrl>> playClickListener;
    private View.OnClickListener addToLibraryClickListener;
    private AdapterView.OnItemSelectedListener seasonSpinnerSelectedListener;

    private RequestManager glide;
    private RecyclerView recyclerView;
    private final EpisodeAnimator episodeAnimator;
    private final Transition expandCollapse;

    private int selectedSeason = 0;
    private int expandedPosition = NO_POSITION;

    public MediaDetailAdapter(Media media, RequestManager glide) {
        this.media = media;
        this.glide = glide;
        episodes = new SparseArray<>();
        episodesDetails = new SparseArray<>();
        episodeAnimator = new EpisodeAnimator();
        expandCollapse = new AutoTransition();
    }

    public void setSeasons(SpinnerSeasonAdapter spinnerSeasonAdapter) {
        this.spinnerSeasonAdapter = spinnerSeasonAdapter;
        notifyItemInserted(1);
    }

    public int getSelectedSeason() {
        return selectedSeason;
    }

    public void swapEpisodes(SparseArray<Episode> newEpisodes) {
        final int previousSize = episodes.size();
        episodes = newEpisodes;
        final int newSize = episodes.size();
        episodesDetails.clear();
        expandedPosition = NO_POSITION;
        if (previousSize == 0) {
            notifyItemRangeInserted(2, episodes.size());
        } else {
            final int offset = newSize - previousSize;
            if (offset == 0)
                notifyItemRangeChanged(2, previousSize);
            else if (offset > 0) {  //Added more elements than before
                notifyItemRangeChanged(2, previousSize);
                notifyItemRangeInserted(previousSize, offset);
            } else {  //Added less elements than before
                notifyItemRangeChanged(2, newSize);
                notifyItemRangeRemoved(newSize, -offset);
            }
        }
    }

    public void setEpisodesDetails(final List<TmdbEpisode> episodeList) {
        for (TmdbEpisode episode : episodeList) {
            episodesDetails.put(episode.getNumber(), episode);
            final int position = episodes.indexOfKey(episode.getNumber()) + 2;
            notifyItemChanged(position, episode);
        }
    }

    static class DetailsContainer {
        String text1;
        String text2;
        String overviewText;

        DetailsContainer(final Context context) {
            text1 = "-";
            text2 = "-";
            overviewText = context.getString(R.string.empty_description);
        }

        DetailsContainer(final Context context, final TmdbMovie tmdbMovie) {
            this(context);
            if (tmdbMovie != null) {
                final int year = tmdbMovie.getReleaseYear();
                final int duration = tmdbMovie.getDuration();
                final String overview = tmdbMovie.getOverview();

                if (year != 0)
                    text1 = String.valueOf(year);
                if (duration != 0)
                    text2 = context.getString(R.string.media_duration, duration);
                if (overview != null && !overview.isEmpty())
                    overviewText = overview;
            }
        }

        DetailsContainer(final Context context, final TmdbTvSeries tmdbTvSeries) {
            this(context);
            if (tmdbTvSeries != null) {
                final int firstAirYear = tmdbTvSeries.getFirstAirYear();
                final int lastAirYear = tmdbTvSeries.getLastAirYear();
                final int seasonNumber = tmdbTvSeries.getSeasonNumber();
                final String overview = tmdbTvSeries.getOverview();

                if (firstAirYear == 0) text1 = "-";
                else if (lastAirYear == 0) text1 = firstAirYear + " - ?";
                else if (firstAirYear == lastAirYear) text1 = String.valueOf(firstAirYear);
                else text1 = firstAirYear + " - " + lastAirYear;

                if (seasonNumber != 0)
                    text2 = context.getString(R.string.seasons_number, seasonNumber);
                if (overview != null && !overview.isEmpty())
                    overviewText = overview;
            }
        }
    }

    public void setHeaderDetails(@Nullable final TmdbMovie tmdbMovie) {
        notifyItemChanged(0, new DetailsContainer(recyclerView.getContext(), tmdbMovie));
    }

    public void setHeaderDetails(@Nullable final TmdbTvSeries tmdbTvSeries) {
        notifyItemChanged(0, new DetailsContainer(recyclerView.getContext(), tmdbTvSeries));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == HEADER_TYPE) {
            holder = new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details_header, parent, false));
        } else if (viewType == SPINNER_TYPE) {
            holder = new SpinnerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details_spinner, parent, false));
        } else {
            holder = new EpisodeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details_episode, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int itemPosition) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerTitle.setText(media.getTitle());
            glide.load(media.getImageUrl())
                    .apply(new RequestOptions()
                            .error(R.drawable.media_poster))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(((HeaderViewHolder) holder).posterImage);
        } else if (holder instanceof EpisodeViewHolder) {
            ((EpisodeViewHolder) holder).number.setText(String.valueOf(episodes.keyAt(itemPosition - 2)));
            ((EpisodeViewHolder) holder).details.setVisibility(View.GONE);
            ((EpisodeViewHolder) holder).itemView.setActivated(false);
            if (episodesDetails.size() != 0) {
                setEpisodeDetail((EpisodeViewHolder) holder, episodesDetails.valueAt(itemPosition - 2), false);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (holder instanceof HeaderViewHolder) {
            if (!payloads.isEmpty() && payloads.get(0) instanceof DetailsContainer) {
                DetailsContainer details = (DetailsContainer) payloads.get(0);
                ((HeaderViewHolder) holder).headerText1.setText(details.text1);
                ((HeaderViewHolder) holder).headerText2.setText(details.text2);
                ((HeaderViewHolder) holder).descriptionTextView.setText(details.overviewText);
            } else if (!payloads.isEmpty() && payloads.get(0) instanceof Boolean) {
                ((HeaderViewHolder) holder).addToLibraryButton.setSelected((Boolean) payloads.get(0));
            } else {
                onBindViewHolder(holder, position);
            }
        } else if (holder instanceof EpisodeViewHolder) {
            if (payloads.contains(EXPAND) || payloads.contains(COLLAPSE)) {
                setEpisodeExpanded((EpisodeViewHolder) holder, position == expandedPosition);
            } else if (!payloads.isEmpty() && payloads.get(0) instanceof TmdbEpisode) {
                setEpisodeDetail((EpisodeViewHolder) holder, (TmdbEpisode) payloads.get(0), true);
            } else {
                onBindViewHolder(holder, position);
            }
        }
    }

    private void setEpisodeDetail(EpisodeViewHolder holder, TmdbEpisode tmdbEpisode, boolean animated) {
        if (tmdbEpisode != null) {
            if (animated)
                holder.title.setText(tmdbEpisode.getName());
            else
                holder.title.setCurrentText(tmdbEpisode.getName());
            holder.details.setText(tmdbEpisode.getOverview());
        }
    }

    private void setEpisodeExpanded(EpisodeViewHolder holder, boolean isExpanded) {
        holder.itemView.setActivated(isExpanded);
        holder.details.setVisibility((isExpanded) ? View.VISIBLE : View.GONE);
    }

    public void setPlayClickListener(OnItemClickListener<ArrayList<StreamUrl>> onItemClickListener) {
        playClickListener = onItemClickListener;
    }

    public void setDownloadClickListener(OnItemClickListener<ArrayList<StreamUrl>> onItemClickListener) {
        downloadClickListener = onItemClickListener;
    }

    public void setAddToLibraryClickListener(View.OnClickListener onClickListener) {
        addToLibraryClickListener = onClickListener;
    }

    public void setAddToLibrarySelected(Boolean selected) {
        notifyItemChanged(0, selected);
    }

    public void setSeasonSpinnerSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        seasonSpinnerSelectedListener = onItemSelectedListener;
    }

    @Override
    public int getItemCount() {
        if (media.getType() == MediaType.MOVIE)
            return 1;
        else if (spinnerSeasonAdapter == null)
            return 1;
        else
            return 2 + episodes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_TYPE;
        else if (position == 1)
            return SPINNER_TYPE;
        else
            return EPISODE_TYPE;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageButton addToLibraryButton;
        TextView headerTitle;
        FadeTextSwitcher headerText1;
        FadeTextSwitcher headerText2;
        ImageView posterImage;
        ExpandableDescription descriptionTextView;
        FloatingActionButton playButton;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.title);
            headerText1 = itemView.findViewById(R.id.text1);
            headerText2 = itemView.findViewById(R.id.text2);
            addToLibraryButton = itemView.findViewById(R.id.add_to_library_button);
            posterImage = itemView.findViewById(R.id.poster_image);
            descriptionTextView = itemView.findViewById(R.id.description);
            playButton = itemView.findViewById(R.id.play_button);

            addToLibraryButton.setOnClickListener(addToLibraryClickListener);

            if (media.getType() == MediaType.MOVIE) {
                descriptionTextView.setMaxLines(Integer.MAX_VALUE);
                playButton.setOnClickListener(v -> playClickListener.onItemClick(v, null));
            } else {
                playButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    class SpinnerViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;

        SpinnerViewHolder(View itemView) {
            super(itemView);
            spinner = itemView.findViewById(R.id.season_selector);

            spinner.setAdapter(spinnerSeasonAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedSeason = (int) id;
                    seasonSpinnerSelectedListener.onItemSelected(parent, view, position, id);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView number;
        FadeTextSwitcher title;
        ImageButton downloadButton;
        FloatingActionButton playButton;
        TextView details;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            title = itemView.findViewById(R.id.title);
            downloadButton = itemView.findViewById(R.id.download_button);
            playButton = itemView.findViewById(R.id.play_button);
            details = itemView.findViewById(R.id.details);

            downloadButton.setOnClickListener(v -> {
                downloadClickListener.onItemClick(v, episodes.valueAt(getAdapterPosition() - 2).getUrls());
            });

            playButton.setOnClickListener(v -> {
                playClickListener.onItemClick(v, episodes.valueAt(getAdapterPosition() - 2).getUrls());
            });

            this.itemView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                if (position == NO_POSITION) return;

                TransitionManager.beginDelayedTransition(recyclerView, expandCollapse);
                episodeAnimator.setAnimateMoves(false);

                //  Collapse any currently expanded items
                if (expandedPosition != NO_POSITION) {
                    notifyItemChanged(expandedPosition, COLLAPSE);
                }

                //  Expand clicked item
                if (expandedPosition != position) {
                    expandedPosition = position;
                    notifyItemChanged(position, EXPAND);
                } else {
                    expandedPosition = NO_POSITION;
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        this.recyclerView.setItemAnimator(episodeAnimator);

        expandCollapse.setDuration(recyclerView.getContext().getResources().getInteger(R.integer.episode_expand_collapse_duration));
        expandCollapse.setInterpolator(AnimationUtils.loadInterpolator(this.recyclerView.getContext(), android.R.interpolator.fast_out_slow_in));
        expandCollapse.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(android.transition.Transition transition) {
                MediaDetailAdapter.this.recyclerView.setOnTouchListener((v, event) -> {
                    v.performClick();
                    return true;
                });
            }

            @Override
            public void onTransitionEnd(android.transition.Transition transition) {
                episodeAnimator.setAnimateMoves(true);
                MediaDetailAdapter.this.recyclerView.setOnTouchListener(null);
            }

            @Override
            public void onTransitionCancel(android.transition.Transition transition) {
            }

            @Override
            public void onTransitionPause(android.transition.Transition transition) {
            }

            @Override
            public void onTransitionResume(android.transition.Transition transition) {
            }
        });
    }

    static class EpisodeAnimator extends SlideInItemAnimator {
        private boolean animateMoves = false;

        EpisodeAnimator() {
            super();
        }

        void setAnimateMoves(boolean animateMoves) {
            this.animateMoves = animateMoves;
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            if (!animateMoves) {
                dispatchMoveFinished(holder);
                return false;
            }
            return super.animateMove(holder, fromX, fromY, toX, toY);
        }
    }
}

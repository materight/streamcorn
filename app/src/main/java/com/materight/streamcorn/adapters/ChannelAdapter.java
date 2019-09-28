package com.materight.streamcorn.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.materight.streamcorn.R;
import com.materight.streamcorn.scrapers.channels.Channel;
import com.materight.streamcorn.ui.activities.MediaListActivity;
import com.materight.streamcorn.utils.Constants;
import com.materight.streamcorn.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Matteo on 05/01/2018.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private final RequestManager glide;
    private ArrayList<Channel> channels = new ArrayList<>();

    public ChannelAdapter(Context context, RequestManager glide) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.glide = glide;
        this.channels = new ArrayList<>();
    }

    public ChannelAdapter(Context context, RequestManager glide, ArrayList<Channel> channels) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.glide = glide;
        this.channels = channels;
    }

    public void addChannels(ArrayList<Channel> channels) {
        int newFirstMovie = this.channels.size();
        this.channels.addAll(channels);
        this.notifyItemRangeInserted(newFirstMovie, channels.size() - 1);
    }

    // inflates the cell layout from xml when needed
    @Override
    public ChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_channel, parent, false);
        return new ChannelAdapter.ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ChannelAdapter.ViewHolder holder, int position) {
        Channel channel = channels.get(position);
        holder.title.setText(Utils.getColoredString(channel.getProperties().getParametricName(), null));
        glide.load(channel.getProperties().getBannerDrawable())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.media_poster))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }
    }

    Channel getItem(int id) {
        return channels.get(id);
    }

    private void onItemClick(View view, int position) {
        Intent intent = new Intent(context, MediaListActivity.class);
        intent.putExtra(Constants.CHANNEL_KEY, channels.get(position).getProperties().getDomain());
        context.startActivity(intent);
    }
}
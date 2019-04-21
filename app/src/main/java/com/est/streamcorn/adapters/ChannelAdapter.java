package com.est.streamcorn.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.est.streamcorn.R;
import com.est.streamcorn.network.channels.ChannelService;
import com.est.streamcorn.ui.activities.MediaListActivity;
import com.est.streamcorn.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Matteo on 05/01/2018.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private final RequestManager glide;
    private ArrayList<ChannelService> channelServices = new ArrayList<>();

    public ChannelAdapter(Context context, RequestManager glide) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.glide = glide;
        this.channelServices = new ArrayList<>();
    }

    public ChannelAdapter(Context context, RequestManager glide, ArrayList<ChannelService> channelServices) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.glide = glide;
        this.channelServices = channelServices;
    }

    public void addChannels(ArrayList<ChannelService> channelServices){
        int newFirstMovie = this.channelServices.size();
        this.channelServices.addAll(channelServices);
        this.notifyItemRangeInserted(newFirstMovie,  channelServices.size() - 1);
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
        ChannelService channelService = channelServices.get(position);
        holder.title.setText(Utils.getColoredString(channelService.getParametricName(), null));
        glide.load(channelService.getBannerDrawable())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.media_poster))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return channelServices.size();
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

    ChannelService getItem(int id) {
        return channelServices.get(id);
    }

    private void onItemClick(View view, int position) {
        Intent intent = new Intent(context, MediaListActivity.class);
        intent.putExtra("channel", channelServices.get(position).getChannelId());
        context.startActivity(intent);
    }
}
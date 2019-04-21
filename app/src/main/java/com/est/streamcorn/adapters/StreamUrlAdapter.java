package com.est.streamcorn.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.est.streamcorn.R;
import com.est.streamcorn.adapters.base.OnItemClickListener;
import com.est.streamcorn.models.StreamUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matteo on 14/01/2018.
 */

public class StreamUrlAdapter extends RecyclerView.Adapter<StreamUrlAdapter.ViewHolder> {

    private List<StreamUrl> urls;
    private OnItemClickListener<StreamUrl> onItemClickListener;

    public StreamUrlAdapter() {
        this.urls = new ArrayList<>();
    }

    public StreamUrlAdapter(List<StreamUrl> urls) {
        this.urls = urls;
    }

    public void setUrls(List<StreamUrl> urls){
        this.urls.clear();
        this.urls.addAll(urls);
        notifyDataSetChanged();
    }

    @Override
    public StreamUrlAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_url, parent, false);
        return new StreamUrlAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StreamUrlAdapter.ViewHolder holder, int position) {
        StreamUrl url = urls.get(position);
        holder.name.setText(url.getName());
    }

    public void setOnItemClickListener(OnItemClickListener<StreamUrl> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, urls.get(getAdapterPosition()));
        }
    }

    StreamUrl getItem(int id) {
        return urls.get(id);
    }

    private void onItemClick(View view, int position) {

    }
}

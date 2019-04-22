package com.est.streamcorn.persistence.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.est.streamcorn.scrapers.ChannelService;
import com.est.streamcorn.scrapers.models.MediaType;

@Entity
public class Media {

    @PrimaryKey
    public String url;

    @ColumnInfo
    public String title;

    @ColumnInfo
    public String imageURL;

    @ColumnInfo
    @ChannelService.ChannelType
    public String channelId;

    @ColumnInfo
    @MediaType
    public int type;

    public Media(String url, String title, String imageURL, @ChannelService.ChannelType String channelId, @MediaType int type) {
        this.url = url;
        this.title = title;
        this.imageURL = imageURL;
        this.channelId = channelId;
        this.type = type;
    }
}

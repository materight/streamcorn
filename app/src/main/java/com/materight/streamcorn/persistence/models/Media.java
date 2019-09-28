package com.materight.streamcorn.persistence.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.materight.streamcorn.scrapers.ChannelService;
import com.materight.streamcorn.scrapers.models.MediaInterface;
import com.materight.streamcorn.scrapers.models.MediaType;

@Entity
public class Media extends MediaInterface implements Parcelable {

    @NonNull
    @PrimaryKey
    public String url;

    @ColumnInfo
    public String title;

    @ColumnInfo
    public String imageUrl;

    @ColumnInfo
    @MediaType
    public int type;

    @ColumnInfo
    @ChannelService.ChannelType
    public String channelId;

    public Media(String url, String title, String imageUrl, @ChannelService.ChannelType String channelId, @MediaType int type) {
        this.url = url;
        this.title = title;
        this.imageUrl = imageUrl;
        this.channelId = channelId;
        this.type = type;
    }

    @Override
    @NonNull
    public String getUrl() {
        return url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    @MediaType
    public int getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.url);
        parcel.writeString(this.title);
        parcel.writeString(this.imageUrl);
        parcel.writeInt(this.type);
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    private Media(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.imageUrl = in.readString();
        this.type = in.readInt();
    }
}

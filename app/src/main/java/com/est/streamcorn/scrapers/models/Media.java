package com.est.streamcorn.scrapers.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

/**
 * Created by Matteo on 30/12/2017.
 */

public class Media implements Parcelable {

    private String title;
    private String imageUrl;
    private String url;
    @MediaType
    private int type;

    public static final int MOVIE = 0;
    public static final int TV_SERIES = 1;
    public static final int UNKNOWN = 2;

    @IntDef({MOVIE, TV_SERIES, UNKNOWN})
    public @interface MediaType {
    }

    public Media(@Nullable String title, @Nullable String imageUrl, String url, @MediaType int type) {
        this.title = (title != null) ? title : "-";
        this.imageUrl = imageUrl;
        this.url = url;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getEscapedTitle() {
        return title.replaceAll("’", "'");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @MediaType
    public int getType() {
        return type;
    }

    public void setType(@MediaType int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.url);
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
        this.title = in.readString();
        this.imageUrl = in.readString();
        this.url = in.readString();
        this.type = in.readInt();
    }
}

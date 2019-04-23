package com.est.streamcorn.scrapers.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

public class Media extends MediaInterface implements Parcelable {

    private String url;
    private String title;
    private String imageUrl;
    @MediaType
    private int type;

    public Media() {
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

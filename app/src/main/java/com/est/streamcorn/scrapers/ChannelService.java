package com.est.streamcorn.scrapers;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import com.est.streamcorn.scrapers.channels.Channel;
import com.est.streamcorn.scrapers.channels.Cineblog01;
import com.est.streamcorn.scrapers.channels.FilmSenzaLimiti;

public class ChannelService {

    @StringDef({ChannelType.CINEBLOG01, ChannelType.FILMSENZALIMITI})
    public @interface ChannelType {
        String CINEBLOG01 = "cb01.date";
        String FILMSENZALIMITI = "filmsenzalimiti.beer";
    }

    @Nullable
    public static Channel getChannelInstance(@ChannelType String channel, Context context) {
        switch (channel) {
            case ChannelType.CINEBLOG01:
                return new Cineblog01();
            case ChannelType.FILMSENZALIMITI:
                return new FilmSenzaLimiti();
        }
        return null;
    }
}

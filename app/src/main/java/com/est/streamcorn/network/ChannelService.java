package com.est.streamcorn.network;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import com.est.streamcorn.network.channels.Channel;
import com.est.streamcorn.network.channels.Cineblog01;
import com.est.streamcorn.network.channels.FilmSenzaLimiti;

public class ChannelService {

    @StringDef({ChannelType.CINEBLOG01, ChannelType.FILMSENZALIMITI})
    public @interface ChannelType {
        String CINEBLOG01 = "cb01.pink";
        String FILMSENZALIMITI = "filmsenzalimiti.black";
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

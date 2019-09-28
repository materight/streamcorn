package com.est.streamcorn.scrapers;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import com.est.streamcorn.scrapers.channels.Channel;
import com.est.streamcorn.scrapers.channels.Cineblog01;
import com.est.streamcorn.scrapers.channels.FilmSenzaLimiti;
import com.est.streamcorn.scrapers.channels.IlGenioDelloStreaming;

public class ChannelService {

    @StringDef({ChannelType.CINEBLOG01, ChannelType.FILMSENZALIMITI})
    public @interface ChannelType {
        String CINEBLOG01 = "cb01.productions";
        String FILMSENZALIMITI = "filmsenzalimiti.beer";
        String ILGENIODELLOSTREAMING = "ilgeniodellostreaming.black";
    }

    @Nullable
    public static Channel getChannelInstance(@ChannelType String channel) {
        switch (channel) {
            case ChannelType.CINEBLOG01:
                return new Cineblog01();
            case ChannelType.FILMSENZALIMITI:
                return new FilmSenzaLimiti();
            case ChannelType.ILGENIODELLOSTREAMING:
                return new IlGenioDelloStreaming();
        }
        return null;
    }
}

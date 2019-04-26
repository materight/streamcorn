package com.est.streamcorn.scrapers.channels;

import android.util.Log;
import com.est.streamcorn.R;
import com.est.streamcorn.scrapers.ChannelService;
import com.est.streamcorn.scrapers.models.*;
import com.est.streamcorn.scrapers.utils.InfoExtractor;
import com.est.streamcorn.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cineblog01 extends Channel {

    private static final String TAG = "Cineblog01";

    private static final ChannelProperties properties = new ChannelProperties(ChannelService.ChannelType.CINEBLOG01,
            "Cineblog<font color=\"%s\">01</font>",
            R.drawable.channel_cineblog01,
            true,
            true,
            true,
            page -> String.format("page/%d/", page + 1),
            page -> String.format("serietv/page/%d/", page + 1),
            query -> page -> String.format("page/%d/?s=%s", page + 1, Utils.encodeQuery(query)),
            query -> page -> String.format("serietv/page/%d/?s=%s", page + 1, Utils.encodeQuery(query)));

    @Override
    public ChannelProperties getProperties() {
        return properties;
    }

    @Override
    protected ArrayList<Media> parseMovieList(Document document) throws Exception {
        return parseMediaList(document, MediaType.MOVIE);
    }

    @Override
    protected ArrayList<Media> parseTvSeriesList(Document document) throws Exception {
        return parseMediaList(document, MediaType.TV_SERIES);
    }

    @Override
    protected ArrayList<Media> parseSearchedMovieList(Document document) throws Exception {
        return parseMovieList(document);
    }

    @Override
    protected ArrayList<Media> parseSearchedTvSeriesList(Document document) throws Exception {
        return parseTvSeriesList(document);
    }

    @Override
    protected Movie parseMovieDetail(Document document) throws Exception {
        Movie movie = new Movie();
        Elements titleElements;
        Elements linkElements;
        titleElements = document.select("div#fancy-tabs li a");
        linkElements = document.select("div.tabs-catch-all[data-counter][data-src]");
        int len = linkElements.size();
        for (int i = 0; i < len; i++) {
            movie.addStreamUrl(new StreamUrl(titleElements.get(i).text(), linkElements.get(i).attr("data-src"), false));
        }
        return movie;
    }

    private static final Pattern TV_SERIES_NUMBER = Pattern.compile("([0-9]+)×([0-9]+)");

    @Override
    protected TvSeries parseTvSeriesDetail(Document document) throws Exception {
        TvSeries tvSeries = new TvSeries();
        Elements seasonsBlockElements = document.select(".sp-body p:contains(×), div.sp-wrap div.sp-head");
        String urlPrefix = "";
        for (Element element : seasonsBlockElements) {
            if (element.is("div")) {   //  Link type (HD, SUB-ITA, ...)
                String upperCase = element.html().toUpperCase();
                urlPrefix = upperCase.contains("SUB") ? "SUB-ITA " : "ITA ";
                urlPrefix += upperCase.contains("HD") ? "HD " : "";
                urlPrefix += upperCase.contains("HQ") ? "HQ " : "";
            } else if (element.is("p")) {   //  Season and episode number
                Integer seasonNumber = null, episodeNumber = null;
                Matcher m = TV_SERIES_NUMBER.matcher(element.html());
                if (m.find()) {
                    seasonNumber = Integer.parseInt(m.group(1));
                    episodeNumber = Integer.parseInt(m.group(2));
                }
                if (seasonNumber != null) {
                    ArrayList<StreamUrl> urls = new ArrayList<>();
                    Elements hrefs = element.select("a[href]");
                    for (Element a : hrefs) {
                        urls.add(new StreamUrl(urlPrefix + " " + a.html(), a.attr("href"), false));
                    }
                    tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
                }
            }
        }
        return tvSeries;
    }

    private ArrayList<Media> parseMediaList(Document document, int type) throws Exception {
        ArrayList<Media> movieList = new ArrayList<>();
        Elements elements = document.select("div.post");
        if (elements == null) return movieList;

        for (Element element : elements) {
            Element titleContainer, imgContainer;
            String url, title, imageUrl;
            url = title = imageUrl = null;
            if ((titleContainer = element.selectFirst("h3.card-title")) != null) {
                Element a;
                if ((a = titleContainer.selectFirst("a[href]")) != null) {
                    url = a.attr("href");
                    title = InfoExtractor.getTitle(a.text());
                }
            }
            if ((imgContainer = element.selectFirst("div.card-image img[src]")) != null) {
                imageUrl = imgContainer.attr("src");
                if (!imageUrl.startsWith("http"))    //  Aggiungo http:// all'url
                    imageUrl = getProperties().getBaseUrl() + imageUrl;
            }
            if (url != null && !(type == MediaType.TV_SERIES && (url.equals(getProperties().getBaseUrl() + "serietv/aggiornamento-quotidiano-serie-tv/") || url.equals(getProperties().getBaseUrl() + "serietv/richieste-serie-tv/")))) {
                movieList.add(new Media(title, imageUrl, url, type));
            }
        }
        return movieList;
    }


}

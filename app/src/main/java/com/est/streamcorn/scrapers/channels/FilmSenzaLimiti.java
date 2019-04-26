package com.est.streamcorn.scrapers.channels;

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

public class FilmSenzaLimiti extends Channel {

    private static final ChannelProperties properties = new ChannelProperties(
            ChannelService.ChannelType.FILMSENZALIMITI,
            "<font color=\"%s\">Film</font>SenzaLimiti",
            R.drawable.channel_filmsenzalimiti,
            true,
            true,
            false,
            page -> String.format("genere/film/page/%d/", page + 1),
            page -> String.format("genere/serie-tv/page/%d/", page + 1),
            query -> page -> String.format("genere/film/page/%d/?s=%s", page + 1, Utils.encodeQuery(query)),
            query -> page -> String.format("genere/serie-tv/page/%d/?s=%s", page + 1, Utils.encodeQuery(query))
    );

    @Override
    public ChannelProperties getProperties() {
        return properties;
    }

    //Methods
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
        return null;
    }

    @Override
    protected ArrayList<Media> parseSearchedTvSeriesList(Document document) throws Exception {
        return null;
    }

    @Override
    protected Movie parseMovieDetail(Document document) throws Exception {
        Movie movie = new Movie();
        Element element = document.selectFirst("iframe.embed-responsive-item[src]");
        if (element != null) {
            movie.addStreamUrl(new StreamUrl("Speedvideo", element.attr("src"), false));
        }
        return movie;
    }

    private static final Pattern TV_SERIES_URLS = Pattern.compile("(?:(\\d+)×(\\d+).*?)?<a href=\"([^\"]+)\".*?>([^<]+)</a>");

    @Override
    protected TvSeries parseTvSeriesDetail(Document document) throws Exception {
        TvSeries tvSeries = new TvSeries();
        Elements links = document.select(".pad p:contains(×), strong:contains(ita)");

        String urlPrefix = "";  //To show HD, SUB...
        for (Element element : links) {
            if (element.is("strong")) {
                urlPrefix = "";
                String upperCase = element.html().toUpperCase();
                urlPrefix += upperCase.contains("SUB") ? "SUB-ITA " : "ITA ";
                urlPrefix += upperCase.contains("HD") ? "HD " : "";
            } else if (element.is("p")) {
                Integer seasonNumber = null, episodeNumber = null;
                ArrayList<StreamUrl> urls = new ArrayList<>();
                Matcher m = TV_SERIES_URLS.matcher(element.html());
                while (m.find()) {
                    if (m.group(1) != null && m.group(2) != null) {  //If there is a new episode number, save the urls of previous episode
                        if (seasonNumber != null) {
                            tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
                            urls.clear();
                        }
                        seasonNumber = Integer.parseInt(m.group(1));
                        episodeNumber = Integer.parseInt(m.group(2));
                    }
                    urls.add(new StreamUrl(urlPrefix + m.group(4), m.group(3), false));
                }
                if (seasonNumber != null)
                    tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
            }
        }
        return tvSeries;
    }

    private ArrayList<Media> parseMediaList(Document document, int type) throws Exception {
        ArrayList<Media> moviesList = new ArrayList<>();
        Elements elements = document.select("ul.posts li");
        if (elements == null) return moviesList;

        for (Element element : elements) {
            Element a;
            String url, title, imageUrl;
            url = title = imageUrl = null;
            if ((a = element.selectFirst("a[href][data-thumbnail]")) != null) {
                imageUrl = a.attr("data-thumbnail");
                url = a.attr("href");
                title = InfoExtractor.getTitle(a.selectFirst("div.title").text());
            }
            moviesList.add(new Media(title, imageUrl, url, type));
        }
        return moviesList;
    }
}

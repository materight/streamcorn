package com.est.streamcorn.network.channels;

import com.est.streamcorn.R;
import com.est.streamcorn.models.Media;
import com.est.streamcorn.models.Movie;
import com.est.streamcorn.models.StreamUrl;
import com.est.streamcorn.models.TvSeries;
import com.est.streamcorn.network.UrlResolver;
import com.est.streamcorn.utils.Utils;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Matteo on 14/02/2018.
 */

public class Cineblog01 extends ChannelService {

    //Methods
    @Override
    protected ArrayList<Media> parseMovieList(Document document) throws IOException {
        return parseMediaList(document, Media.MOVIE);
    }

    @Override
    protected ArrayList<Media> parseTvSeriesList(Document document) throws IOException {
        return parseMediaList(document, Media.TV_SERIES);
    }

    @Override
    ArrayList<Media> parseSearchMovieList(Document document) throws IOException {
        return parseMovieList(document);
    }

    @Override
    ArrayList<Media> parseSearchTvSeriesList(Document document) throws IOException {
        return parseTvSeriesList(document);
    }

    @Override
    protected Movie parseMovie(Document document) throws IOException {
        Movie movie = new Movie();
        if(document == null)
            return movie;
        Elements titleElements;
        Elements linkElements;
        titleElements = document.select("div#fancy-tabs li a");
        linkElements = document.select("div.tabs-catch-all[data-counter][data-src]");
        int len = linkElements.size();
        for (int i=0; i < len; i++){
            movie.addUrl(new StreamUrl(titleElements.get(i).text(), linkElements.get(i).attr("data-src"), false));
        }
        return movie;
    }

    private static final Pattern TV_SERIES_NUMBER = Pattern.compile("([0-9]+)×([0-9]+)");

    @Override
    protected TvSeries parseTvSeries(Document document) throws IOException {
        TvSeries tvSeries = new TvSeries();
        if(document == null)
            return tvSeries;
        Elements seasonsBlockElements = document.select(".sp-body p:contains(×), div.sp-wrap div.sp-head");

        for (Element element: seasonsBlockElements) {
            String urlPrefix = "";
            if (element.is("div")) {   //  Link type (HD, SUB-ITA, ...)
                String upperCase = element.html().toUpperCase();
                urlPrefix += upperCase.contains("SUB") ? "SUB-ITA " : "ITA ";
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
                        urls.add(new StreamUrl(urlPrefix + a.html(), a.attr("href"), false));
                    }
                    tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
                }
            }
        }
        return tvSeries;
    }

    private ArrayList<Media> parseMediaList(Document doc, int type){
        Elements elements;
        ArrayList<Media> movieList = new ArrayList<>();
        if(doc == null || (elements = doc.select("div.post")) == null)
            return movieList;
        for (Element element : elements) {
            Element titleContainer, imgContainer;
            String url, title, imageUrl;
            url = title = imageUrl = null;
            if((titleContainer = element.selectFirst("h3.card-title")) != null) {
                Element a;
                if((a = titleContainer.selectFirst("a[href]")) != null) {
                    url = a.attr("href");
                    title = UrlResolver.getTitle(a.text());
                }
            }
            if((imgContainer = element.selectFirst("div.card-image img[src]")) != null) {
                imageUrl = imgContainer.attr("src");
                if(!imageUrl.startsWith("http"))    //  Rimouvo http:// dall'url
                    imageUrl = getHost() + "/" + imageUrl;
            }
            if(url != null && !(type ==  Media.TV_SERIES && (url.equals(getHost() + "serietv/aggiornamento-quotidiano-serie-tv/") || url.equals(getHost() + "serietv/richieste-serie-tv/")))){
                movieList.add(new Media(title, imageUrl, url, type));
            }
        }
        return movieList;
    }

    //Properties
    @Override
    public String getHost() { return "https://www.cb01.pink/"; }

    @Override
    public String getParametricName() { return "Cineblog<font color=\"%s\">01</font>"; }

    @Override
    public int getBannerDrawable() { return R.drawable.channel_cineblog01; }

    @Override
    public boolean hasMovie() { return true; }

    @Override
    public boolean hasTvSeries() { return true; }

    @Override
    public boolean canSearch() {
        return true;
    }

    @Override
    protected String getMovieListUrl(int page) { return getHost() + "page/" + (page + 1) + "/"; }

    @Override
    protected String getTvSeriesListUrl(int page) { return getHost() + "serietv/page/" + (page + 1) + "/"; }

    @Override
    String getMovieSearchUrl(String query, int page) {
        return getMovieListUrl(page) + "?s=" + Utils.encodeQuery(query);
    }

    @Override
    String getTvSeriesSearchUrl(String query, int page) {
        return getTvSeriesListUrl(page) + "?s=" + Utils.encodeQuery(query);
    }

    @Override
    public int getChannelId() { return CINEBLOG01; }
}

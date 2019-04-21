package com.est.streamcorn.network.channels;

import com.est.streamcorn.R;
import com.est.streamcorn.models.Media;
import com.est.streamcorn.models.Movie;
import com.est.streamcorn.models.StreamUrl;
import com.est.streamcorn.models.TvSeries;
import com.est.streamcorn.network.UrlResolver;

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

public class FilmSenzaLimiti extends ChannelService {

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
        return null;
    }

    @Override
    ArrayList<Media> parseSearchTvSeriesList(Document document) throws IOException {
        return null;
    }

    @Override
    protected Movie parseMovie(Document document) throws IOException {
        Movie movie = new Movie();
        Element element;
        if(document != null && (element = document.selectFirst("iframe.embed-responsive-item[src]")) != null) {
            movie.addUrl(new StreamUrl("Speedvideo", element.attr("src"), false));
        }
        return movie;
    }

    private static final Pattern TV_SERIES_URLS = Pattern.compile("(?:(\\d+)×(\\d+).*?)?<a href=\"([^\"]+)\".*?>([^<]+)<\\/a>");

    @Override
    protected TvSeries parseTvSeries(Document document) throws IOException {
        TvSeries tvSeries = new TvSeries();
        if(document == null)
            return tvSeries;
        Elements links = document.select(".pad p:contains(×), strong:containsSeason(ita)");

        String urlPrefix = "";  //To show HD, SUB...
        for (Element element: links) {
            if(element.is("strong")){
                urlPrefix = "";
                String upperCase = element.html().toUpperCase();
                urlPrefix += upperCase.contains("SUB") ? "SUB-ITA " : "ITA ";
                urlPrefix += upperCase.contains("HD") ? "HD " : "";
            }
            else if(element.is("p")) {
                Integer seasonNumber = null, episodeNumber = null;
                ArrayList<StreamUrl> urls = new ArrayList<>();
                Matcher m = TV_SERIES_URLS.matcher(element.html());
                while (m.find()){
                    if(m.group(1) != null && m.group(2) != null) {  //If there is a new episode number, save the urls of previous episode
                        if(seasonNumber != null) {
                            tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
                            urls.clear();
                        }
                        seasonNumber = Integer.parseInt(m.group(1));
                        episodeNumber = Integer.parseInt(m.group(2));
                    }
                    urls.add(new StreamUrl(urlPrefix + m.group(4), m.group(3), false));
                }
                if(seasonNumber != null)
                    tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
            }
        }
        return tvSeries;
    }

    private ArrayList<Media> parseMediaList(Document doc, int type){
        Elements elements;
        ArrayList<Media> moviesList = new ArrayList<>();
        if(doc == null || (elements = doc.select("ul.posts li")) == null)
            return moviesList;
        for (Element element : elements) {
            Element a;
            String url, title, imageUrl;
            url = title = imageUrl = null;
            if((a = element.selectFirst("a[href][data-thumbnail]")) != null) {
                imageUrl = a.attr("data-thumbnail");
                url = a.attr("href");
                title = UrlResolver.getTitle(a.selectFirst("div.title").text());
            }
            moviesList.add(new Media(title, imageUrl, url, type));
        }
        return moviesList;
    }

    //Properties
    @Override
    public String getHost() { return "http://filmsenzalimiti.black"; }

    @Override
    public String getParametricName() { return "<font color=\"%s\">Film</font>SenzaLimiti"; }

    @Override
    public int getBannerDrawable() { return R.drawable.channel_filmsenzalimiti; }

    @Override
    public boolean hasMovie() { return true; }

    @Override
    public boolean hasTvSeries() { return true; }

    @Override
    public boolean canSearch() {
        return false;
    }

    @Override
    protected String getMovieListUrl(int page) { return getHost() + "/page/" + (page+1) + "/?s=%5BHD%5D"; }

    @Override
    protected String getTvSeriesListUrl(int page) { return getHost() + "/genere/serie-tv/page/" + (page+1) + "/"; }

    @Override
    String getMovieSearchUrl(String query, int page) {
        return null;
    }

    @Override
    String getTvSeriesSearchUrl(String query, int page) {
        return null;
    }

    @Override
    public int getChannelId() { return FILMSENZALIMITI; }
}

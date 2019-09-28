package com.materight.streamcorn.scrapers.channels;

import com.materight.streamcorn.R;
import com.materight.streamcorn.scrapers.ChannelService;
import com.materight.streamcorn.scrapers.models.*;
import com.materight.streamcorn.scrapers.utils.InfoExtractor;
import com.materight.streamcorn.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

//  TODO: FIX
public class IlGenioDelloStreaming extends Channel {

    private static final String TAG = "IlGenioDelloStreaming";

    private static final ChannelProperties properties = new ChannelProperties(ChannelService.ChannelType.ILGENIODELLOSTREAMING,
            "Il<font color=\"%s\">Genio</font>DelloStreaming",
            R.drawable.channel_ilgeniodellostreaming,
            true,
            true,
            true,
            true,
            page -> String.format("film/page/%d/", page + 1),
            page -> String.format("serie/page/%d/", page + 1),
            query -> page -> String.format("page/%d/?s=%s", page + 1, Utils.encodeQuery(query)),
            query -> page -> String.format("page/%d/?s=%s", page + 1, Utils.encodeQuery(query)));

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
        return parseSearchedMediaList(document, MediaType.MOVIE);
    }

    @Override
    protected ArrayList<Media> parseSearchedTvSeriesList(Document document) throws Exception {
        return parseSearchedMediaList(document, MediaType.TV_SERIES);
    }

    @Override
    protected Movie parseMovieDetail(Document document) throws Exception {
        Movie movie = new Movie();
        String link = document.selectFirst("iframe.metaframe[src]").attr("src");
        movie.addStreamUrl(new StreamUrl("Openload", link, false));
        return movie;
    }

    @Override
    protected TvSeries parseTvSeriesDetail(Document document) throws Exception {
        TvSeries tvSeries = new TvSeries();
        Elements seasonsBlockElements = document.select(".seasons .se-c");
        String urlPrefix = "";
        for (Element element : seasonsBlockElements) {
            int seasonNumber = Integer.parseInt(element.selectFirst("span.se-t").text().replaceAll("[^\\d.]", ""));
            Elements episodes = element.select(".se-a li");
            for (Element episode : episodes) {
                int episodeNumber = Integer.parseInt(episode.selectFirst(".numerando").text().split("-")[1].replaceAll("[^\\d.]", ""));
                String url = episode.selectFirst(".episodiotitle a[href]").attr("href");
                ArrayList<StreamUrl> urls = new ArrayList<>();
                urls.add(new StreamUrl("Openload", url, false));
                tvSeries.putStreamUrls(seasonNumber, episodeNumber, urls);
            }
        }
        return tvSeries;
    }

    private ArrayList<Media> parseMediaList(Document document, int type) throws Exception {
        ArrayList<Media> movieList = new ArrayList<>();
        Elements elements = document.select("article.item.movies");

        for (Element element : elements) {
            Element poster = element.selectFirst(".poster a img[src]");
            Element data = element.selectFirst(".data h3 a[href]");

            String imageUrl, title, url;
            url = title = imageUrl = null;
            if (poster != null) {
                imageUrl = poster.attr("src");
            }
            if (data != null) {
                url = data.attr("href");
                title = data.text();
            }
            if (url != null) movieList.add(new Media(title, imageUrl, url, type));
        }
        return movieList;
    }

    private ArrayList<Media> parseSearchedMediaList(Document document, @MediaType int type) throws Exception {
        ArrayList<Media> movieList = new ArrayList<>();
        Elements elements = document.select("div.result-item article");

        for (Element element : elements) {
            Element posterContainer = element.selectFirst(".image a");
            Element typeLabel = posterContainer.selectFirst("span");

            //  Prendo solo i FILM/SERIE TV in base a cosa ho cercato
            if (typeLabel != null
                    && ((type == MediaType.MOVIE && typeLabel.text().toLowerCase().contains("film"))
                    || (type == MediaType.TV_SERIES && typeLabel.text().toLowerCase().contains("tv")))) {
                Element poster = posterContainer.selectFirst("img[src]");
                Element data = element.selectFirst(".details .title a[href]");

                String imageUrl, title, url;
                url = title = imageUrl = null;
                if (poster != null) {
                    imageUrl = poster.attr("src");
                }
                if (data != null) {
                    url = data.attr("href");
                    title = InfoExtractor.getTitle(data.text());
                }
                if (url != null) movieList.add(new Media(title, imageUrl, url, type));
            }
        }
        return movieList;
    }


}

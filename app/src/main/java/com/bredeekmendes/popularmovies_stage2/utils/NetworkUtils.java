package com.bredeekmendes.popularmovies_stage2.utils;

import android.content.Context;
import android.net.Uri;

import com.bredeekmendes.popularmovies_stage2.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by arthur on 5/3/18.
 */

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String QUERY_PARAM = "api_key";
    private static final String API_KEY = "";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";


    /**
     * This method will generate the appropriate URL based on @param urlType used
     * @param context
     * @param urlType
     * @return
     */
    public static URL getUrl (Context context, String urlType){
        if (urlType.equals(context.getResources().getString(R.string.pref_sortby_rate))){
            return buildTopRatedUrl();
        }
        if (urlType.equals(context.getResources().getString(R.string.pref_sortby_popularity))){
            return buildPopularUrl();
        }
        else return null;
    }

    private static URL buildTopRatedUrl() {
        String TOP_RATED = "top_rated";
        Uri movieQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(TOP_RATED)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();
        try{
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    private static URL buildPopularUrl() {
        String POPULAR = "popular";
        Uri movieQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(POPULAR)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();
        try{
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns a URL for the videos request of a movie with movieID ID.
     * @param movieID
     * @return
     */
    public static URL buildVideosUrl(String movieID) {
        String VIDEO = "videos";
        Uri movieQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(VIDEO)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();
        try{
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns a URL for the reviews of a movie with movieID ID.
     * @param movieID
     * @return
     */
    public static URL buildReviewsUrl(String movieID) {
        String REVIEW = "reviews";
        Uri movieQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(REVIEW)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();
        try{
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is responsible to make the URL request and returns the JSON answer from the server
     * @param url
     * @return
     * @throws IOException
     */
    public static String getJsonResponseFromUrlRequest (URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method is responsible for building the correct URL to retrieve the movie poster image
     * @param imageUrl
     * @return
     */
    public static String getImageUrl(String imageUrl){
        return IMAGE_BASE_URL + IMAGE_SIZE + imageUrl;
    }

    /**
     * This method is responsible for building the correct URL to retrieve the youtube movie video
     * @param key
     * @return
     */
    public static String getYoutubeVideoUrl(String key){
        return YOUTUBE_BASE_URL + key;
    }

}

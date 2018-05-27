/**
 * Created by arthur on 5/3/18.
 */
package com.bredeekmendes.popularmovies_stage2.utils;

import com.bredeekmendes.popularmovies_stage2.model.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

    private static final String TITLE = "title";
    private static final String POSTER = "poster_path";
    private static final String SYNOPSIS = "overview";
    private static final String RATING = "vote_average";
    private static final String RELEASE = "release_date";
    private static final String RESULTS = "results";
    private static final String KEY = "key";
    private static final String SITE = "site";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String ID = "id";

    /**
    *Locked away constructor. JsonUtils should not be instantiated.
    */
    private JsonUtils (){
        throw new AssertionError("No JsonUtils instances should be instantiated!");
    }

    /**
     * This method is responsible for parsing the JSON data and encapsulating it in the Movie object.
     * @param jsonString
     * @return
     */
    public static List<Movie> parseMovieJson (String jsonString){

        List<Movie> movieList = new ArrayList<>();
        if (!jsonString.isEmpty()){
            try{
                JSONObject movies = new JSONObject(jsonString);
                JSONArray results = movies.getJSONArray(RESULTS);
                for (int i=0; i<results.length(); i++){
                    JSONObject item = results.getJSONObject(i);
                    String title = item.getString(TITLE);
                    String synopsis = item.getString(SYNOPSIS);
                    String rating = item.getString(RATING);
                    String releaseDate = item.getString(RELEASE);
                    String poster_image = item.getString(POSTER);
                    String id = item.getString(ID);
                    Movie movie = new Movie();
                    movie.setTitle(title);
                    movie.setSynopsis(synopsis);
                    movie.setRating(rating);
                    movie.setReleaseDate(releaseDate);
                    movie.setImage(poster_image);
                    movie.setId(id);
                    movieList.add(movie);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return movieList;
    }

    /**
     * This method is responsible for parsing the JSON data for the videos associated with a
     * movie and returning a list with all the links.
     * @param jsonString
     * @return
     */
    public static List<String[]> parseVideosJson (String jsonString){
        List<String[]> videos = new ArrayList<>();
        if (!jsonString.isEmpty()) {
            try {
                JSONObject videosObject = new JSONObject(jsonString);
                JSONArray results = videosObject.getJSONArray(RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    String key = item.getString(KEY);
                    String site = item.getString(SITE);
                    String[] data = new String[2];
                    data[0]=key;
                    data[1]=site;
                    videos.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return videos;
    }

    /**
     * This method is responsible for parsing the JSON data for the reviews associated with a
     * movie and returning a list with all the reviews.
     * @param jsonString
     * @return
     */
    public static List<String[]> parseReviewsJson (String jsonString){
        List<String[]> reviews = new ArrayList<>();
        if (!jsonString.isEmpty()) {
            try {
                JSONObject reviewsObject = new JSONObject(jsonString);
                JSONArray results = reviewsObject.getJSONArray(RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    String author = item.getString(AUTHOR);
                    String content = item.getString(CONTENT);
                    String[] data = new String[2];
                    data[0]=author;
                    data[1]=content;
                    reviews.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reviews;
    }
}

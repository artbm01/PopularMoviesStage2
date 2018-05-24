package com.bredeekmendes.popularmovies_stage2.dbtools;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by arthur on 5/22/18.
 */

public class MovieContract {

    private static final String CONTENT_AUTHORITY = "com.movieproject";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_MOVIE = "movie";

    public static final class MovieDatabaseEntry implements BaseColumns {

        static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildMovieUriWithId(String id){
            return CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .build();
        }
    }
}

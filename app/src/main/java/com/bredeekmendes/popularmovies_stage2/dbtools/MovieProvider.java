package com.bredeekmendes.popularmovies_stage2.dbtools;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class MovieProvider extends ContentProvider{

    private static final String TAG = "DebugProvider";

    private static final int CODE_MOVIES = 100;
    private static final int CODE_MOVIES_WITH_ID = 101;

    private MovieDbHelper mMovieDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE,
                CODE_MOVIES
        );
        uriMatcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/*",
                CODE_MOVIES_WITH_ID
        );
        Log.d(TAG,"UriMatcher");
        Log.d(TAG,uriMatcher.toString());
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieDatabaseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieDatabaseEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieDatabaseEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                long id = db.insert(
                        MovieContract.MovieDatabaseEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id>0){
                    returnUri = ContentUris.withAppendedId(
                            MovieContract.MovieDatabaseEntry.CONTENT_URI, id);
                }
                else throw new UnsupportedOperationException("Unknown Uri");
                break;
            default: throw new UnsupportedOperationException("Default: Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int delCount;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES_WITH_ID:
                String idStr = uri.getLastPathSegment();
                String where = MovieContract.MovieDatabaseEntry.COLUMN_MOVIE_ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        MovieContract.MovieDatabaseEntry.TABLE_NAME,
                        where,
                        selectionArgs
                );
                break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return delCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}

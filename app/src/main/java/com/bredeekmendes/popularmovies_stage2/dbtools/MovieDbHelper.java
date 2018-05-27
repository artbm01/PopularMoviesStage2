package com.bredeekmendes.popularmovies_stage2.dbtools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bredeekmendes.popularmovies_stage2.dbtools.MovieContract.*;


/**
 * Created by arthur on 5/22/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ORCHID_DATABASE = "CREATE TABLE "+
                MovieDatabaseEntry.TABLE_NAME + " (" +
                MovieDatabaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieDatabaseEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieDatabaseEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieDatabaseEntry.COLUMN_IMAGE_URL + " TEXT, " +
                MovieDatabaseEntry.COLUMN_RATING + " TEXT, " +
                MovieDatabaseEntry.COLUMN_RELEASE_DATE + " TEXT," +
                MovieDatabaseEntry.COLUMN_SYNOPSIS + " TEXT," +
                " UNIQUE (" + MovieDatabaseEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_ORCHID_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //No upgrades so far
        /*sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieDatabaseEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);*/
    }
}

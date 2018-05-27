package com.bredeekmendes.popularmovies_stage2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bredeekmendes.popularmovies_stage2.dbtools.MovieContract;
import com.bredeekmendes.popularmovies_stage2.dbtools.MovieDbHelper;
import com.bredeekmendes.popularmovies_stage2.model.Movie;
import com.bredeekmendes.popularmovies_stage2.utils.JsonUtils;
import com.bredeekmendes.popularmovies_stage2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;

import static com.bredeekmendes.popularmovies_stage2.dbtools.MovieContract.MovieDatabaseEntry.buildMovieUriWithId;
import static com.bredeekmendes.popularmovies_stage2.utils.NetworkUtils.getJsonResponseFromUrlRequest;

public class MovieDetailActivity extends AppCompatActivity implements LoaderCallbacks<String[]>{

    private Movie myDetailedMovie;
    private RecyclerView mRecyclerViewVideos;
    private RecyclerView mRecyclerViewReviews;
    private MyVideosAdapter mVideosAdapter;
    private MyReviewsAdapter mReviewsAdapter;
    private String mId;
    private static final int DETAIL_LOADER_ID = 1;
    private ToggleButton mToggle;
    private SharedPreferences sharedPreferences;
    private SQLiteDatabase movieDatabase;
    private View separatorVideos;
    private View separatorReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        setTitle(R.string.detail_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String PARCELABLE_KEY = this.getResources().getString(R.string.parcelable_key);

        Intent intent = getIntent();
        myDetailedMovie = intent.getParcelableExtra(PARCELABLE_KEY);

        TextView mTitle = (TextView) findViewById(R.id.detail_title_tv);
        TextView mSynopsis = (TextView) findViewById(R.id.detail_synopsis_tv);
        TextView mRelease = (TextView) findViewById(R.id.detail_release_tv);
        TextView mRating = (TextView) findViewById(R.id.detail_rating_tv);
        ImageView mPoster = (ImageView) findViewById(R.id.detail_poster_iv);
        mToggle = (ToggleButton) findViewById(R.id.detail_fav_tb);

        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.rv_movie_videos);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_movie_reviews);

        separatorVideos = findViewById(R.id.alternate_separator_line_1);
        separatorReviews = findViewById(R.id.alternate_separator_line_2);

        String imageUrl = NetworkUtils.getImageUrl(myDetailedMovie.getImage());
        Picasso.with(this).load(imageUrl).into(mPoster);

        mTitle.setText(myDetailedMovie.getTitle());
        mSynopsis.setText(myDetailedMovie.getSynopsis());
        mRelease.setText(myDetailedMovie.getReleaseDate());
        mRating.setText(myDetailedMovie.getRating()+"/10");
        mId = myDetailedMovie.getId();

        RecyclerView.LayoutManager mVideosLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mReviewsLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewVideos.setLayoutManager(mVideosLayoutManager);
        mRecyclerViewReviews.setLayoutManager(mReviewsLayoutManager);
        mReviewsAdapter = new MyReviewsAdapter();
        mVideosAdapter = new MyVideosAdapter();
        mRecyclerViewVideos.setAdapter(mVideosAdapter);
        mRecyclerViewReviews.setAdapter(mReviewsAdapter);

        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this).forceLoad();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MovieDetailActivity.this);

        MovieDbHelper helper = new MovieDbHelper(this);
        movieDatabase = helper.getWritableDatabase();

        mToggle.setOnClickListener(new View.OnClickListener() {
            /**
             * Creates a listener to respond to every time the favorite button is clicked
             * The status of the button is saved to shared preferences
             * @param v
             */
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(myDetailedMovie.getId(),mToggle.isChecked());
                editor.apply();
                if (mToggle.isChecked()){
                    addMovieToFavorite();
                } else if (!mToggle.isChecked()){
                    removeMovieFromFavorite();
                }
            }
        });
        mToggle.setChecked(sharedPreferences.getBoolean(myDetailedMovie.getId(),false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String[]>(this) {

            String[] cacheVideoAndReviewData = new String[2];

            @Override
            protected void onStartLoading(){
                if (cacheVideoAndReviewData==null){
                    deliverResult(cacheVideoAndReviewData);
                }
                forceLoad();
            }

            @Override
            public String[] loadInBackground() {
                try {

                    cacheVideoAndReviewData[0] = getJsonResponseFromUrlRequest(NetworkUtils.buildVideosUrl(mId));
                    cacheVideoAndReviewData[1] = getJsonResponseFromUrlRequest(NetworkUtils.buildReviewsUrl(mId));

                    return cacheVideoAndReviewData;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] data) {
                cacheVideoAndReviewData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        List<String[]> cacheVideosData = JsonUtils.parseVideosJson(data[0]);
        List<String[]> cacheReviewsData = JsonUtils.parseReviewsJson(data[1]);

        if(!cacheVideosData.isEmpty()){
            myDetailedMovie.setVideos(cacheVideosData);
            mVideosAdapter.setVideosData(cacheVideosData);
        }

        if(!cacheReviewsData.isEmpty()){
            myDetailedMovie.setReviews(cacheReviewsData);
            mReviewsAdapter.setReviewsData(cacheReviewsData);
        }
        callReviews();
        callVideos();
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
    }

    /**
     * This class is the adapter for the recyclerview containing the movie trailers
     */
    public class MyVideosAdapter extends RecyclerView.Adapter<MyVideosAdapter.ViewHolder>{
        private List<String[]> mVideosData;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            final TextView mTrailerX;

            ViewHolder(View v) {
                super(v);
                mTrailerX = (TextView) v.findViewById(R.id.trailer_number_tv);
                mTrailerX.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                String trailerId = mVideosData.get(position)[0];
                String url = NetworkUtils.getYoutubeVideoUrl(trailerId);
                onClickOpenYoutubeButton(MovieDetailActivity.this, url);
            }
        }

        MyVideosAdapter(){
        }

        @Override
        public MyVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_videos_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            holder.mTrailerX.setText("Trailer "+(position+1));
        }

        @Override
        public int getItemCount(){
            if (mVideosData==null){
                return 0;
            }
            return mVideosData.size();
        }

        void setVideosData(List<String[]> myVideosData){
            mVideosData = myVideosData;
            notifyDataSetChanged();
        }
        Boolean isEmpty(){
            return (mVideosData==null);
        }
    }

    /**
     * This class is the adapter for the recyclerview containing the movie reviews
     */
    public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ViewHolder>{
        private List<String[]> mReviewsData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mAuthor;
            final TextView mComment;

            ViewHolder(View v) {
                super(v);
                mAuthor = (TextView) v.findViewById(R.id.author_name);
                mComment = (TextView) v.findViewById(R.id.author_comment);
            }
        }

        MyReviewsAdapter(){
        }

        @Override
        public MyReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_reviews_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            holder.mAuthor.setText(mReviewsData.get(position)[0]+":");
            holder.mComment.setText(mReviewsData.get(position)[1]);
        }

        @Override
        public int getItemCount(){
            if (mReviewsData==null){
                return 0;
            }
            return mReviewsData.size();
        }

        void setReviewsData(List<String[]> myReviewsData){
            mReviewsData = myReviewsData;
            notifyDataSetChanged();
        }

        Boolean isEmpty(){
            return (mReviewsData==null);
        }
    }


    private void callReviews(){
        if (mReviewsAdapter.isEmpty()){
            separatorReviews.setVisibility(View.INVISIBLE);
            mRecyclerViewReviews.setVisibility(View.INVISIBLE);
        }
        else {
            separatorReviews.setVisibility(View.VISIBLE);
            mRecyclerViewReviews.setVisibility(View.VISIBLE);
        }
    }

    private void callVideos(){
        if (mVideosAdapter.isEmpty()){
            separatorVideos.setVisibility(View.INVISIBLE);
            mRecyclerViewVideos.setVisibility(View.INVISIBLE);
        }
        else {
            separatorVideos.setVisibility(View.VISIBLE);
            mRecyclerViewVideos.setVisibility(View.VISIBLE);
        }
    }


    private void onClickOpenYoutubeButton(Context context, String url){
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try{
            context.startActivity(youtubeIntent);
        } catch (ActivityNotFoundException e){
            Toast.makeText(context, R.string.video_not_found,Toast.LENGTH_LONG).show();
        }
    }

    private void addMovieToFavorite(){
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieDatabaseEntry.COLUMN_MOVIE_ID, myDetailedMovie.getId());
        cv.put(MovieContract.MovieDatabaseEntry.COLUMN_TITLE, myDetailedMovie.getTitle());
        cv.put(MovieContract.MovieDatabaseEntry.COLUMN_IMAGE_URL, myDetailedMovie.getImage());
        cv.put(MovieContract.MovieDatabaseEntry.COLUMN_RATING, myDetailedMovie.getRating());
        cv.put(MovieContract.MovieDatabaseEntry.COLUMN_RELEASE_DATE, myDetailedMovie.getReleaseDate());
        cv.put(MovieContract.MovieDatabaseEntry.COLUMN_SYNOPSIS, myDetailedMovie.getSynopsis());
        getContentResolver().insert(MovieContract.MovieDatabaseEntry.CONTENT_URI, cv);
    }

    private void removeMovieFromFavorite(){
        String id = myDetailedMovie.getId();
        Uri uri = buildMovieUriWithId(id);
        getContentResolver().delete(
                uri,
                null,
                null
                );
    }
}

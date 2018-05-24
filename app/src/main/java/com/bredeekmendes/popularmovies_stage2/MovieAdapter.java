package com.bredeekmendes.popularmovies_stage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bredeekmendes.popularmovies_stage2.model.Movie;
import com.bredeekmendes.popularmovies_stage2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by arthur on 5/3/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context mContext;
    private List<Movie> myMovieData;
    private final OnClickedListener mListener;

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView mMoviePoster;

        MovieAdapterViewHolder(View itemView){
            super(itemView);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.movie_poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movieItem = myMovieData.get(adapterPosition);
            mListener.onClicked(movieItem);
        }
    }

    public interface OnClickedListener {
        void onClicked(Movie myMovie);
    }

    public MovieAdapter(Context context, OnClickedListener listener){
        mContext = context;
        mListener = listener;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_movie_list, parent, false);
        return new MovieAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
        Movie movieItem = myMovieData.get(position);
        String imageUrl = NetworkUtils.getImageUrl(movieItem.getImage());
        Picasso.with(mContext).load(imageUrl).into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (null == myMovieData) {
            return 0;
        }
        return myMovieData.size();
    }

    public void setMovieData(List<Movie> data) {
        myMovieData = data;
        notifyDataSetChanged();
    }

    public Boolean isEmpty(){
        return myMovieData.isEmpty();
    }
}

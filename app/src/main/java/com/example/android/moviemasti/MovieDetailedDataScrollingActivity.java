package com.example.android.moviemasti;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviemasti.DataManipulation.MovieData;
import com.squareup.picasso.Picasso;

public class MovieDetailedDataScrollingActivity extends AppCompatActivity {

    private TextView mContentTextView;
    private AppBarLayout appBarLayout;
    private ImageView mBackDropImageView;
    private ImageView mPosterImageView;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieRate;
    private String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detailed_data_scrolling);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
        setTitle("");
        mMovieTitle = (TextView)findViewById(R.id.movie_title);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mBackDropImageView = (ImageView)findViewById(R.id.movie_backdrop_image);
        mPosterImageView = (ImageView)findViewById(R.id.movie_poster_image);
        mMovieReleaseDate = (TextView)findViewById(R.id.movie_details_release_date);
        mMovieRate = (TextView)findViewById(R.id.movie_details_rate);
        Bundle intentData = getIntent().getExtras();
        MovieData movieData = intentData.getParcelable("movieIntentData");
        if(movieData!=null){
            Long movieId = movieData.getMovieId();
            movieTitle = movieData.getMovieTitle();
            String movieDescription = movieData.getMovieDescription();
            String moviePosterPath = movieData.getMoviePosterPath();
            String movieBackdropPath = movieData.getMovieBackdropPath();
            String movieReleaseDate = movieData.getMovieReleaseDate();
            String movieRate = movieData.getMovieVotes() + "/10";
            mMovieTitle.setText(movieTitle);
            mContentTextView.setText(movieDescription);
            mMovieReleaseDate.setText(movieReleaseDate);
            mMovieRate.setText(movieRate);
            loadingMovieBackDropImage(movieBackdropPath);
            loadingMoviePosterImage(moviePosterPath);
        }

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(movieTitle);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    public void loadingMovieBackDropImage(String imgUrlPartBackDrop) {
        if(imgUrlPartBackDrop!=null) {
            String imgUrl = "https://image.tmdb.org/t/p/w500" + imgUrlPartBackDrop;
            Picasso.with(this).load(imgUrl).into(mBackDropImageView);
        }
    }

    public void loadingMoviePosterImage(String imgUrlPartPoster){
        if(imgUrlPartPoster!=null) {
            String imgUrl = "https://image.tmdb.org/t/p/w500" + imgUrlPartPoster;
            Picasso.with(this).load(imgUrl).into(mPosterImageView);
        }
    }

}
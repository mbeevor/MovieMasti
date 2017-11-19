package com.example.android.moviemasti.fragmentsmanipulation;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.moviemasti.R;
import com.example.android.moviemasti.activities.MovieDetailedDataScrollingActivity;
import com.example.android.moviemasti.adapters.FavouriteAdapter;
import com.example.android.moviemasti.database.MovieDataBaseContract;
import com.example.android.moviemasti.pojo.MovieData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by soumyajit on 17/11/17.
 */

public class FavouriteMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
        , FavouriteAdapter.OnClickFavouriteItemHandler {

    public static final int FAV_MOVIE_LOADER = 1216;
    private static final String MOVIE_TAG = "sorting_tag";
    private static final String TAG_RATE = "rate";
    private static final String TAG_POPULARITY = "popularity";
    private final static String FAVOURITE_VALUE_KEY = "movieIntentData";
    @BindView(R.id.fav_framelayout)
    FrameLayout frameLayout;
    @BindView(R.id.favourite_movie_data_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.favourite_action_error)
    TextView mErrorTextView;
    @BindView(R.id.favourite_progress_bar)
    ProgressBar mProgressbar;
    private Cursor dataCursor = null;
    private FavouriteAdapter favouriteAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(FAV_MOVIE_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favourite_fragment, container, false);
        ButterKnife.bind(this, view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        favouriteAdapter = new FavouriteAdapter(getActivity().getApplicationContext(), this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(favouriteAdapter);
        getLoaderManager().initLoader(FAV_MOVIE_LOADER, null, this);
        loadMovieData(TAG_RATE);
        return view;
    }

    public void loadMovieData(String TAG) {
        Bundle movieBundle = new Bundle();
        movieBundle.putString(MOVIE_TAG, TAG);
        LoaderManager loaderManager = getLoaderManager();
        Loader<Cursor> loader = loaderManager.getLoader(FAV_MOVIE_LOADER);
        if (loader == null) {
            loaderManager.initLoader(FAV_MOVIE_LOADER, movieBundle, this);
        } else {
            loaderManager.restartLoader(FAV_MOVIE_LOADER, movieBundle, this);
        }

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity().getApplicationContext()) {
            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                mProgressbar.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mErrorTextView.setVisibility(View.INVISIBLE);
                if (mCursor != null)
                    deliverResult(mCursor);
                else
                    forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    String sortingOrder = MovieDataBaseContract.MovieEntry.COLUMN_MOVIE_RATING + " DESC ";
                    if (args != null && args.getString(MOVIE_TAG).equals(TAG_POPULARITY)) {
                        sortingOrder = MovieDataBaseContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " DESC ";
                    }
                    return getContext().getContentResolver().query(MovieDataBaseContract.MovieEntry.CONTENT_URI,
                            null, null, null,
                            sortingOrder);
                } catch (Exception e) {
                    Log.i("favourite", "Not successfully loaded the data", e);
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                super.deliverResult(data);
                mCursor = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProgressbar.setVisibility(View.INVISIBLE);
        if (data != null && data.getCount() != 0) {
            if (loader.getId() == FAV_MOVIE_LOADER)
                favouriteAdapter.swapCursor(data);
            dataCursor = data;
            Log.i("Favourite", "Count " + dataCursor.getCount());

        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onFavoutiteItemClick(Cursor cursor) {
        int movieIdIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_ID);
        int movieTitleIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_TITLE);
        int movieRatingIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_RATING);
        int moviePosterPathIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_POSTER_PATH);
        int movieBackDropIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_BACKDROP_PATH);
        int movieDescriptionIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_DESCRIPTION);
        int movieReleaseDateIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        int moviePopularityIndex = cursor.getColumnIndex(MovieDataBaseContract
                .MovieEntry.COLUMN_MOVIE_POPULARITY);

        Long movieId = cursor.getLong(movieIdIndex);
        double popularity = cursor.getDouble(moviePopularityIndex);
        String movieTitle = cursor.getString(movieTitleIndex);
        double movieRating = cursor.getDouble(movieRatingIndex);
        String movieBackDropPath = cursor.getString(movieBackDropIndex);
        String movieDescription = cursor.getString(movieDescriptionIndex);
        String movieReleaseDate = cursor.getString(movieReleaseDateIndex);
        String moviePosterPath = cursor.getString(moviePosterPathIndex);

        MovieData movieData = new MovieData(movieId, movieRating, movieTitle, moviePosterPath, popularity
                , movieBackDropPath, movieDescription, movieReleaseDate);
        Bundle bundle = new Bundle();
        bundle.putParcelable(FAVOURITE_VALUE_KEY, movieData);
        Intent intent = new Intent(getActivity(), MovieDetailedDataScrollingActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sorting_movies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();
        switch (menuItemId) {
            case R.id.menu_sorting_popularity:
                if (dataCursor != null && dataCursor.getCount() > 1) {
                    loadMovieData(TAG_POPULARITY);
                } else {
                    Snackbar.make(frameLayout, "Sorting is not possible", Snackbar.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_sorting_rating:
                if (dataCursor != null && dataCursor.getCount() > 1) {
                    loadMovieData(TAG_RATE);
                } else {
                    Snackbar.make(frameLayout, "Sorting is not possible", Snackbar.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}

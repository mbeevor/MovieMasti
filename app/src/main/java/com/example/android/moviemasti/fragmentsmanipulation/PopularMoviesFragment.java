package com.example.android.moviemasti.fragmentsmanipulation;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.android.moviemasti.R;
import com.example.android.moviemasti.activities.MovieDetailedDataScrollingActivity;
import com.example.android.moviemasti.adapters.MovieAdapter;
import com.example.android.moviemasti.datamanipulation.JsonDataParsing;
import com.example.android.moviemasti.datamanipulation.Networking;
import com.example.android.moviemasti.menusmanipulation.SortingMovieData;
import com.example.android.moviemasti.pojo.MovieData;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by soumyajit on 14/11/17.
 */

public class PopularMoviesFragment extends Fragment implements MovieAdapter.MovieOnClickItemHandler,
        LoaderManager.LoaderCallbacks<ArrayList<MovieData>> {

    private static final String MOVIE_URL = "url";
    private static final int MOVIE_POPULARITY_LOADER = 2400;
    private static final String SAVED_ARRAYLIST = "saved_array_list";
    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";
    public static ArrayList<MovieData> arrayPopularList = null;
    public final String API_KEY = "532dfe3fbb248c4ecc6f42703334d18e";
    private final String POPULARITY_URL =
            "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    public RecyclerView.LayoutManager gridLayoutManager;
    @BindView(R.id.popular_movie_data_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.action_error)
    LinearLayout mErrorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.reload_button)
    Button mReloadButton;
    @BindView(R.id.main_framelayout)
    FrameLayout frameLayout;
    private Parcelable onSavedInstanceState = null;
    private MovieAdapter movieAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            arrayPopularList = savedInstanceState.getParcelableArrayList(SAVED_ARRAYLIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View popularMoviesView = inflater.inflate(R.layout.activity_popular_movies, container, false);
        ButterKnife.bind(this, popularMoviesView);
        gridLayoutManager = new GridLayoutManager(popularMoviesView.getContext(), numberOfColumns());
        mRecyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(getActivity().getApplicationContext(), this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(movieAdapter);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMovieData(POPULARITY_URL);
            }
        });
        loadMovieData(POPULARITY_URL);
        if (savedInstanceState != null) {
            onSavedInstanceState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        }
        return popularMoviesView;
    }

    public void loadMovieData(String movieApiUrl) {
        Bundle movieBundle = new Bundle();
        movieBundle.putString(MOVIE_URL, movieApiUrl);
        LoaderManager loaderManager = getLoaderManager();
        Loader<ArraySet<MovieData>> loader = loaderManager.getLoader(MOVIE_POPULARITY_LOADER);
        if (loader == null) {
            loaderManager.initLoader(MOVIE_POPULARITY_LOADER, movieBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_POPULARITY_LOADER, movieBundle, this);
        }

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<MovieData>>(getActivity().getApplicationContext()) {

            @Override
            protected void onStartLoading() {
                mProgressBar.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mErrorLayout.setVisibility(View.INVISIBLE);
                if (args == null) {
                    return;
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<MovieData> loadInBackground() {
                try {
                    ArrayList<MovieData> movieDataArrayList = null;
                    String apiUrl = args.getString(MOVIE_URL);
                    String jsonMovieResult = Networking.getJSONResponseFromUrl(apiUrl);
                    if (jsonMovieResult != null) {
                        movieDataArrayList = JsonDataParsing.getDataForPopularity(jsonMovieResult);
                    }
                    return movieDataArrayList;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }


        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (data != null && data.size() != 0) {
            mErrorLayout.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            if (arrayPopularList == null) {
                movieAdapter.setpopularMoviesData(data);
                arrayPopularList = data;
            } else {
                movieAdapter.setpopularMoviesData(arrayPopularList);
            }
            if (onSavedInstanceState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(onSavedInstanceState);
            }
        } else {
            mErrorLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieData>> loader) {

    }
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClickItem(MovieData imageData) {
        Intent movieIntent = new Intent(getActivity().getApplicationContext(), MovieDetailedDataScrollingActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("movieIntentData", imageData);
        movieIntent.putExtras(mBundle);
        startActivity(movieIntent);
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
                if (arrayPopularList != null) {
                    arrayPopularList = SortingMovieData.sortAccordingToPopularity(arrayPopularList);
                    movieAdapter.setpopularMoviesData(arrayPopularList);
                } else {
                    Snackbar.make(frameLayout, "No data to sort", Snackbar.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_sorting_rating:
                if (arrayPopularList != null) {
                    arrayPopularList = SortingMovieData.sortAccordingToRating(arrayPopularList);
                    movieAdapter.setpopularMoviesData(arrayPopularList);
                } else {
                    Snackbar.make(frameLayout, "No data to sort", Snackbar.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mRecyclerView.getLayoutManager()
                .onSaveInstanceState());
        outState.putParcelableArrayList(SAVED_ARRAYLIST, arrayPopularList);
    }


}

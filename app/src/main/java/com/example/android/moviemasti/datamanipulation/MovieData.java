package com.example.android.moviemasti.datamanipulation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by soumyajit on 26/9/17.
 */

public class MovieData implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
    public static Comparator<MovieData> popularity = new Comparator<MovieData>() {
        @Override
        public int compare(MovieData s1, MovieData s2) {
            double popularity1 = s1.getMoviePopularity();
            double popularity2 = s2.getMoviePopularity();

            //descending order

            if (popularity2 > popularity1)
                return 1;
            else
                return -1;
        }
    };
    public static Comparator<MovieData> rating = new Comparator<MovieData>() {
        @Override
        public int compare(MovieData o1, MovieData o2) {
            double rating1 = o1.getMovieVotes();
            double rating2 = o2.getMovieVotes();
            if (rating2 > rating1)
                return 1;
            else
                return -1;
        }
    };
    private long movieId;
    private double movieVoteAverage;
    private String movieTitle;
    private String moviePosterPath;
    private double moviePopularity;
    private String movieBackdropPath;
    private String movieDescription;
    private String movieReleaseDate;

    public MovieData(long id, double vote, String title, String poster, double popularity, String backDrop, String description, String releaseDate) {
        movieId = id;
        movieVoteAverage = vote;
        movieTitle = title;
        moviePosterPath = poster;
        moviePopularity = popularity;
        movieBackdropPath = backDrop;
        movieDescription = description;
        movieReleaseDate = releaseDate;
    }

    // Parcelling part
    public MovieData(Parcel in) {
        movieId = in.readLong();
        movieVoteAverage = in.readDouble();
        movieTitle = in.readString();
        moviePosterPath = in.readString();
        moviePopularity = in.readDouble();
        movieBackdropPath = in.readString();
        movieDescription = in.readString();
        movieReleaseDate = in.readString();
    }

    public long getMovieId() {
        return movieId;
    }

    public double getMovieVotes() {
        return movieVoteAverage;
    }

    public double getMoviePopularity() {
        return moviePopularity;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public String getMovieBackdropPath() {
        return movieBackdropPath;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeDouble(movieVoteAverage);
        dest.writeString(movieTitle);
        dest.writeString(moviePosterPath);
        dest.writeDouble(moviePopularity);
        dest.writeString(movieBackdropPath);
        dest.writeString(movieDescription);
        dest.writeString(movieReleaseDate);

    }

}

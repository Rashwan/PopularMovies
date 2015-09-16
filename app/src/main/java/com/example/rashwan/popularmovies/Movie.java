package com.example.rashwan.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by rashwan on 9/11/15.
 */
public class Movie implements Parcelable {
    private String id;
    private String title;
    private String releaseDate;
    private String voteAverage;
    private String plot;
    private Uri posterUri;
    private String posterPath;
    private Uri homeUri;
    private String homePath;
    private Uri blurPosterUri;
    private String blurPosterPath;
    private List<Trailer> trailers;
    private List<Review> reviews;

    private static final String HOME_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String BLUR_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";


    public Movie(String id, String title, String releaseDate, String voteAverage, String plot, String homePath ,String posterPath, String blurPosterPath) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.plot = plot;
        this.homePath = homePath;
        this.posterPath = posterPath;
        this.blurPosterPath =  blurPosterPath;
        this.homeUri = Uri.parse(HOME_BASE_URL).buildUpon().appendEncodedPath(homePath).build();
        this.posterUri = Uri.parse(POSTER_BASE_URL).buildUpon().appendEncodedPath(posterPath).build();
        this.blurPosterUri = Uri.parse(BLUR_POSTER_BASE_URL).buildUpon().appendEncodedPath(blurPosterPath).build();
    }

    public Movie(String id, String title, String homePath) {
        this.id = id;
        this.title = title;
        this.homeUri = Uri.parse(HOME_BASE_URL).buildUpon().appendEncodedPath(homePath).build();
    }

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
        plot = in.readString();
        posterUri = in.readParcelable(Uri.class.getClassLoader());
        posterPath = in.readString();
        homeUri = in.readParcelable(Uri.class.getClassLoader());
        homePath = in.readString();
        blurPosterUri = in.readParcelable(Uri.class.getClassLoader());
        blurPosterPath = in.readString();
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getPlot() {
        return plot;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getHomePath() {
        return homePath;
    }

    public String getBlurPosterPath() {
        return blurPosterPath;
    }

    public Uri getPosterUri() {
        return posterUri;
    }

    public Uri getHomeUri() {
        return homeUri;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Uri getBlurPosterUri() {
        return blurPosterUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
        dest.writeString(plot);
        dest.writeParcelable(posterUri, flags);
        dest.writeString(posterPath);
        dest.writeParcelable(homeUri, flags);
        dest.writeString(homePath);
        dest.writeParcelable(blurPosterUri, flags);
        dest.writeString(blurPosterPath);
    }
}

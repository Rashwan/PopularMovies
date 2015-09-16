package com.example.rashwan.popularmovies.provider.movie;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.rashwan.popularmovies.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code movie} table.
 */
public class MovieCursor extends AbstractCursor implements MovieModel {
    public MovieCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(MovieColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The movie ID in the MovieDB
     * Cannot be {@code null}.
     */
    @NonNull
    public String getMovieId() {
        String res = getStringOrNull(MovieColumns.MOVIE_ID);
        if (res == null)
            throw new NullPointerException("The value of 'movie_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The movie title
     * Can be {@code null}.
     */
    @Nullable
    public String getTitle() {
        String res = getStringOrNull(MovieColumns.TITLE);
        return res;
    }

    /**
     * Get the {@code release_date} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getReleaseDate() {
        String res = getStringOrNull(MovieColumns.RELEASE_DATE);
        return res;
    }

    /**
     * Get the {@code vote_average} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getVoteAverage() {
        String res = getStringOrNull(MovieColumns.VOTE_AVERAGE);
        return res;
    }

    /**
     * Get the {@code plot} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getPlot() {
        String res = getStringOrNull(MovieColumns.PLOT);
        return res;
    }

    /**
     * Get the {@code poster_uri} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getPosterUri() {
        String res = getStringOrNull(MovieColumns.POSTER_URI);
        return res;
    }

    /**
     * Get the {@code home_uri} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getHomeUri() {
        String res = getStringOrNull(MovieColumns.HOME_URI);
        return res;
    }

    /**
     * Get the {@code blur_poster_uri} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getBlurPosterUri() {
        String res = getStringOrNull(MovieColumns.BLUR_POSTER_URI);
        return res;
    }
}

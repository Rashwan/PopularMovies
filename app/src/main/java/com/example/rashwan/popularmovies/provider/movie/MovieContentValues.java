package com.example.rashwan.popularmovies.provider.movie;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.rashwan.popularmovies.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code movie} table.
 */
public class MovieContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return MovieColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable MovieSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable MovieSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The movie ID in the MovieDB
     */
    public MovieContentValues putMovieId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("movieId must not be null");
        mContentValues.put(MovieColumns.MOVIE_ID, value);
        return this;
    }


    /**
     * The movie title
     */
    public MovieContentValues putTitle(@Nullable String value) {
        mContentValues.put(MovieColumns.TITLE, value);
        return this;
    }

    public MovieContentValues putTitleNull() {
        mContentValues.putNull(MovieColumns.TITLE);
        return this;
    }

    public MovieContentValues putReleaseDate(@Nullable String value) {
        mContentValues.put(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieContentValues putReleaseDateNull() {
        mContentValues.putNull(MovieColumns.RELEASE_DATE);
        return this;
    }

    public MovieContentValues putVoteAverage(@Nullable String value) {
        mContentValues.put(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieContentValues putVoteAverageNull() {
        mContentValues.putNull(MovieColumns.VOTE_AVERAGE);
        return this;
    }

    public MovieContentValues putPlot(@Nullable String value) {
        mContentValues.put(MovieColumns.PLOT, value);
        return this;
    }

    public MovieContentValues putPlotNull() {
        mContentValues.putNull(MovieColumns.PLOT);
        return this;
    }

    public MovieContentValues putPosterUri(@Nullable String value) {
        mContentValues.put(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieContentValues putPosterUriNull() {
        mContentValues.putNull(MovieColumns.POSTER_URI);
        return this;
    }

    public MovieContentValues putHomeUri(@Nullable String value) {
        mContentValues.put(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieContentValues putHomeUriNull() {
        mContentValues.putNull(MovieColumns.HOME_URI);
        return this;
    }

    public MovieContentValues putBlurPosterUri(@Nullable String value) {
        mContentValues.put(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieContentValues putBlurPosterUriNull() {
        mContentValues.putNull(MovieColumns.BLUR_POSTER_URI);
        return this;
    }
}

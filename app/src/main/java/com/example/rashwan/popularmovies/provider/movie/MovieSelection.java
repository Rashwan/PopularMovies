package com.example.rashwan.popularmovies.provider.movie;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.example.rashwan.popularmovies.provider.base.AbstractSelection;

/**
 * Selection for the {@code movie} table.
 */
public class MovieSelection extends AbstractSelection<MovieSelection> {
    @Override
    protected Uri baseUri() {
        return MovieColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code MovieCursor} object, which is positioned before the first entry, or null.
     */
    public MovieCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new MovieCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public MovieCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code MovieCursor} object, which is positioned before the first entry, or null.
     */
    public MovieCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new MovieCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public MovieCursor query(Context context) {
        return query(context, null);
    }


    public MovieSelection id(long... value) {
        addEquals("movie." + MovieColumns._ID, toObjectArray(value));
        return this;
    }

    public MovieSelection idNot(long... value) {
        addNotEquals("movie." + MovieColumns._ID, toObjectArray(value));
        return this;
    }

    public MovieSelection orderById(boolean desc) {
        orderBy("movie." + MovieColumns._ID, desc);
        return this;
    }

    public MovieSelection orderById() {
        return orderById(false);
    }

    public MovieSelection movieId(String... value) {
        addEquals(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieSelection movieIdNot(String... value) {
        addNotEquals(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieSelection movieIdLike(String... value) {
        addLike(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieSelection movieIdContains(String... value) {
        addContains(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieSelection movieIdStartsWith(String... value) {
        addStartsWith(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieSelection movieIdEndsWith(String... value) {
        addEndsWith(MovieColumns.MOVIE_ID, value);
        return this;
    }

    public MovieSelection orderByMovieId(boolean desc) {
        orderBy(MovieColumns.MOVIE_ID, desc);
        return this;
    }

    public MovieSelection orderByMovieId() {
        orderBy(MovieColumns.MOVIE_ID, false);
        return this;
    }

    public MovieSelection title(String... value) {
        addEquals(MovieColumns.TITLE, value);
        return this;
    }

    public MovieSelection titleNot(String... value) {
        addNotEquals(MovieColumns.TITLE, value);
        return this;
    }

    public MovieSelection titleLike(String... value) {
        addLike(MovieColumns.TITLE, value);
        return this;
    }

    public MovieSelection titleContains(String... value) {
        addContains(MovieColumns.TITLE, value);
        return this;
    }

    public MovieSelection titleStartsWith(String... value) {
        addStartsWith(MovieColumns.TITLE, value);
        return this;
    }

    public MovieSelection titleEndsWith(String... value) {
        addEndsWith(MovieColumns.TITLE, value);
        return this;
    }

    public MovieSelection orderByTitle(boolean desc) {
        orderBy(MovieColumns.TITLE, desc);
        return this;
    }

    public MovieSelection orderByTitle() {
        orderBy(MovieColumns.TITLE, false);
        return this;
    }

    public MovieSelection releaseDate(String... value) {
        addEquals(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieSelection releaseDateNot(String... value) {
        addNotEquals(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieSelection releaseDateLike(String... value) {
        addLike(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieSelection releaseDateContains(String... value) {
        addContains(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieSelection releaseDateStartsWith(String... value) {
        addStartsWith(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieSelection releaseDateEndsWith(String... value) {
        addEndsWith(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieSelection orderByReleaseDate(boolean desc) {
        orderBy(MovieColumns.RELEASE_DATE, desc);
        return this;
    }

    public MovieSelection orderByReleaseDate() {
        orderBy(MovieColumns.RELEASE_DATE, false);
        return this;
    }

    public MovieSelection voteAverage(String... value) {
        addEquals(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieSelection voteAverageNot(String... value) {
        addNotEquals(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieSelection voteAverageLike(String... value) {
        addLike(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieSelection voteAverageContains(String... value) {
        addContains(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieSelection voteAverageStartsWith(String... value) {
        addStartsWith(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieSelection voteAverageEndsWith(String... value) {
        addEndsWith(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieSelection orderByVoteAverage(boolean desc) {
        orderBy(MovieColumns.VOTE_AVERAGE, desc);
        return this;
    }

    public MovieSelection orderByVoteAverage() {
        orderBy(MovieColumns.VOTE_AVERAGE, false);
        return this;
    }

    public MovieSelection plot(String... value) {
        addEquals(MovieColumns.PLOT, value);
        return this;
    }

    public MovieSelection plotNot(String... value) {
        addNotEquals(MovieColumns.PLOT, value);
        return this;
    }

    public MovieSelection plotLike(String... value) {
        addLike(MovieColumns.PLOT, value);
        return this;
    }

    public MovieSelection plotContains(String... value) {
        addContains(MovieColumns.PLOT, value);
        return this;
    }

    public MovieSelection plotStartsWith(String... value) {
        addStartsWith(MovieColumns.PLOT, value);
        return this;
    }

    public MovieSelection plotEndsWith(String... value) {
        addEndsWith(MovieColumns.PLOT, value);
        return this;
    }

    public MovieSelection orderByPlot(boolean desc) {
        orderBy(MovieColumns.PLOT, desc);
        return this;
    }

    public MovieSelection orderByPlot() {
        orderBy(MovieColumns.PLOT, false);
        return this;
    }

    public MovieSelection posterUri(String... value) {
        addEquals(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieSelection posterUriNot(String... value) {
        addNotEquals(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieSelection posterUriLike(String... value) {
        addLike(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieSelection posterUriContains(String... value) {
        addContains(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieSelection posterUriStartsWith(String... value) {
        addStartsWith(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieSelection posterUriEndsWith(String... value) {
        addEndsWith(MovieColumns.POSTER_URI, value);
        return this;
    }

    public MovieSelection orderByPosterUri(boolean desc) {
        orderBy(MovieColumns.POSTER_URI, desc);
        return this;
    }

    public MovieSelection orderByPosterUri() {
        orderBy(MovieColumns.POSTER_URI, false);
        return this;
    }

    public MovieSelection homeUri(String... value) {
        addEquals(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieSelection homeUriNot(String... value) {
        addNotEquals(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieSelection homeUriLike(String... value) {
        addLike(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieSelection homeUriContains(String... value) {
        addContains(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieSelection homeUriStartsWith(String... value) {
        addStartsWith(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieSelection homeUriEndsWith(String... value) {
        addEndsWith(MovieColumns.HOME_URI, value);
        return this;
    }

    public MovieSelection orderByHomeUri(boolean desc) {
        orderBy(MovieColumns.HOME_URI, desc);
        return this;
    }

    public MovieSelection orderByHomeUri() {
        orderBy(MovieColumns.HOME_URI, false);
        return this;
    }

    public MovieSelection blurPosterUri(String... value) {
        addEquals(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieSelection blurPosterUriNot(String... value) {
        addNotEquals(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieSelection blurPosterUriLike(String... value) {
        addLike(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieSelection blurPosterUriContains(String... value) {
        addContains(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieSelection blurPosterUriStartsWith(String... value) {
        addStartsWith(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieSelection blurPosterUriEndsWith(String... value) {
        addEndsWith(MovieColumns.BLUR_POSTER_URI, value);
        return this;
    }

    public MovieSelection orderByBlurPosterUri(boolean desc) {
        orderBy(MovieColumns.BLUR_POSTER_URI, desc);
        return this;
    }

    public MovieSelection orderByBlurPosterUri() {
        orderBy(MovieColumns.BLUR_POSTER_URI, false);
        return this;
    }
}

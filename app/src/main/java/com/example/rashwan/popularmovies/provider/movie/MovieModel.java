package com.example.rashwan.popularmovies.provider.movie;

import com.example.rashwan.popularmovies.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Data model for the {@code movie} table.
 */
public interface MovieModel extends BaseModel {

    /**
     * The movie ID in the MovieDB
     * Cannot be {@code null}.
     */
    @NonNull
    String getMovieId();

    /**
     * The movie title
     * Can be {@code null}.
     */
    @Nullable
    String getTitle();

    /**
     * Get the {@code release_date} value.
     * Can be {@code null}.
     */
    @Nullable
    String getReleaseDate();

    /**
     * Get the {@code vote_average} value.
     * Can be {@code null}.
     */
    @Nullable
    String getVoteAverage();

    /**
     * Get the {@code plot} value.
     * Can be {@code null}.
     */
    @Nullable
    String getPlot();

    /**
     * Get the {@code poster_uri} value.
     * Can be {@code null}.
     */
    @Nullable
    String getPosterUri();

    /**
     * Get the {@code home_uri} value.
     * Can be {@code null}.
     */
    @Nullable
    String getHomeUri();

    /**
     * Get the {@code blur_poster_uri} value.
     * Can be {@code null}.
     */
    @Nullable
    String getBlurPosterUri();
}

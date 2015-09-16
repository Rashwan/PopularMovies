package com.example.rashwan.popularmovies.provider.movie;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.rashwan.popularmovies.provider.MoviesProvider;
import com.example.rashwan.popularmovies.provider.movie.MovieColumns;

/**
 * Columns for the {@code movie} table.
 */
public class MovieColumns implements BaseColumns {
    public static final String TABLE_NAME = "movie";
    public static final Uri CONTENT_URI = Uri.parse(MoviesProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The movie ID in the MovieDB
     */
    public static final String MOVIE_ID = "movie_id";

    /**
     * The movie title
     */
    public static final String TITLE = "title";

    public static final String RELEASE_DATE = "release_date";

    public static final String VOTE_AVERAGE = "vote_average";

    public static final String PLOT = "plot";

    public static final String POSTER_URI = "poster_uri";

    public static final String HOME_URI = "home_uri";

    public static final String BLUR_POSTER_URI = "blur_poster_uri";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            MOVIE_ID,
            TITLE,
            RELEASE_DATE,
            VOTE_AVERAGE,
            PLOT,
            POSTER_URI,
            HOME_URI,
            BLUR_POSTER_URI
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(MOVIE_ID) || c.contains("." + MOVIE_ID)) return true;
            if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
            if (c.equals(RELEASE_DATE) || c.contains("." + RELEASE_DATE)) return true;
            if (c.equals(VOTE_AVERAGE) || c.contains("." + VOTE_AVERAGE)) return true;
            if (c.equals(PLOT) || c.contains("." + PLOT)) return true;
            if (c.equals(POSTER_URI) || c.contains("." + POSTER_URI)) return true;
            if (c.equals(HOME_URI) || c.contains("." + HOME_URI)) return true;
            if (c.equals(BLUR_POSTER_URI) || c.contains("." + BLUR_POSTER_URI)) return true;
        }
        return false;
    }

}

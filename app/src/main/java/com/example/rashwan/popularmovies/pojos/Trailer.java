package com.example.rashwan.popularmovies.pojos;

import android.net.Uri;

/**
 * Created by rashwan on 9/11/15.
 */
public class Trailer {
    private Uri trailerImageUri;
    private Uri trailerUri;
    private String name;
    private static final String TRAILER_BASE_URL = "https://www.youtube.com/watch";
    private static final String TRAILER_IMAGE_BASE_URL = "http://img.youtube.com/vi/%s/default.jpg";

    public Trailer(String trailerKey, String name) {
        this.trailerImageUri = Uri.parse(String.format(TRAILER_IMAGE_BASE_URL,trailerKey));
        this.trailerUri = Uri.parse(TRAILER_BASE_URL).buildUpon().appendQueryParameter("v",trailerKey).build();
        this.name = name;
    }

    public Uri getTrailerImageUri() {
        return trailerImageUri;
    }

    public String getName() {
        return name;
    }

    public Uri getTrailerUri() {
        return trailerUri;
    }
}

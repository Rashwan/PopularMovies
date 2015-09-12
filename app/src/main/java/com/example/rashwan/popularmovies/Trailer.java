package com.example.rashwan.popularmovies;

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

    public Trailer(String trailerId, String name) {
        this.trailerImageUri = Uri.parse(String.format(TRAILER_IMAGE_BASE_URL,trailerId));
        this.trailerUri = Uri.parse(TRAILER_BASE_URL).buildUpon().appendQueryParameter("v",trailerId).build();
        this.name = name;
    }
}

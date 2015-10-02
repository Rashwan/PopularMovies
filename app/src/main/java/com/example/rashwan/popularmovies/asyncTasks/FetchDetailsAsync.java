package com.example.rashwan.popularmovies.asyncTasks;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.rashwan.popularmovies.R;
import com.example.rashwan.popularmovies.pojos.Review;
import com.example.rashwan.popularmovies.pojos.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rashwan on 10/1/15.
 */
public class FetchDetailsAsync extends AsyncTaskLoader<List<?>>{
    private Boolean isTrailer;
    private Context mContext;
    private String baseUrl;
    private String movieId;

    private static final String LOG_TAG = FetchDetailsAsync.class.getSimpleName();
    private List<?> mData;

    public FetchDetailsAsync(Context context,Bundle args) {
        super(context);

        baseUrl = args.getString(context.getString(R.string.bundle_base_url_key));
        movieId = args.getString(context.getString(R.string.bundle_movie_id_key));
        mContext = context;
    }

    @Override
    public List<?> loadInBackground() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String base_path = String.format(baseUrl, movieId);
            if (baseUrl.contains("videos")) {
                isTrailer = true;
            } else {
                isTrailer = false;
            }
            Uri uri = Uri.parse(base_path).buildUpon().appendQueryParameter(mContext.getString(R.string.api_key_query_param), mContext.getString(R.string.movie_db_api_key)).build();
            URL url = null;
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        String responseJsonString;
        try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseJsonString = buffer.toString();
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(LOG_TAG, "Error ", e1);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                if (isTrailer) {
                    //We Are Fetching Trailers
                    return getTrailersFromJson(responseJsonString);
                } else {
                    //We Are Fetching Reviews
                    return getReviewsFromJson(responseJsonString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }


    private List<Trailer> getTrailersFromJson(String trailersJsonString) throws JSONException {
        JSONObject object = new JSONObject(trailersJsonString);
        JSONArray trailersArray = object.getJSONArray(mContext.getString(R.string.json_result_key));
        List<Trailer> trailerList = new ArrayList<>();
        Trailer trailer;

        for (int i = 0; i < trailersArray.length(); i++) {
            JSONObject jsonTrailer = trailersArray.getJSONObject(i);
            trailer = new Trailer(jsonTrailer.getString(mContext.getString(R.string.json_trailer_key_key)),jsonTrailer.getString(mContext.getString(R.string.json_trailer_name_key)));
            trailerList.add(trailer);
        }
        return trailerList;

    }


    private List<Review> getReviewsFromJson(String reviewsJsonString) throws JSONException {
        JSONObject object = new JSONObject(reviewsJsonString);
        JSONArray  reviewsArray = object.getJSONArray(mContext.getString(R.string.json_result_key));
        List<Review> reviewsList = new ArrayList<>();
        Review review;

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject jsonReview = reviewsArray.getJSONObject(i);
            review = new Review(jsonReview.getString(mContext.getString(R.string.json_review_author_key)),jsonReview.getString(mContext.getString(R.string.json_review_content_key)));
            reviewsList.add(review);
        }
        return reviewsList;

    }

    public Boolean getIsTrailer() {
        return isTrailer;
    }

    @Override
    public void deliverResult(List<?> data) {
        mData = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mData != null){
            deliverResult(mData);
        }else {
            forceLoad();
        }

    }
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'mData'
        // if needed.
        if (mData != null) {
            mData = null;
        }
    }
}

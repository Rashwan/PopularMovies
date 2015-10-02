package com.example.rashwan.popularmovies.asyncTasks;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.rashwan.popularmovies.R;
import com.example.rashwan.popularmovies.pojos.Movie;

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
 * Created by rashwan on 9/30/15.
 */
public class FetchMoviesAsync extends android.content.AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = FetchMoviesAsync.class.getSimpleName();
    private JSONArray jsonResponse = new JSONArray();
    private String baseUrl;
    private String scrollPage;
    private List<Movie> mData;
    private Context mContext;

    public FetchMoviesAsync(Context context, Bundle args) {
        super(context);
        mContext = context;
        String stringResponse = args.getString(mContext.getString(R.string.bundle_json_response_key));
        try {
            this.jsonResponse = new JSONArray(stringResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        baseUrl = args.getString(mContext.getString(R.string.bundle_url_key));
        scrollPage = args.getString(mContext.getString(R.string.bundle_scroll_page_key));
    }

    public JSONArray getJsonResponse() {
        return jsonResponse;
    }

    @Override
    public List<Movie> loadInBackground() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter("page",scrollPage).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        String moviesJsonStr;
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
            moviesJsonStr = buffer.toString();
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
            JSONObject object = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = object.getJSONArray(mContext.getString(R.string.json_result_key));
            for (int i = 0; i < moviesArray.length(); i++) {
                jsonResponse.put(moviesArray.getJSONObject(i));
            }
            return getMoviesfromJson(moviesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Movie> getMoviesfromJson(JSONArray moviesArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        Movie movie;

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject jsonMovie = (JSONObject) moviesArray.get(i);

            if (!jsonMovie.getString(mContext.getString(R.string.json_movie_poster_key)).equals("null")) {
                movie = new Movie(jsonMovie.getString(mContext.getString(R.string.json_movie_id_key)), jsonMovie.getString(mContext.getString(R.string.json_movie_title_key)), jsonMovie.getString(mContext.getString(R.string.json_movie_poster_key)));

            }else{
                movie = new Movie(jsonMovie.getString(mContext.getString(R.string.json_movie_id_key)), jsonMovie.getString(mContext.getString(R.string.json_movie_title_key)), jsonMovie.getString(mContext.getString(R.string.json_movie_backdrop_key)));
            }
            movies.add(movie);
        }
        return movies;
    }

    @Override
    public void deliverResult(List<Movie> data) {

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

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null) {
            mData = null;
        }
    }
}

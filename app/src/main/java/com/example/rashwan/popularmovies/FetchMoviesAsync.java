package com.example.rashwan.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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
    private String[] params;
    private String moviesJsonStr;
    private JSONArray jsonResponse = new JSONArray();
    private List<Movie> mData;
    public FetchMoviesAsync(Context context, Bundle args) {
        super(context);
        Log.e(LOG_TAG,"CONSTRUCTOR");
        this.params = args.getStringArray("params");
        String stringResponse = args.getString("jsonResponse");
        try {
            this.jsonResponse = new JSONArray(stringResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getJsonResponse() {
        return jsonResponse;
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.e(LOG_TAG, "DOINBACKGROUND");
        Log.e(LOG_TAG,params[0] + " " + params[1]);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri uri = Uri.parse(params[0]).buildUpon().appendQueryParameter("page",params[1]).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


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
            JSONArray moviesArray = object.getJSONArray("results");
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
            if (!jsonMovie.getString("poster_path").equals("null")) {
                movie = new Movie(jsonMovie.getString("id"), jsonMovie.getString("original_title"), jsonMovie.getString("poster_path"));
            }else{
                movie = new Movie(jsonMovie.getString("id"), jsonMovie.getString("original_title"), jsonMovie.getString("backdrop_path"));
            }
            Log.e(LOG_TAG,movie.getTitle());
            movies.add(movie);
        }
        return movies;

    }

    @Override
    public void deliverResult(List<Movie> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }
        }
        List<Movie> oldData = mData;

        mData = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }
        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.e("ONSTARTLOADING", "HERE");
        super.onStartLoading();
        if (mData != null){
            Log.e("ONSTARTLOADING",mData.toString());
            deliverResult(mData);
        }else {
            forceLoad();
        }

    }
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }


    @Override public void onCanceled(List<Movie> data) {
        super.onCanceled(data);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
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
            onReleaseResources(mData);
            mData = null;
        }
    }
    protected void onReleaseResources(List<Movie> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}

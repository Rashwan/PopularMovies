package com.example.rashwan.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static final String POPULAR_MOVIES = "http://api.themoviedb.org/3/movie/popular?api_key=9c3654aee5aea28f21963eeebfd6f4a0";
    public static final String TOP_RATED_MOVIES = "http://api.themoviedb.org/3/movie/top_rated?page=1&api_key=9c3654aee5aea28f21963eeebfd6f4a0";

    private ImageAdapter adapter;
    public GridView gridView;
    SharedPreferences menu_sp ;
    SharedPreferences.Editor editor;
    String sort_pref;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        menu_sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        sort_pref = menu_sp.getString("sort_mode",(getString(R.string.sort_popular)));

        editor = menu_sp.edit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        if (sort_pref.equals(getString(R.string.sort_popular))){
            menu.getItem(0).setTitle(R.string.sort_top_rated);
        }else{
            menu.getItem(0).setTitle(R.string.sort_popular);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_settings :
                return true;

            case R.id.action_sort :
                if (item.getTitle().equals(getString(R.string.sort_popular))){
                    new FetchMovies().execute(POPULAR_MOVIES);
                    editor.putString("sort_mode", getString(R.string.sort_popular));
                    item.setTitle(R.string.sort_top_rated);

                }else{
                    new FetchMovies().execute(TOP_RATED_MOVIES);
                    editor.putString("sort_mode", getString(R.string.sort_top_rated));
                    item.setTitle(R.string.sort_popular);
                }

                editor.commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);

        adapter = new ImageAdapter(getActivity(),new JSONArray());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(getActivity(),MovieDetalisActivity.class);

                detailsIntent.putExtra("movieDetails",adapter.getItem(position).toString());

                startActivity(detailsIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Start",sort_pref);
        if (sort_pref.equals(getString(R.string.sort_popular))){
            Log.e("Start","popular");
            new FetchMovies().execute(POPULAR_MOVIES);
        }else{
            new FetchMovies().execute(TOP_RATED_MOVIES);
            Log.e("Start", "RAted");
        }

    }

    public class FetchMovies extends AsyncTask<String, Void, JSONArray> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();
        private String moviesJsonStr;


        private JSONArray getMoviesfromJson(String moviesJson) throws JSONException {
            JSONObject object = new JSONObject(moviesJson);
            JSONArray movies = object.getJSONArray("results");
            return movies;

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Log.e(LOG_TAG,"DO in BG");
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
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
                return getMoviesfromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null) {
                adapter = new ImageAdapter(getActivity(),result);
                gridView.setAdapter(adapter);

            }

        }
    }
}


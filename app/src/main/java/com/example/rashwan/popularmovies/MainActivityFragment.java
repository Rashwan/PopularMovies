package com.example.rashwan.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
    private String popularMoviesURL;
    private String topRatedMoviesURL;
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
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), (getString(R.string.sort_popular)));
        editor = menu_sp.edit();

        //Build popular movies query URL
        Uri popularURI = Uri.parse(getString(R.string.movies_base_url)).buildUpon()
                .appendPath(getString(R.string.popular_path))
                .appendQueryParameter(getString(R.string.api_key_query_param),getString(R.string.movie_db_api_key))
                .build();
        popularMoviesURL = popularURI.toString();

        //Build top rated movies query URL
        Uri topRatedURI = Uri.parse(getString(R.string.movies_base_url)).buildUpon()
                .appendPath(getString(R.string.top_rated_path))
                .appendQueryParameter(getString(R.string.api_key_query_param), getString(R.string.movie_db_api_key))
                .build();
        topRatedMoviesURL = topRatedURI.toString();
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
                    adapter = new ImageAdapter(getActivity(),new JSONArray());
                    new FetchMovies().execute(popularMoviesURL,"1");
                    editor.putString(getString(R.string.sort_mode_key), getString(R.string.sort_popular));
                    item.setTitle(R.string.sort_top_rated);

                }else{
                    adapter = new ImageAdapter(getActivity(),new JSONArray());
                    new FetchMovies().execute(topRatedMoviesURL,"1");
                    editor.putString(getString(R.string.sort_mode_key), getString(R.string.sort_top_rated));
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

                detailsIntent.putExtra(getString(R.string.movie_details_extra_key),adapter.getItem(position).toString());

                startActivity(detailsIntent);
            }
        });
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (sort_pref.equals(getString(R.string.sort_popular))){
                    new FetchMovies().execute(popularMoviesURL,Integer.valueOf(page+1).toString());
                }else{
                    new FetchMovies().execute(topRatedMoviesURL,Integer.valueOf(page+1).toString());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (sort_pref.equals(getString(R.string.sort_popular))){
            new FetchMovies().execute(popularMoviesURL,"1");
        }else{
            new FetchMovies().execute(topRatedMoviesURL,"1");
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
                return getMoviesfromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null) {
                Log.e(LOG_TAG, String.valueOf(adapter.getCount()));
                if (adapter.isEmpty()){
                    adapter.add(result);
                    gridView.setAdapter(adapter);
                }else {
                    adapter.add(result);
                    adapter.notifyDataSetChanged();
                }


            }

        }
    }
}


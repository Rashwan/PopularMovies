package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.rashwan.popularmovies.provider.movie.MovieCursor;
import com.example.rashwan.popularmovies.provider.movie.MovieSelection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BrowseMoviesActivityFragment extends android.app.Fragment implements android.app.LoaderManager.LoaderCallbacks<List<Movie>> {
    private String popularMoviesURL;
    private String topRatedMoviesURL;
    private BrowseMoviesAdapter adapter;
    private GridView gridView;
    private LinearLayout offlineView;
    private SharedPreferences menu_sp ;
    private SharedPreferences.Editor editor;
    private String sort_pref;
    private JSONArray jsonResponse = new JSONArray();
    private int scrollPage = 1;
    private String modePopular;
    private String modeTopRated;
    private String modeFavorites;
    private OnItemSelectedListener listener;
    private static final int LOADER_ID = 1;
    Context mContext;
    private Bundle args = new Bundle();
    private String[] params;

    @Override
    public android.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        Log.e("ONCREATELOADER","QE");
        return new FetchMoviesAsync(mContext,args);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Movie>> loader, List<Movie> data) {
        Log.e("ONLOADFINISHED","ASD");
        FetchMoviesAsync f = (FetchMoviesAsync) loader;
        if (data != null) {
            if (adapter.isEmpty()){
                Log.e("ONLOADFINISHED", "ADAPTERISEMPTY");
                if (jsonResponse.length()==0){
                    adapter.add(data);
                    gridView.setAdapter(adapter);
                }else {
                    Log.e("JSONRESONSE","HASITEMS");
                    try {
                        adapter.add(f.getMoviesfromJson(jsonResponse));
                        gridView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else {
                adapter.add(data);
                adapter.notifyDataSetChanged();
            }

            Log.e("OURRESONSE",String.valueOf(f.getJsonResponse().length()));
            jsonResponse = f.getJsonResponse();
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Movie>> loader) {
        Log.e("ONLOADERRESET","RESET");
        adapter = new BrowseMoviesAdapter(getActivity(),new ArrayList<Movie>());
    }


    public interface OnItemSelectedListener {
        void onItemSelected(Movie movie,ImageView posterView);
    }


    public BrowseMoviesActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());
    }

    @Override
    public void onAttach(Activity activity) {
        Log.e("ONATTACH", "Hold");
        super.onAttach(activity);

        if (activity instanceof OnItemSelectedListener){
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ItemsListFragment.OnItemSelectedListener");
        }
        mContext = activity;
    }

    @Override
    public void onAttach(Context context) {
        Log.e("ONATTACH", "H");
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener){
            listener = (OnItemSelectedListener) context;
        } else {
        throw new ClassCastException(context.toString()
                + " must implement ItemsListFragment.OnItemSelectedListener");
        }
        mContext = context;
    }
    @Override
    public void onResume() {
        super.onResume();
        android.content.Loader l = getLoaderManager().getLoader(LOADER_ID);
        if (l==null){
            Log.e("NULL","NULL");
        }else {
            Log.e("FOUNDONE!","asd");
        }
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);

        args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());


        if(adapter.isEmpty()) {
            if (Utilities.checkConnectivity(getActivity())) {
                if (sort_pref.equals(modePopular)) {
                    params = new String[]{popularMoviesURL,String.valueOf(scrollPage)};
                    args.putStringArray(getString(R.string.bundle_params_key), params);
                    getLoaderManager().initLoader(LOADER_ID, args, this);
                } else if (sort_pref.equals(modeTopRated)) {
                    params = new String[]{topRatedMoviesURL,String.valueOf(scrollPage)};
                    args.putStringArray(getString(R.string.bundle_params_key), params);
                    getLoaderManager().initLoader(LOADER_ID, args, this);
                } else {
                    getFavorites();
                }
            } else {
                editor.putString(getString(R.string.sort_mode_key), modeFavorites);
                editor.commit();
                getFavorites();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        menu_sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), getString(R.string.sort_mode_popular));
        editor = menu_sp.edit();
        modePopular = getString(R.string.sort_mode_popular);
        modeTopRated = getString(R.string.sort_mode_top_rated);
        modeFavorites = getString(R.string.sort_mode_favorites);

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
        if (savedInstanceState != null){
            String stringResponse = savedInstanceState.getString(getString(R.string.bundle_json_response_key));
            try {
                jsonResponse = new JSONArray(stringResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
        Utilities.menuSortCheck(menu, sort_pref, getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        jsonResponse = new JSONArray();
        scrollPage = 1;
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
        offlineView.setVisibility(View.GONE);
        args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());
        switch (item.getItemId()){

            case R.id.action_settings :
                return true;

            case R.id.action_sort_popular :
                item.setChecked(true);
                adapter = new BrowseMoviesAdapter(getActivity(), new ArrayList<Movie>());
                editor.putString(getString(R.string.sort_mode_key), modePopular);

                if (Utilities.checkConnectivity(getActivity())){
                    if ((sort_pref.equals(modeTopRated)) || (sort_pref.equals(modeFavorites))) {
                        params = new String[]{popularMoviesURL,String.valueOf(scrollPage)};
                        args.putStringArray(getString(R.string.bundle_params_key), params);
                        getLoaderManager().restartLoader(LOADER_ID, args, this);
                    }
                }else{
                        gridView.setAdapter(adapter);
                        offlineView.setVisibility(View.VISIBLE);
                }
                editor.commit();
                return true;

            case R.id.action_sort_top_rated:
                item.setChecked(true);
                adapter = new BrowseMoviesAdapter(getActivity(), new ArrayList<Movie>());
                editor.putString(getString(R.string.sort_mode_key), modeTopRated);
                if (Utilities.checkConnectivity(getActivity())) {
                    if ((sort_pref.equals(modePopular)) || (sort_pref.equals(modeFavorites))) {
                        params = new String[]{topRatedMoviesURL,String.valueOf(scrollPage)};
                        args.putStringArray(getString(R.string.bundle_params_key), params);
                        getLoaderManager().restartLoader(LOADER_ID, args, this);
                    }
                }else{
                    gridView.setAdapter(adapter);
                    offlineView.setVisibility(View.VISIBLE);
                }
                editor.commit();
                return true;

            case R.id.action_favorite:
                item.setChecked(true);
                adapter = new BrowseMoviesAdapter(getActivity(), new ArrayList<Movie>());
                editor.putString(getString(R.string.sort_mode_key), modeFavorites);
                editor.commit();
                getFavorites();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);


        gridView = (GridView) rootView.findViewById(R.id.gridview);
        offlineView  = (LinearLayout) rootView.findViewById(R.id.offline_view);

        adapter = new BrowseMoviesAdapter(getActivity(),new ArrayList<Movie>());

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
                ImageView gridPoster = (ImageView) view.findViewById(R.id.movie_poster);
                if (Utilities.isLollipopandAbove()) {
                    gridPoster.setTransitionName("poster" + position);
                }
                Movie movie = null;
                if (!sort_pref.equals(modeFavorites)) {
                    try {
                        JSONObject movieJson = jsonResponse.getJSONObject(position);
                        if (movieJson!=null) {
                            movie = new Movie(movieJson.getString("id"), movieJson.getString("original_title")
                                    , movieJson.getString("release_date"), movieJson.getString("vote_average"), movieJson.getString("overview")
                                    , movieJson.getString("poster_path"), movieJson.getString("poster_path"), movieJson.getString("backdrop_path"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    movie = (Movie) adapter.getItem(position);
                }
                listener.onItemSelected(movie, gridPoster);
            }
        });
        final BrowseMoviesActivityFragment browseFragment = this;
        gridView.setOnScrollListener(new EndlessScrollListener(5, 1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
                scrollPage++;
                args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());
                if (sort_pref.equals(modePopular)) {
                    params = new String[]{popularMoviesURL, String.valueOf(scrollPage)};
                    args.putStringArray(getString(R.string.bundle_params_key), params);
                    getLoaderManager().restartLoader(LOADER_ID, args, browseFragment);

                } else if (sort_pref.equals(modeTopRated)) {
                    params = new String[]{topRatedMoviesURL, String.valueOf(scrollPage)};
                    args.putStringArray(getString(R.string.bundle_params_key), params);
                    getLoaderManager().restartLoader(LOADER_ID, args, browseFragment);
                }
            }
        });

        return rootView;
    }

    private void getFavorites(){
        MovieSelection where = new MovieSelection();
        MovieCursor cursor = where.query(getActivity());
        Movie movie;
        List<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {

            movie = new Movie(cursor.getMovieId(), cursor.getTitle(), cursor.getReleaseDate(), cursor.getVoteAverage(),
                    cursor.getPlot(), cursor.getHomeUri(), cursor.getPosterUri(), cursor.getBlurPosterUri());
            movieList.add(movie);
        }
        cursor.close();
        if (adapter.isEmpty()){
            adapter.add(movieList);
            gridView.setAdapter(adapter);
        }else {
            adapter.add(movieList);
            adapter.notifyDataSetChanged();
        }
    }
}


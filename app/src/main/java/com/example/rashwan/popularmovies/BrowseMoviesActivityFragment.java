package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.rashwan.popularmovies.adapters.BrowseMoviesAdapter;
import com.example.rashwan.popularmovies.asyncTasks.FetchMoviesAsync;
import com.example.rashwan.popularmovies.pojos.Movie;
import com.example.rashwan.popularmovies.utilities.EndlessScrollListener;
import com.example.rashwan.popularmovies.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BrowseMoviesActivityFragment extends android.app.Fragment implements android.app.LoaderManager.LoaderCallbacks<List<Movie>> , Utilities.FavoriteStateListener{
    private String popularMoviesURL;
    private String topRatedMoviesURL;
    private BrowseMoviesAdapter adapter;
    private GridView gridView;
    private LinearLayout offlineView;
    private LinearLayout noFavoritesView;
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
    private Context mContext;
    private Bundle args = new Bundle();

    @Override
    public android.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesAsync(mContext,args);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Movie>> loader, List<Movie> data) {
        FetchMoviesAsync f = (FetchMoviesAsync) loader;
        if (data != null) {
            if (adapter.isEmpty()){
                //If we want the a specific page (basically the first page because the adapter is empty
                if (jsonResponse.length()==0){
                    adapter.add(data);
                    gridView.setAdapter(adapter);
                //if we want more that one page so we get it from the json response (i.e :screen orientation)
                }else {
                    try {
                        adapter.add(f.getMoviesfromJson(jsonResponse));
                        gridView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            //Load more pages on scroll
            }else {
                adapter.add(data);
                adapter.notifyDataSetChanged();
            }
            jsonResponse = f.getJsonResponse();
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Movie>> loader) {
        adapter = new BrowseMoviesAdapter(getActivity(),new ArrayList<Movie>());
    }

    @Override
    public void favStateChanged() {
        List<Movie> movies = Utilities.getFavorites(getActivity());
        if (sort_pref.equals(getString(R.string.sort_mode_favorites))){
            Utilities.setFavoritesAdapter(gridView,adapter,movies);
        }
    }

    //Listener for selecting a movie from the gridView
    public interface OnItemSelectedListener {
        void onItemSelected(Movie movie,ImageView posterView);
    }


    public BrowseMoviesActivityFragment() {
    }

    //Save jsonResponse on configuration change
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());
    }

    //deprecated onAttach for devices pre Marshmallow 6.0 (Bug in Fragment class)
    public void onAttach(Activity activity) {
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
    public void onDetach() {
        super.onDetach();
        mContext =null;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get the sorting preference
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
        noFavoritesView.setVisibility(View.GONE);
        args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());

        if(adapter.isEmpty()) {
            //If the device is connected to the internet fetch movies based on the user preference
            if (Utilities.checkConnectivity(getActivity())) {

                if (sort_pref.equals(modePopular)) {
                    args.putString(getString(R.string.bundle_url_key),popularMoviesURL);
                    args.putString(getString(R.string.bundle_scroll_page_key), String.valueOf(scrollPage));
                    getLoaderManager().initLoader(LOADER_ID, args, this);

                } else if (sort_pref.equals(modeTopRated)) {
                    args.putString(getString(R.string.bundle_url_key),topRatedMoviesURL);
                    args.putString(getString(R.string.bundle_scroll_page_key), String.valueOf(scrollPage));
                    getLoaderManager().initLoader(LOADER_ID, args, this);

                } else {
                    List<Movie> moviesList = Utilities.getFavorites(getActivity());
                    Utilities.setFavoritesAdapter(gridView, adapter, moviesList);
                    if(adapter.isEmpty()){
                        noFavoritesView.setVisibility(View.VISIBLE);
                    }
                }
            //If the device is offline select "Favorite Movies" preference
            } else {
                editor.putString(getString(R.string.sort_mode_key), modeFavorites);
                editor.commit();
                List<Movie> moviesList = Utilities.getFavorites(getActivity());
                Utilities.setFavoritesAdapter(gridView,adapter,moviesList);
                if(adapter.isEmpty()){
                    noFavoritesView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //set the context of the favorite listener
        Utilities.setFavListener(this);

        menu_sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), getString(R.string.sort_mode_popular));
        editor = menu_sp.edit();
        modePopular = getString(R.string.sort_mode_popular);
        modeTopRated = getString(R.string.sort_mode_top_rated);
        modeFavorites = getString(R.string.sort_mode_favorites);

        //Build popular movies query URL
        Uri popularURI = Uri.parse(getString(R.string.movies_base_url)).buildUpon()
                .appendPath(getString(R.string.popular_path))
                .appendQueryParameter(getString(R.string.api_key_query_param), getString(R.string.movie_db_api_key))
                .build();
        popularMoviesURL = popularURI.toString();

        //Build top rated movies query URL
        Uri topRatedURI = Uri.parse(getString(R.string.movies_base_url)).buildUpon()
                .appendPath(getString(R.string.top_rated_path))
                .appendQueryParameter(getString(R.string.api_key_query_param), getString(R.string.movie_db_api_key))
                .build();
        topRatedMoviesURL = topRatedURI.toString();

        //Load Json response after configuration change
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
        sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);

        offlineView.setVisibility(View.GONE);
        noFavoritesView.setVisibility(View.GONE);

        switch (item.getItemId()){

            case R.id.action_sort_popular :
                item.setChecked(true);
                editor.putString(getString(R.string.sort_mode_key), modePopular);

                if (Utilities.checkConnectivity(mContext.getApplicationContext())){
                    //If the device is connected to the internet & didn't choose the same preference
                    if (!sort_pref.equals(modePopular)) {
                        jsonResponse = new JSONArray();
                        scrollPage = 1;
                        adapter = new BrowseMoviesAdapter(mContext, new ArrayList<Movie>());
                        args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());
                        args.putString(getString(R.string.bundle_url_key), popularMoviesURL);
                        args.putString(getString(R.string.bundle_scroll_page_key), String.valueOf(scrollPage));
                        getLoaderManager().restartLoader(LOADER_ID, args, this);
                    }
                //If the device isn't connected to the internet show offline view
                }else{
                    adapter = new BrowseMoviesAdapter(mContext, new ArrayList<Movie>());
                    gridView.setAdapter(adapter);
                    offlineView.setVisibility(View.VISIBLE);
                }
                editor.commit();
                return true;

            case R.id.action_sort_top_rated:
                item.setChecked(true);
                editor.putString(getString(R.string.sort_mode_key), modeTopRated);
                if (Utilities.checkConnectivity(mContext.getApplicationContext())) {
                    if (!sort_pref.equals(modeTopRated)) {
                        jsonResponse = new JSONArray();
                        scrollPage = 1;
                        adapter = new BrowseMoviesAdapter(mContext, new ArrayList<Movie>());
                        args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());
                        args.putString(getString(R.string.bundle_url_key),topRatedMoviesURL);
                        args.putString(getString(R.string.bundle_scroll_page_key), String.valueOf(scrollPage));
                        getLoaderManager().restartLoader(LOADER_ID, args, this);
                    }
                }else{
                    adapter = new BrowseMoviesAdapter(mContext, new ArrayList<Movie>());
                    gridView.setAdapter(adapter);
                    offlineView.setVisibility(View.VISIBLE);
                }
                editor.commit();
                return true;

            case R.id.action_favorite:
                item.setChecked(true);
                adapter = new BrowseMoviesAdapter(mContext, new ArrayList<Movie>());
                editor.putString(getString(R.string.sort_mode_key), modeFavorites);
                editor.commit();
                List<Movie> moviesList = Utilities.getFavorites(mContext);
                Utilities.setFavoritesAdapter(gridView, adapter, moviesList);
                if(adapter.isEmpty()){
                    noFavoritesView.setVisibility(View.VISIBLE);
                }
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
        noFavoritesView = (LinearLayout) rootView.findViewById(R.id.no_favorites_view);

        adapter = new BrowseMoviesAdapter(mContext,new ArrayList<Movie>());

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
                ImageView gridPoster = (ImageView) view.findViewById(R.id.movie_poster);
                if (Utilities.isLollipopandAbove()) {
                    gridPoster.setTransitionName(getString(R.string.shared_element_transition_name) + position);
                }

                Movie movie = null;
                if (!sort_pref.equals(modeFavorites)) {
                    try {
                        JSONObject movieJson = jsonResponse.getJSONObject(position);
                        if (movieJson != null) {
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
                //Broadcast to main activity that a movie has been selected
                listener.onItemSelected(movie, gridPoster);
            }
        });

        //Load more Pages on Scroll
        final BrowseMoviesActivityFragment browseFragment = this;
        gridView.setOnScrollListener(new EndlessScrollListener(5, 1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                sort_pref = menu_sp.getString(getString(R.string.sort_mode_key), modePopular);
                scrollPage++;
                args.putString(getString(R.string.bundle_json_response_key), jsonResponse.toString());

                if (sort_pref.equals(modePopular)) {
                    args.putString(getString(R.string.bundle_url_key),popularMoviesURL);
                    args.putString(getString(R.string.bundle_scroll_page_key), String.valueOf(scrollPage));
                    getLoaderManager().restartLoader(LOADER_ID, args, browseFragment);

                } else if (sort_pref.equals(modeTopRated)) {
                    args.putString(getString(R.string.bundle_url_key),topRatedMoviesURL);
                    args.putString(getString(R.string.bundle_scroll_page_key), String.valueOf(scrollPage));
                    getLoaderManager().restartLoader(LOADER_ID, args, browseFragment);
                }
            }
        });
        return rootView;
    }
}
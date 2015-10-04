package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.rashwan.popularmovies.pojos.Movie;
import com.example.rashwan.popularmovies.utilities.Utilities;

public class BrowseMoviesActivity extends AppCompatActivity implements BrowseMoviesActivityFragment.OnItemSelectedListener{
    private Boolean isTwoPane = false;
    //Make selected item not equal to any item in the beginning
    private int selectedItem = -1;
    private static final String TAG_BROWSE_FRAGMENT = "browse_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        FragmentManager fm = getFragmentManager();
        BrowseMoviesActivityFragment browseFragment = (BrowseMoviesActivityFragment) fm.findFragmentByTag(TAG_BROWSE_FRAGMENT);

        //First time to create the fragment
        if (savedInstanceState == null){
            if (browseFragment==null){
                browseFragment = new BrowseMoviesActivityFragment();
                fm.beginTransaction().replace(R.id.browse_container, browseFragment).commit();
            }
        }
        determinePaneLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    @Override
    public void onItemSelected(Movie movie,ImageView posterView) {

        if (isTwoPane){
            //If the user chooses a different movie than the selected one
            if (selectedItem != Integer.valueOf(movie.getId())){
                selectedItem = Integer.valueOf(movie.getId());
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                //If the device on Lollipop+ use fragment enter transition
                if (Utilities.isLollipopandAbove()){
                    MovieDetailsActivityFragment detailsFragment = MovieDetailsActivityFragment.newInstance(getApplicationContext(),movie,posterView.getTransitionName(),false);
                    detailsFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left));
                    ft.replace(R.id.movie_detail_container,detailsFragment).addSharedElement(posterView,posterView.getTransitionName()).commit();

                }else {
                    MovieDetailsActivityFragment detailsFragment = MovieDetailsActivityFragment.newInstance(getApplicationContext(),movie,null,false);
                    ft.replace(R.id.movie_detail_container,detailsFragment).commit();
                }
            }

        }else {
            Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
            detailsIntent.putExtra(getString(R.string.movie_details_extra_key), movie);
            if (Utilities.isLollipopandAbove()){
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (this, posterView,posterView.getTransitionName());
                detailsIntent.putExtra(getString(R.string.shared_element_transition_name),posterView.getTransitionName());
                ActivityCompat.startActivity(this, detailsIntent, optionsCompat.toBundle());
            }else {
                startActivity(detailsIntent);
            }
        }
    }

    private void determinePaneLayout() {
        FrameLayout container = (FrameLayout) findViewById(R.id.movie_detail_container);
        if(container!=null){
            isTwoPane = true;
        }
    }

}

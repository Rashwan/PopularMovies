package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class BrowseMoviesActivity extends AppCompatActivity implements BrowseMoviesActivityFragment.OnItemSelectedListener{
    private Boolean isTwoPane = false;
    private int selectedItem = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        determinePaneLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            if (selectedItem != Integer.valueOf(movie.getId())){
                selectedItem = Integer.valueOf(movie.getId());
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                if (Utilities.isLollipopandAbove()){
                    MovieDetailsActivityFragment detailsFragment = MovieDetailsActivityFragment.newInstance(movie,posterView.getTransitionName());
                    detailsFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left));
                    ft.replace(R.id.movie_detail_container,detailsFragment).addSharedElement(posterView,posterView.getTransitionName()).commit();

                }else {
                    MovieDetailsActivityFragment detailsFragment = MovieDetailsActivityFragment.newInstance(movie,null);
                    ft.replace(R.id.movie_detail_container,detailsFragment).commit();
                }
            }
        }else {

            Intent detailsIntent = new Intent(this, MovieDetalisActivity.class);
            detailsIntent.putExtra(getString(R.string.movie_details_extra_key), movie);
            if (Utilities.isLollipopandAbove()){
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (this, posterView,posterView.getTransitionName());
                detailsIntent.putExtra("transition",posterView.getTransitionName());
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
            BrowseMoviesActivityFragment browseFragment = (BrowseMoviesActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.browse_fragment);
            browseFragment.setActivateOnItemClick(true);

        }
    }

}

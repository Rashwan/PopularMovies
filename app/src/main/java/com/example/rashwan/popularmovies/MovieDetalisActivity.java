package com.example.rashwan.popularmovies;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MovieDetalisActivity extends AppCompatActivity{
    MovieDetailsActivityFragment detailsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_movie_detalis);
        postponeEnterTransition();
        Movie movie = getIntent().getParcelableExtra(getString(R.string.movie_details_extra_key));
        String transitionName = getIntent().getStringExtra("transition");
        detailsFragment = MovieDetailsActivityFragment.newInstance(movie,transitionName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.movie_detail_container,detailsFragment);
        ft.commit();

    }

    @Override
    public void onStop() {
        super.onStop();
        supportFinishAfterTransition();
    }

}

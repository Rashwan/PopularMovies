package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.rashwan.popularmovies.pojos.Movie;
import com.example.rashwan.popularmovies.utilities.Utilities;

public class MovieDetalisActivity extends AppCompatActivity{
    MovieDetailsActivityFragment detailsFragment;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detalis);
        String transitionName = null;
        if (Utilities.isLollipopandAbove()){
            postponeEnterTransition();
            transitionName= getIntent().getStringExtra("transition");
        }
        Movie movie = getIntent().getParcelableExtra(getString(R.string.movie_details_extra_key));
        if (savedInstanceState == null){
            detailsFragment = MovieDetailsActivityFragment.newInstance(movie,transitionName,true);
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.movie_detail_container,detailsFragment);
            ft.commit();
        }


    }

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        super.onStop();
//        if (Utilities.isLollipopandAbove()){
//            finishAfterTransition();
//        }
    }

}

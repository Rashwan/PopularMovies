package com.example.rashwan.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetalisActivityFragment extends Fragment {

    public static final String BASE_URL = "http://image.tmdb.org/t/p/w500/";

    public MovieDetalisActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detalis, container, false);
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("movieDetails")){
            JSONObject movieDetails = null;
            String movieExtras = intent.getExtras().getString("movieDetails");
            try {
                movieDetails = new JSONObject(movieExtras);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ImageView posterView = (ImageView) rootView.findViewById(R.id.big_poster);
            TextView titleView = (TextView) rootView.findViewById(R.id.title);
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
            TextView plotView = (TextView) rootView.findViewById(R.id.plot);
            TextView userRatingView = (TextView) rootView.findViewById(R.id.user_rating);

            try {
                if (movieDetails != null) {
                    String posterUrl = BASE_URL.concat(movieDetails.getString("backdrop_path"));
                    Picasso.with(getActivity()).load(posterUrl).into(posterView);
                    titleView.setText(movieDetails.getString("original_title"));
                    String date = movieDetails.getString("release_date");
                    releaseDateView.setText(date);
                    plotView.setText(movieDetails.getString("overview"));
                    userRatingView.setText(movieDetails.getString("vote_average"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return rootView;
    }
}

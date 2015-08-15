package com.example.rashwan.popularmovies;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {


    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detalis, container, false);
        Intent intent = getActivity().getIntent();

        //get movie object and parse it
        if (intent.hasExtra(getString(R.string.movie_details_extra_key))){
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
                    Uri posterURI = Uri.parse(getString(R.string.poster_base_url)).buildUpon()
                            .appendEncodedPath(movieDetails.getString("backdrop_path"))
                            .build();

                    //Load poster image using Picasso & change the actionBar and statusBar accordingly
                    Picasso.with(getActivity()).load(posterURI).into(posterView
                        , PicassoPalette.with(posterURI.toString(), posterView)
                            .intoCallBack(new PicassoPalette.CallBack() {
                                  @Override
                                  public void onPaletteLoaded(Palette palette) {

                                      //Adding Color to actionBar
                                      Palette.Swatch actionBarColor = palette.getVibrantSwatch();
                                      if (actionBarColor!= null) {
                                          int actionBarColorRGB = actionBarColor.getRgb();

                                          AppCompatActivity actionBarActivity = (AppCompatActivity) getActivity();
                                          android.support.v7.app.ActionBar actionBar = actionBarActivity.getSupportActionBar();
                                          if (actionBar != null) {
                                              actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColorRGB));
                                          }
                                      }

                                      //Adding color to StatusBar
                                      Palette.Swatch statusBarColor = palette.getDarkVibrantSwatch();

                                      if (statusBarColor != null) {
                                          int statusBarColorRGB = statusBarColor.getRgb();

                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                              Window window = getActivity().getWindow();
                                              window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                              window.setStatusBarColor(statusBarColorRGB);
                                          }
                                      }
                                  }
                              }

                                ));


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

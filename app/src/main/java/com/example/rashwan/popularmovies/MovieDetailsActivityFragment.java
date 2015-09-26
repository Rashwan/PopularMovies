package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.florent37.picassopalette.BitmapPalette;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivityFragment extends Fragment {
    Movie movie;
    TrailerAdapter trailerAdapter;
    ListView trailersListview;
    ReviewAdapter reviewAdapter;
    ListView reviewsListview;
    TextView trailersHeader;
    TextView reviewsHeader;
    View dividers ;
    Boolean isFavorite;
    Boolean isLollipop;
    Toolbar toolbar;
    String transitionName;


    private static final String TRAILER_BASE_URL ="http://api.themoviedb.org/3/movie/%s/videos";
    private static final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/%s/reviews";

    public MovieDetailsActivityFragment() {

    }

    public static MovieDetailsActivityFragment newInstance(Movie movie,String transitionName) {
        MovieDetailsActivityFragment detailsFragment = new MovieDetailsActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable("movie", movie);
        if (transitionName !=null){
            args.putString("transition", transitionName);
        }
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        movie = getArguments().getParcelable("movie");
        if (getArguments().containsKey("transition")){
            transitionName = getArguments().getString("transition");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details_fragment, menu);
        if (Utilities.checkConnectivity(getActivity())){
            menu.getItem(0).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onStop();
                getActivity().supportFinishAfterTransition();
                return true;
            case R.id.menu_share:
                List<Trailer> trailerList = movie.getTrailers();
                if (!trailerList.isEmpty()) {
                    Trailer firstTrailer = trailerList.get(0);
                    Utilities.createShareIntent(getActivity(), movie.getTitle(), firstTrailer.getTrailerUri().toString());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded()) {
            if (trailerAdapter.isEmpty()) {
                new FetchDetails().execute(TRAILER_BASE_URL, movie.getId());
            }
            if (reviewAdapter.isEmpty()) {
                new FetchDetails().execute(REVIEW_BASE_URL, movie.getId());
            }
        }

    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_details);

        if (toolbar!= null){
            //onePane Mode
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            try {
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        isFavorite = Utilities.checkFavorite(movie.getId(),getActivity());
        isLollipop = Utilities.isLollipopandAbove();

        final ImageView blur_poster = (ImageView) rootView.findViewById(R.id.blur_poster);
        final ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
        if (transitionName!=null){
            poster.setTransitionName(transitionName);
        }
        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_view);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        TextView plotView = (TextView) rootView.findViewById(R.id.plot);
        TextView userRatingView = (TextView) rootView.findViewById(R.id.user_rating);
        trailersListview = (ListView) rootView.findViewById(R.id.trailers_list_view);
        reviewsListview = (ListView) rootView.findViewById(R.id.reviews_list_view);
        trailersHeader = (TextView) rootView.findViewById(R.id.trailers_header);
        reviewsHeader = (TextView) rootView.findViewById(R.id.reviews_header);
        dividers = rootView.findViewById(R.id.reviews_divider);
        FloatingActionButton fabFavorite = (FloatingActionButton) rootView.findViewById(R.id.fab_favorite);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsingToolbarLayout);
        Utilities.updateHeartButton(fabFavorite,isFavorite);

            fabFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavorite){
                        Utilities.movieDisliked(movie.getId(),getActivity());
                    }else {
                        Utilities.movieLiked(movie, getActivity());
                    }
                    isFavorite = Utilities.checkFavorite(movie.getId(),getActivity());
                    Utilities.updateHeartButton((FloatingActionButton) v,isFavorite);
                }
            });
            if (movie != null) {
                Uri blurPosterUri = movie.getBlurPosterUri();

                Picasso.with(getActivity()).load(blurPosterUri).
                        fit().centerCrop().into(blur_poster,
                        PicassoPalette.with(blurPosterUri.toString(), blur_poster)
                                .intoCallBack(new BitmapPalette.CallBack() {
                                    @Override
                                    public void onPaletteLoaded(Palette palette) {
                                        //Add Blur
                                        Bitmap bitmap = ((BitmapDrawable) blur_poster.getDrawable()).getBitmap();
                                        blur_poster.setImageBitmap(Utilities.blurRenderScript(bitmap, 18, getActivity()));

                                        //Apply Palette  Effect
                                        applyPalette(palette, collapsingToolbarLayout);
                                    }
                                }));
                Uri posterUri = movie.getPosterUri();

                Picasso.with(getActivity()).load(posterUri).fit().into(poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        poster.getViewTreeObserver().addOnPreDrawListener(
                                new ViewTreeObserver.OnPreDrawListener() {
                                    @Override
                                    public boolean onPreDraw() {
                                        if (isLollipop) {
                                            poster.getViewTreeObserver().removeOnPreDrawListener(this);
                                            getActivity().startPostponedEnterTransition();
                                        }
                                        return true;

                                    }
                                });
                    }

                    @Override
                    public void onError() {

                    }
                });

                String movieTitle = movie.getTitle();
                if (titleView != null){
                    //TwoPane Mode
                    titleView.setText(movieTitle);
                }else {
                    //SinglePane Mode With CollapsingToolbarLayout
                    if (Utilities.getDeviceSW(getContext())>=600){
                        if (movieTitle.length()>33){
                            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.small_expanded);
                            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.small_collapsed);
                        }
                    }else {
                        if (movieTitle.length()>15){
                            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.small_expanded);
                            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.small_collapsed);
                        }
                    }

                    collapsingToolbarLayout.setTitle(movieTitle);
                }

                releaseDateView.setText(movie.getReleaseDate());
                plotView.setText(movie.getPlot());
                userRatingView.setText(movie.getVoteAverage());
            }
            trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
            trailersListview.setAdapter(trailerAdapter);
            trailersListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trailer selectedTrailer = (Trailer) trailerAdapter.getItem(position);
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, selectedTrailer.getTrailerUri());
                    startActivity(trailerIntent);
                }
            });

            reviewAdapter = new ReviewAdapter(getActivity(),new ArrayList<Review>());
            reviewsListview.setAdapter(reviewAdapter);


//        }


        return rootView;
    }


    private void applyPalette(Palette palette,CollapsingToolbarLayout collapsingToolbar){
        if (palette !=null && toolbar != null) {
            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
            if (vibrantSwatch!=null){
                int actionBarColorRGB = vibrantSwatch.getRgb();
                collapsingToolbar.setContentScrimColor(actionBarColorRGB);
            }

            //Adding color to StatusBar
            Palette.Swatch darkVSwatch = palette.getDarkVibrantSwatch();
            if (darkVSwatch != null) {
                int statusBarColorRGB = palette.getDarkVibrantSwatch().getRgb();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getActivity().getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(statusBarColorRGB);
                }
            }
        }
    }

    public class FetchDetails extends AsyncTask<String,Void,List<?>>{
        private String responseJsonString;
        private final  String LOG_TAG = AsyncTask.class.getSimpleName();
        private Boolean isTrailer;


        private List<Trailer> getTrailersFromJson(String trailersJsonString) throws JSONException {
            JSONObject object = new JSONObject(trailersJsonString);
            JSONArray  trailersArray = object.getJSONArray("results");
            List<Trailer> trailerList = new ArrayList<>();
            Trailer trailer;

            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject jsonTrailer = trailersArray.getJSONObject(i);
                trailer = new Trailer(jsonTrailer.getString("key"),jsonTrailer.getString("name"));
                trailerList.add(trailer);
                movie.setTrailers(trailerList);
            }
            return trailerList;

        }


        private List<Review> getReviewsFromJson(String reviewsJsonString) throws JSONException {
            JSONObject object = new JSONObject(reviewsJsonString);
            JSONArray  reviewsArray = object.getJSONArray("results");
            List<Review> reviewsList = new ArrayList<>();
            Review review;

            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject jsonReview = reviewsArray.getJSONObject(i);
                review = new Review(jsonReview.getString("author"),jsonReview.getString("content"));
                reviewsList.add(review);
                movie.setReviews(reviewsList);
            }
            return reviewsList;

        }


        @Override
        protected List<?> doInBackground(String... params) {
            if (isAdded()) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String baseURl = String.format(params[0], params[1]);
                if (params[0].contains("videos")) {
                    isTrailer = true;
                } else {
                    isTrailer = false;
                }
                Uri uri = Uri.parse(baseURl).buildUpon().appendQueryParameter(getString(R.string.api_key_query_param), getString(R.string.movie_db_api_key)).build();
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
                    responseJsonString = buffer.toString();
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
                    if (isTrailer) {
                        return getTrailersFromJson(responseJsonString);
                    } else {
                        return getReviewsFromJson(responseJsonString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<?>responseList) {
            Log.e(LOG_TAG, "ONPOST");
            if (responseList!=null && !responseList.isEmpty()) {
                dividers.setVisibility(View.VISIBLE);
                if (isTrailer) {
                    trailersHeader.setVisibility(View.VISIBLE);
                    List<Trailer> trailersList = (List<Trailer>) responseList;
                    trailerAdapter.add(trailersList);
                    Utilities.setListViewHeightBasedOnChildren(trailersListview);
                    trailerAdapter.notifyDataSetChanged();

                } else {
                    reviewsHeader.setVisibility(View.VISIBLE);
                    List<Review> reviewsList = (List<Review>) responseList;
                    for (Review review : reviewsList) {
                        Log.e(LOG_TAG, review.getAuthor() + ": " + review.getContent());
                    }
                    reviewAdapter.add(reviewsList);
                    Utilities.setListViewHeightBasedOnChildren(reviewsListview);
                    reviewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

}
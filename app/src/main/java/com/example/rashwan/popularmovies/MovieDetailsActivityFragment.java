package com.example.rashwan.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.florent37.picassopalette.BitmapPalette;
import com.github.florent37.picassopalette.PicassoPalette;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {
    public Movie movie;
    TrailerAdapter trailerAdapter;
    ListView trailersListview;
    ReviewAdapter reviewAdapter;
    ListView reviewsListview;
    TextView trailersHeader;
    TextView reviewsHeader;
    View dividers ;
    Boolean isFavorite;
    Boolean isLollipop;

    private static final String TRAILER_BASE_URL ="http://api.themoviedb.org/3/movie/%s/videos";
    private static final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/%s/reviews";

    public MovieDetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        if (trailerAdapter.isEmpty()) {
            new FetchDetails().execute(TRAILER_BASE_URL, movie.getId());
        }if (reviewAdapter.isEmpty()){
            new FetchDetails().execute(REVIEW_BASE_URL, movie.getId());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movie_detalis, container, false);
        Intent intent = getActivity().getIntent();

        //get movie object and parse it
        if (intent.hasExtra(getString(R.string.movie_details_extra_key))) {
            movie = intent.getParcelableExtra(getString(R.string.movie_details_extra_key));

            isFavorite = Utilities.checkFavorite(movie.getId(),getActivity());
            isLollipop = Utilities.isLollipopandAbove();

            TextView titleView = (TextView) rootView.findViewById(R.id.title);
            final ImageView blur_poster = (ImageView) rootView.findViewById(R.id.blur_poster);
            ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
            TextView plotView = (TextView) rootView.findViewById(R.id.plot);
            TextView userRatingView = (TextView) rootView.findViewById(R.id.user_rating);
            trailersListview = (ListView) rootView.findViewById(R.id.trailers_list_view);
            reviewsListview = (ListView) rootView.findViewById(R.id.reviews_list_view);
            trailersHeader = (TextView) rootView.findViewById(R.id.trailers_header);
            reviewsHeader = (TextView) rootView.findViewById(R.id.reviews_header);
            dividers = rootView.findViewById(R.id.reviews_divider);
            final FloatingActionButton fabFavorite = (FloatingActionButton) rootView.findViewById(R.id.fab_favorite);
            final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
            Log.e("isFavoriteOncreate",isFavorite.toString());
            Utilities.updateHeartButton(fabFavorite,isLollipop,isFavorite);

            final int[] oldY = {0};
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    int scrollY = scrollView.getScrollY();
                    if (scrollY <= 0) {
                        fabFavorite.show();
                    } else if (scrollY > oldY[0]) {
                        fabFavorite.hide();
                    } else {
                        fabFavorite.show();
                    }
                    oldY[0] = scrollY;
                }
            });
            fabFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavorite){
                        Utilities.movieDisliked(movie.getId(),getActivity());
                    }else {
                        Utilities.movieLiked(movie, getActivity());
                    }
                    isFavorite = Utilities.checkFavorite(movie.getId(),getActivity());
                    Utilities.updateHeartButton((FloatingActionButton) v,isLollipop,isFavorite);
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
                                        applyPalette(palette);
                                    }
                                }));
                Uri posterUri = movie.getPosterUri();
                Picasso.with(getActivity()).load(posterUri).into(poster);

                //Setting up Movie Details
                titleView.setText(movie.getTitle());
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


        }


        return rootView;
    }


    private void applyPalette(Palette palette){
        if (palette !=null) {
            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
            if (vibrantSwatch!=null){
                int actionBarColorRGB = vibrantSwatch.getRgb();

                AppCompatActivity actionBarActivity = (AppCompatActivity) getActivity();
                android.support.v7.app.ActionBar actionBar = actionBarActivity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColorRGB));
                }
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
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String baseURl = String.format(params[0], params[1]);
            if (params[0].contains("videos")) {
                isTrailer = true;
            }else{
                isTrailer = false;
            }
            Uri uri = Uri.parse(baseURl).buildUpon().appendQueryParameter(getString(R.string.api_key_query_param),getString(R.string.movie_db_api_key)).build();
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
                }else{
                    return getReviewsFromJson(responseJsonString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

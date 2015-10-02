package com.example.rashwan.popularmovies;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivityFragment extends android.app.Fragment implements LoaderManager.LoaderCallbacks<List<?>>{
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
    private Boolean showActionBar;
    private static final int TRAILER_LOADER_ID = 2;
    private static final int REVIEW_LOADER_ID = 3;

    private static final String TRAILER_BASE_URL ="http://api.themoviedb.org/3/movie/%s/videos";
    private static final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/%s/reviews";

    public MovieDetailsActivityFragment() {

    }

    public static MovieDetailsActivityFragment newInstance(Movie movie,String transitionName,Boolean showToolBar) {
        MovieDetailsActivityFragment detailsFragment = new MovieDetailsActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable("movie", movie);
        if (transitionName !=null){
            args.putString("transition", transitionName);
        }
        args.putBoolean("toolbar",showToolBar);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    @Override
    public Loader<List<?>> onCreateLoader(int id, Bundle args) {
        Log.e("ONCREATELOADER", "H");
        return new FetchDetailsAsync(getActivity(),args);
    }

    @Override
    public void onLoadFinished(Loader<List<?>> loader, List<?> data) {
        FetchDetailsAsync detailsAsync = (FetchDetailsAsync) loader;
        Boolean isTrailer = detailsAsync.getIsTrailer();
        if (data!=null && !data.isEmpty()) {
            dividers.setVisibility(View.VISIBLE);
            if (isTrailer) {

                trailersHeader.setVisibility(View.VISIBLE);
                List<Trailer> trailersList = (List<Trailer>) data;
                trailerAdapter.add(trailersList);
                movie.setTrailers(trailersList);
                Utilities.setListViewHeightBasedOnChildren(trailersListview);
                trailerAdapter.notifyDataSetChanged();

            } else {
                Log.e("ONLOADFINISHEDREVIEWS","||");
                reviewsHeader.setVisibility(View.VISIBLE);
                List<Review> reviewsList = (List<Review>) data;
                reviewAdapter.add(reviewsList);
                movie.setReviews(reviewsList);
                Utilities.setListViewHeightBasedOnChildren(reviewsListview);
                reviewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<?>> loader) {
        FetchDetailsAsync detailsAsync = (FetchDetailsAsync) loader;
        Boolean isTrailer = detailsAsync.getIsTrailer();
        if (isTrailer){
            trailerAdapter = new TrailerAdapter(getActivity(),new ArrayList<Trailer>());
        }else {
            reviewAdapter = new ReviewAdapter(getActivity(),new ArrayList<Review>());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        movie = getArguments().getParcelable("movie");
        showActionBar = getArguments().getBoolean("toolbar");
        if (getArguments().containsKey("transition")){
            transitionName = getArguments().getString("transition");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.isVisible() && Utilities.checkConnectivity(getActivity())){
            inflater.inflate(R.menu.menu_movie_details_fragment, menu);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onStop();
                if (isLollipop){
                    getActivity().finishAfterTransition();
                }
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
    public void onResume() {
        Log.e("ONResume", "Ww");

        super.onResume();
        if (isAdded()) {
            Bundle args = new Bundle();
            args.putString(getString(R.string.bundle_movie_id_key),movie.getId());

            if (trailerAdapter.isEmpty()) {
                args.putString(getString(R.string.bundle_base_url_key), TRAILER_BASE_URL);
                getLoaderManager().initLoader(TRAILER_LOADER_ID, args, this);
            }
            if (reviewAdapter.isEmpty()) {
                args.putString(getString(R.string.bundle_base_url_key), REVIEW_BASE_URL);
                getLoaderManager().initLoader(REVIEW_LOADER_ID, args, this);
            }
        }

    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_details);

        if (showActionBar && toolbar!= null){
            //onePane Mode
            toolbar.setVisibility(View.VISIBLE);
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
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date_formatted);
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
                    if (isFavorite) {
                        Utilities.movieDisliked(movie.getId(), getActivity());
                    } else {
                        Utilities.movieLiked(movie, getActivity());
                    }
                    isFavorite = Utilities.checkFavorite(movie.getId(), getActivity());
                    Utilities.updateHeartButton((FloatingActionButton) v, isFavorite);
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
                if (titleView != null && !showActionBar){
                    //TwoPane Mode
                    titleView.setText(movieTitle);
                }else {
                    //SinglePane Mode With CollapsingToolbarLayout
                    if (Utilities.getDeviceSW(getActivity())>=600){
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

                releaseDateView.setText(Utilities.getFormattedDate(movie.getReleaseDate()));
                userRatingView.setText(getString((R.string.vote_average), movie.getVoteAverage()));
                plotView.setText(movie.getPlot());
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
        return rootView;
    }


    private void applyPalette(Palette palette,CollapsingToolbarLayout collapsingToolbar){
        if (palette !=null && toolbar!=null) {
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
}
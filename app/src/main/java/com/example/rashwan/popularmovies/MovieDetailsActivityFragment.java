package com.example.rashwan.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.picassopalette.BitmapPalette;
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
        final View rootView = inflater.inflate(R.layout.fragment_movie_detalis, container, false);
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
            TextView titleView = (TextView) rootView.findViewById(R.id.title);
            final ImageView poster = (ImageView) rootView.findViewById(R.id.image);
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
            TextView plotView = (TextView) rootView.findViewById(R.id.plot);
            TextView userRatingView = (TextView) rootView.findViewById(R.id.user_rating);




            try {
                if (movieDetails != null) {
                     Uri posterURI = null;
                    try {
                        posterURI = Uri.parse(getString(R.string.poster_base_url)).buildUpon()
                                .appendEncodedPath(movieDetails.getString("backdrop_path"))
                                .build();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Picasso.with(getActivity()).load(posterURI).fit().centerCrop().into(poster,PicassoPalette.with(posterURI.toString(),poster)
                    .intoCallBack(new BitmapPalette.CallBack() {
                        @Override
                        public void onPaletteLoaded(Palette palette) {
                            //Add Blur
                            Bitmap bitmap = ((BitmapDrawable)poster.getDrawable()).getBitmap();
                            poster.setImageBitmap(blurRenderScript(bitmap,18));

                            //Apply Palette  Effect
                            applyPalette(palette);
                        }
                    }));

                    //Setting up Movie Details
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

    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(getActivity());

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    private void applyPalette(Palette palette){

        int actionBarColorRGB = palette.getMutedColor(R.color.ColorPrimary);

        AppCompatActivity actionBarActivity = (AppCompatActivity) getActivity();
        android.support.v7.app.ActionBar actionBar = actionBarActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColorRGB));
        }


        //Adding color to StatusBar

        int statusBarColorRGB = palette.getDarkMutedColor(R.color.ColorPrimaryDark);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColorRGB);
        }

    }
}

package com.example.rashwan.popularmovies;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.rashwan.popularmovies.provider.movie.MovieColumns;
import com.example.rashwan.popularmovies.provider.movie.MovieContentValues;
import com.example.rashwan.popularmovies.provider.movie.MovieCursor;
import com.example.rashwan.popularmovies.provider.movie.MovieSelection;

/**
 * Created by rashwan on 9/13/15.
 */
public class Utilities {

    public static Bitmap blurRenderScript(Bitmap smallBitmap, int radius, Context context) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

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

    public static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static Boolean checkConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void createShareIntent(Activity activity, String title, String trailerUrl) {

        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(activity.getString(R.string.share_text, title, trailerUrl));
        activity.startActivity(Intent.createChooser(builder.getIntent(), activity.getString(R.string.share_chooser_title)));
    }

    public static void updateHeartButton(final FloatingActionButton fab, Boolean isLollipop, final Boolean isFavorite) {
        if (isLollipop) {
            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(fab, "scaleX", 0.2f, 1f);
            bounceAnimX.setDuration(300);
            bounceAnimX.setInterpolator(new OvershootInterpolator(4));

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(fab, "scaleY", 0.2f, 1f);
            bounceAnimY.setDuration(300);
            bounceAnimY.setInterpolator(new OvershootInterpolator(4));
            bounceAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (isFavorite) {
                        fab.setImageResource(R.drawable.ic_heart_red_filled);
                    } else {
                        fab.setImageResource(R.drawable.ic_heart_outline_red);
                    }
                }
            });

            animatorSet.play(bounceAnimX).with(bounceAnimY);

            animatorSet.start();
        } else {
            if (isFavorite) {
                fab.setImageResource(R.drawable.ic_heart_outline_red);
            }else{
                fab.setImageResource(R.drawable.ic_heart_red_filled);
            }
        }
    }

    public static Uri movieLiked(Movie movie,Context context){
        MovieContentValues contentValues = new MovieContentValues();
        contentValues.putMovieId(movie.getId()).putTitle(movie.getTitle()).putReleaseDate(movie.getReleaseDate())
                .putVoteAverage(movie.getVoteAverage()).putPlot(movie.getPlot()).putHomeUri(movie.getHomePath()).
                putPosterUri(movie.getPosterPath()).putBlurPosterUri(movie.getBlurPosterPath());
        return context.getContentResolver().insert(MovieColumns.CONTENT_URI, contentValues.values());
    }

    public static Boolean movieDisliked(String movieId,Context context){
        MovieSelection where = new MovieSelection();
        int rows = where.movieId(movieId).delete(context);

       return rows!=0;
    }

    public static Boolean checkFavorite(String movieId,Context context){
        MovieSelection where = new MovieSelection();
        MovieCursor cursor = where.movieId(movieId).query(context);
        Boolean isFavorite = cursor.moveToNext();
        cursor.close();
        return isFavorite;
    }

    public static Boolean isLollipopandAbove(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ;
    }

}

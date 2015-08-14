package com.example.rashwan.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rashwan on 8/11/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private JSONArray mMovies;
    public static final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public ImageAdapter(Context context,JSONArray movies) {
        this.mContext = context;
        this.mMovies = movies;
    }

    @Override
    public int getCount() {
        return mMovies.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mMovies.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject movie = (JSONObject) getItem(position);
        String posterUrl = null;
        String nameJson = null;
        try {
            posterUrl = BASE_URL.concat(movie.getString("backdrop_path"));
            nameJson = movie.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View movieView = convertView;
        if (convertView == null) {
            movieView = inflater.inflate(R.layout.movie_item, null);

        }
        SquaredImageView poster = (SquaredImageView) movieView.findViewById(R.id.movie_poster);
        TextView name = (TextView) movieView.findViewById(R.id.movie_name);
        LinearLayout girdLayout = (LinearLayout) movieView.findViewById(R.id.gird_layout);

        name.setText(nameJson);
        //Picasso.with(mContext).load(posterUrl).into(poster);
        Picasso.with(mContext).load(posterUrl).into(poster
                , PicassoPalette.with(posterUrl, poster)
                .use(PicassoPalette.Profile.MUTED_LIGHT)
                .intoBackground(girdLayout)
                .intoTextColor(name, PicassoPalette.Swatch.TITLE_TEXT_COLOR));
        return movieView;
    }

}

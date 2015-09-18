package com.example.rashwan.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rashwan on 8/11/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> mMovies = new ArrayList<>();
    public static final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
    boolean isConnected ;

    public ImageAdapter(Context context,List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
        isConnected = Utilities.checkConnectivity(mContext);
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int position) {
            return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = (Movie) getItem(position);
        Uri posterUri = movie.getHomeUri();
        String nameString = movie.getTitle();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View movieView = convertView;
        if (convertView == null) {
            movieView = inflater.inflate(R.layout.movie_item, null);

        }

        ImageView poster = (ImageView) movieView.findViewById(R.id.movie_poster);
        TextView name = (TextView) movieView.findViewById(R.id.movie_name);
        LinearLayout girdLayout = (LinearLayout) movieView.findViewById(R.id.gird_layout);

        name.setText(nameString);
        Picasso.with(mContext).load(posterUri).fit().into(poster
                , PicassoPalette.with(posterUri.toString(), poster)
                .use(PicassoPalette.Profile.VIBRANT)
                .intoBackground(girdLayout)
                .intoTextColor(name, PicassoPalette.Swatch.BODY_TEXT_COLOR));
        return movieView;
    }

    public void add(List<Movie> result){
        for (int i = 0; i < result.size(); i++) {
            mMovies.add(result.get(i));
        }
    }

}

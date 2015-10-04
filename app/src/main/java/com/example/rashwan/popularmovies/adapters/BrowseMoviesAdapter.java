package com.example.rashwan.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.rashwan.popularmovies.R;
import com.example.rashwan.popularmovies.pojos.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rashwan on 8/11/15.
 */
public class BrowseMoviesAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> mMovies = new ArrayList<>();

    public BrowseMoviesAdapter(Context context, List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
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
        BrowseMoviesViewHolder browseMoviesViewHolder = new BrowseMoviesViewHolder(movieView);

        if (nameString.length()<=18){

            browseMoviesViewHolder.name.setTextAppearance(mContext,R.style.grid_title_text_big);
        }
        browseMoviesViewHolder.name.setText(nameString);
        Picasso.with(mContext).load(posterUri).fit().into(browseMoviesViewHolder.poster
                , PicassoPalette.with(posterUri.toString(), browseMoviesViewHolder.poster)
                .use(PicassoPalette.Profile.VIBRANT)
                .intoBackground(browseMoviesViewHolder.gridLayout)
                .intoTextColor(browseMoviesViewHolder.name, PicassoPalette.Swatch.BODY_TEXT_COLOR));
        return movieView;
    }

    public void add(List<Movie> result){
        for (int i = 0; i < result.size(); i++) {
            mMovies.add(result.get(i));
        }
    }
    public void clear(){
        mMovies.clear();
    }

}

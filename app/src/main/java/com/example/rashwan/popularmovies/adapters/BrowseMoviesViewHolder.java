package com.example.rashwan.popularmovies.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rashwan.popularmovies.R;

/**
 * Created by rashwan on 10/4/15.
 */
public class BrowseMoviesViewHolder {

    public final ImageView poster;
    public final TextView name;
    public final LinearLayout gridLayout;


    public BrowseMoviesViewHolder(View view) {
        poster = (ImageView) view.findViewById(R.id.movie_poster);
        name = (TextView) view.findViewById(R.id.movie_name);
        gridLayout = (LinearLayout) view.findViewById(R.id.gird_layout);
    }
}

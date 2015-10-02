package com.example.rashwan.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rashwan.popularmovies.R;
import com.example.rashwan.popularmovies.pojos.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rashwan on 9/12/15.
 */
public class TrailerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Trailer> mTrailers;
    public TrailerAdapter(Context context,List<Trailer> trailers) {
        this.mContext =context;
        this.mTrailers = trailers;
    }

    @Override
    public int getCount() {
        return mTrailers.size();
    }

    @Override
    public Object getItem(int position) {
        return mTrailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View trailerView = convertView;
        if (convertView == null){
            trailerView = inflater.inflate(R.layout.trailer_list_item, null);
        }

        ImageView trailerThumbnail = (ImageView) trailerView.findViewById(R.id.youtube_thumbnail);
        TextView trailerName = (TextView) trailerView.findViewById(R.id.trailer_name);
        Trailer trailer = (Trailer) getItem(position);

        Picasso.with(mContext).load(trailer.getTrailerImageUri()).fit().centerCrop().into(trailerThumbnail);
        trailerName.setText(trailer.getName());

        return trailerView;
    }

    public void add(List<Trailer> result){
        for (int i = 0; i < result.size(); i++) {
            mTrailers.add(result.get(i));
        }
    }
    public void clear(){
        mTrailers.clear();
    }
}

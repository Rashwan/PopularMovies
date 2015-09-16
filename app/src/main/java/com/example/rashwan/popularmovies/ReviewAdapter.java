package com.example.rashwan.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rashwan on 9/14/15.
 */
public class ReviewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Review> mReviews;

    public ReviewAdapter(Context mContext, List<Review> mReviews) {
        this.mContext = mContext;
        this.mReviews = mReviews;
    }

    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View reviewView = inflater.inflate(R.layout.review_list_item,null);
        TextView reviewAuthor = (TextView) reviewView.findViewById(R.id.review_author);
        TextView reviewContent = (TextView) reviewView.findViewById(R.id.review_content);
        Review review = (Review) getItem(position);
        reviewAuthor.setText(review.getAuthor());
        reviewContent.setText(review.getContent());
        return reviewView;
    }

    public void add(List<Review> result){
        for (int i = 0; i < result.size(); i++) {
            mReviews.add(result.get(i));
        }
    }
}

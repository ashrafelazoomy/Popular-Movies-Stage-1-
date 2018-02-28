package com.example.adam.popularmoviesstage1.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.adam.popularmoviesstage1.R;
import com.example.adam.popularmoviesstage1.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 2/23/2018.
 */

public class ImageAdapter  extends BaseAdapter {
    private final Context context;
    private final  Movie[]  moviesArray;

    /**
     * Constructor
     *
     * @param context Application context
     * @param movies  Movie array
     */
    public ImageAdapter(Context context, Movie[] movies) {
        this.context = context;
        moviesArray = movies;
    }

    @Override
    public int getCount() {
        if (moviesArray == null || moviesArray.length == 0) {
            return -1;
        }

        return moviesArray.length;
    }

    @Override
    public Movie getItem(int position) {
        if (moviesArray == null || moviesArray.length == 0) {
            return null;
        }

        return moviesArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        //  Will initialize ImageView if it is new. otherwise use the recycled one
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(context)
                .load(moviesArray[position].getPosterPath())
                .resize(context.getResources().getInteger(R.integer.the_movie_db_poster_w185_width),
                       context.getResources().getInteger(R.integer.the_movie_db_poster_w185_height))
                .error(R.mipmap.ic_launcher_round)//will enhance it later with good photo
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);

        return imageView;
    }
}
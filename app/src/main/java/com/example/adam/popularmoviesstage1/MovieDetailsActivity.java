package com.example.adam.popularmoviesstage1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adam.popularmoviesstage1.model.Movie;
import com.example.adam.popularmoviesstage1.utils.DateTimeHelper;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.poster_imgv) ImageView poster_imgv;
    @BindView(R.id.original_title_tv)  TextView original_title_tv;
    @BindView(R.id.overview_tv) TextView overview_tv;
    @BindView(R.id.vote_average_tv) TextView vote_average_tv;
    @BindView(R.id.release_date_tv) TextView release_date_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        original_title_tv.setText(movie.getOriginalTitle());

        Picasso.with(this)
                .load(movie.getPosterPath())
                .resize(getResources().getInteger(R.integer.the_movie_db_poster_w185_width),
                        getResources().getInteger(R.integer.the_movie_db_poster_w185_height))
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher)
                .into(poster_imgv);

        overview_tv.setText(movie.getOverview());
        vote_average_tv.setText(movie.getDetailedVoteAverage());

        // Get Release Date and show it with the user format.
        String releaseDate = movie.getReleaseDate();
        if(releaseDate != null) {
            try {
                releaseDate = DateTimeHelper.getLocalizedDate(this,
                        releaseDate, movie.getDateFormat());
                release_date_tv.setText(releaseDate);
            } catch (ParseException e) {

            }
        }

    }
}

package com.example.adam.popularmoviesstage1.utils;

import com.example.adam.popularmoviesstage1.model.Movie;

/**
 * Created by Adam on 2/23/2018.
 */


public interface OnTaskCompleted {
    void onFetchMoviesTaskCompleted(Movie[] movies);
}

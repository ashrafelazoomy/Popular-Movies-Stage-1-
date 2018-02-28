package com.example.adam.popularmoviesstage1.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.adam.popularmoviesstage1.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Adam on 2/23/2018.
 */

public class FetchMovieAsyncTask extends AsyncTask<String, Void, Movie[]> {
    private final String theMovieDBBaseURL;
    private final String theMovieDBAPIKey;
    private final OnTaskCompleted mListener;

     
    public FetchMovieAsyncTask(OnTaskCompleted listener, String theMovieDBAPIKey,String theMovieDBBaseURL) {
        super();
        mListener = listener;
        this.theMovieDBBaseURL=theMovieDBBaseURL;
        this.theMovieDBAPIKey = theMovieDBAPIKey;

    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Save the data returned from the API
        String moviesJsonStr = null;

        try {
            URL url = getApiUrl(params);

            // connect to the api to get JSON
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            if (builder.length() == 0) {
                // No data found. Nothing to do here.
                return null;
            }

            moviesJsonStr = builder.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {

                }
            }
        }

        try {
            // Parse JSON data
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * Extract data from the JSON object and returns an Array of movie objects.
     *
     * @param moviesJsonStr JSON string to be traversed
     * @return Array of Movie objects
     * @throws JSONException
     */
    private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        Movie[] movies = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            movies[i] = new Movie(); 
            JSONObject movieInfo = resultsArray.getJSONObject(i);
            
            movies[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            movies[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
        }

        return movies;
    }

    /**
     * Build and returns an URL.
     */
    private URL getApiUrl(String[] parameters) throws MalformedURLException {
        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        Uri builtUri = Uri.parse( theMovieDBBaseURL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, parameters[0])
                .appendQueryParameter(API_KEY_PARAM, theMovieDBAPIKey)
                .build();

        return new URL(builtUri.toString());
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        // Notify UI
        mListener.onFetchMoviesTaskCompleted(movies);
    }
}

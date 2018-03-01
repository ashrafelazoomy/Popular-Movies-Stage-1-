package com.example.adam.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.adam.popularmoviesstage1.Adapters.ImageAdapter;
import com.example.adam.popularmoviesstage1.model.Movie;
import com.example.adam.popularmoviesstage1.utils.FetchMovieAsyncTask;
import com.example.adam.popularmoviesstage1.utils.OnTaskCompleted;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movies_gv) GridView movies_gv;
Menu menuMain;
    private static final String theMovieDBAPIKey = BuildConfig.key_movie_db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        movies_gv.setOnItemClickListener(moviePosterClickListener);
        if (savedInstanceState == null) {
            // Get data from the movie DB
            getMoviesFromTheMovieDB(getSortMethodFromSharedPreferences());
        } else {
            // Get data from  saved instance state
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_movie));

            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int i = 0; i < numMovieObjects; i++) {
                    movies[i] = (Movie) parcelable[i];
                }

                //Bind the grid view
                movies_gv.setAdapter(new ImageAdapter(this, movies));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuMain = menu;
        updateMenu();
        return true;
    }

    // Update menu_main to show relevant items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.pref_sort_popular_desc_key:
                updateSharedPrefs(getString(R.string.the_movie_db_sort_pop_desc));
                updateMenu();
                getMoviesFromTheMovieDB(getSortMethodFromSharedPreferences());
                return true;
            case R.string.pref_sort_vote_avg_desc_key:
                updateSharedPrefs(getString(R.string.the_movie_db_sort_vote_avg_desc));
                updateMenu();
                getMoviesFromTheMovieDB(getSortMethodFromSharedPreferences());
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Listener for clicks on movie posters in GridView
     */
    private final GridView.OnItemClickListener moviePosterClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Movie movie = (Movie) parent.getItemAtPosition(position);
            Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
            intent.putExtra(getResources().getString(R.string.parcel_movie), (Parcelable) movie);
            startActivity(intent);
        }
    };


   //Get the movies from the API
    private void getMoviesFromTheMovieDB(String sortMethod) {
        if (isNetworkAvailable()) {
            // Movies API Key
            //Base URL
            String theMovieDBBaseURL =getString(R.string.the_movie_db_base_url);
            // Listener for when AsyncTask is ready to update UI
            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onFetchMoviesTaskCompleted(Movie[] movies) {
                    movies_gv.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                }
            };
            // Execute API task
            FetchMovieAsyncTask movieTask = new FetchMovieAsyncTask(taskCompleted, theMovieDBAPIKey,theMovieDBBaseURL);
            movieTask.execute(sortMethod);
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if the Internet is connected.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Update menu_main based on method found set in SharedPreferences
     */
    private void updateMenu() {
        String sortMethod = getSortMethodFromSharedPreferences();

        if (sortMethod.equals(getString(R.string.the_movie_db_sort_pop_desc))) {
            menuMain.findItem(R.string.pref_sort_popular_desc_key).setVisible(false);
            menuMain.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(true);
        } else {
            menuMain.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(false);
            menuMain.findItem(R.string.pref_sort_popular_desc_key).setVisible(true);
        }
    }

    /**
     * Gets the sort method SharedPreferences.
     */
    private String getSortMethodFromSharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.the_movie_db_sort_pop_desc));
    }

    /**
     * Saves the selected sort method in SharedPreferences
     */
    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int numMovieObjects = movies_gv.getCount();
        if (numMovieObjects > 0) {
            // Get Movie objects from grid view
            Movie[] movies = new Movie[numMovieObjects];
            for (int i = 0; i < numMovieObjects; i++) {
                movies[i] = (Movie) movies_gv.getItemAtPosition(i);
            }
            // Save Movie objects to Parcel
            outState.putParcelableArray(getString(R.string.parcel_movie), (Parcelable[]) movies);
        }
        super.onSaveInstanceState(outState);
    }

}

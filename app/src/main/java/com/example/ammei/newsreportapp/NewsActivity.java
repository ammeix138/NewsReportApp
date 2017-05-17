package com.example.ammei.newsreportapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    /**
     * Constant value for Log Tag.
     */
    private static final String LOG_TAG = NewsActivity.class.getName();

    /**
     * Constant value for the news loader ID.
     */
    private static final int NEWS_LOADER_ID = 1;
    /**
     * URL for the news data from the Guardian dataset
     */
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?&" +
                    "order-by=newest&show-fields=All&page-size=20&page=1&" +
                    "api-key=fb315fe0-d28f-4287-8d1e-f7d9b4a4114c";
    /**
     * Adapter for the list of articles being displayed
     */
    private NewsAdapter mAdapter;

    /*
     * Progress bar is displayed when the dataset is queried
     */
    /**
     * TextView that is displayed when the list is empty from No Connectivity or No Results
     */
    private TextView mEmptyStateTextView;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        //Loading Indicator which appears at the beginning of a query
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        //EmptyView which will appear when there are no contents to populate the List.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        //Create a new adapter that takes he list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set the {@link ListView}
        //so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        //Set an item click listener on the ListView, which sends the intent to a web browser
        //to open a website with more information about the selected news article.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Find the current news article that was clicked on
                News currentNews = mAdapter.getItem(position);

                //Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentNews != null;
                Uri newsUri = Uri.parse(currentNews.getURL());

                //Intent constructor which send the actual intent
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                //Initiates the Intent
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            Log.i(LOG_TAG, "Let me know if network is connected");

            loaderManager.initLoader(NEWS_LOADER_ID, null, NewsActivity.this);
            Log.i(LOG_TAG, "Works");
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            //Displays empty view to the user when there is no internet connection.
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String articleSection;

        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        loadingIndicator.setVisibility(View.GONE);

        if (news == null) {
            //Set empty state to display when no news has been found
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText
                    (getString(R.string.no_news_available));
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mAdapter.clear();
        }


        if (news != null && !news.isEmpty()) {
            loadingIndicator.setVisibility(View.GONE);
            mAdapter.addAll(news);

            //Notify the adapter with all changes
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
        mEmptyStateTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getGroupId();
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class NewPreferenceFragment extends PreferenceFragment {

    }
}






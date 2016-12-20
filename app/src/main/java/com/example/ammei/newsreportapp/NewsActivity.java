package com.example.ammei.newsreportapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    /**
     * Adapter for the list of articles being displayed
     */
    private NewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty from No Connectivity or No Results
     */
    private TextView mEmptyStateTextView;

    /**
     * URL for the news data from the Guardian dataset
     */
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?from-date=2016-11-1&to-date=2016-12-18&" +
                    "order-by=newest&show-fields=All&page-size=20&page=1&" +
                    "api-key=fb315fe0-d28f-4287-8d1e-f7d9b4a4114c";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        //Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        //Create a new adapter that takes he list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set the {@link ListView}
        //so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        //Set an item click listeer on the ListView, which sends the intent to a web browser
        //to open a website with more information about the selected news article.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Find the current news article that was clicked on
                News currentNews = mAdapter.getItem(position);
                //Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getURL());
                //Intent constructor which send the actual intent
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                //Initiates the Intent
                startActivity(websiteIntent);
            }
        });

        //Start the AsnycTask to fetch the news data
        NewsAsyncTask task = new NewsAsyncTask();
        task.execute(NEWS_REQUEST_URL);

    }

    /*
     *(@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of news articles in the response
     */
    private class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {

        /*
         *Do in background method performing the actual network request
         */
        @Override
        protected List<News> doInBackground(String... urls) {
            //Don't perform the request if there are no URL's, or the first  URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<News> result = QueryUtils.fetchNewsData(urls[0]);
            return result;

        }

        /*
         *On post execute method receives the data from the doInBackground() method, updating
         *the adapter with a new list of news articles.
         */
        @Override
        protected void onPostExecute(List<News> data) {
            mAdapter.clear();

            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}


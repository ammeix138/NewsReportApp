package com.example.ammei.newsreportapp;

/**
 * Created by ammei on 12/18/2016.
 */

import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from Guardian API.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods.
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and returns a list of {@link News} objects.
     *
     * @param requestUrl
     * @return
     */
    public static List<News> fetchNewsData(String requestUrl) {
        //Created new URL object
        URL url = createURL(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making Http request");
        }

        //Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> news = extractFeatureFromJson(jsonResponse);
        //Returns a list of news articles
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     *
     * @param stringUrl
     * @return
     */
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL");
        }

        return url;
    }

    /*
     *Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        //Actual HTTP request being made
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000/* time in milliseconds*/);
            urlConnection.setConnectTimeout(15000/* time in milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If te request was successful (response code 200)
            //then read the input stream and parse the response
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving your news articles");
        } finally {
            if (urlConnection != null) {
                inputStream.close();
            }
            if (inputStream != null) {
                //Closing the input stream could throw an IOException, which is why
                //the makeHttpRequest(URL url) method signature specifies than an IOException
                //could be thrown.
                inputStream.close();
            }

            return jsonResponse;
        }
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<News> extractFeatureFromJson(String newsJson) {
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // Create an empty ArrayList
        List<News> news = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // build up a list of News objects with the corresponding data.
            JSONObject baseJsonResponse = new JSONObject(newsJson);

            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject ArrayObject = resultsArray.getJSONObject(i);

                String webTitle = ArrayObject.getString("webTitle");
                String sectionName = ArrayObject.getString("sectionName");
                String webPublicationDate = ArrayObject.getString("webPublicationDate");
                String url = ArrayObject.getString("url");

                News results = new News(webTitle, sectionName, webPublicationDate, url);
                news.add(results);


            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of News Articles
        return news;
    }

}

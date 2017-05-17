package com.example.ammei.newsreportapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ammei on 12/14/2016.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news    is the list of news articles
     */
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about individual news articles
     * in the list of news articles searched.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_news_listview, parent, false);
        }

        // Find the news article at the given position in the list of news articles given
        News currentNews = getItem(position);

        //Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        // Display the title of the current news article in that TextView
        titleView.setText(currentNews.getTitle());

        // Find the TextView with view ID section
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        // Display the correct section of the current news article in that TextView
        sectionView.setText(currentNews.getSection());

        // Displays the date for each news entry.
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);

        //Formats the date properly, ie. "May 14th, 2017 11:28:23"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
        Date date = null;
        try {
            date = simpleDateFormat.parse(currentNews.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Taking the Date within each news article and displaying them with the correct
        //formatting
        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
        String finalDate = newDateFormat.format(date);

        dateView.setText(finalDate);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}


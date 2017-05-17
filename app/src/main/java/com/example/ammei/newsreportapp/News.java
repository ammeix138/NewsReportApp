package com.example.ammei.newsreportapp;

/**
 * Created by ammei on 12/14/2016.
 */

public class News {

    /**
     * Title of the news article
     */
    private String mTitle;

    /**
     * Date the news article was published
     */
    private String mDate;

    /**
     * Section the news article is related to
     */
    private String mSection;

    /**
     * Website URL for the news articles
     */
    private String mURL;

    /*
     *Constructs a new {@link News} object
     *
     * @param title, is the title of the news article
     * @param date, is the date in which the article was published
     * @param section, is the section the article is related to
     * @param url, is the websites URL
     */
    public News(String title, String date, String section, String url) {
        mTitle = title;
        mDate = date;
        mSection = section;
        mURL = url;
    }

    /*
     *Return the Title of the news article.
     */
    public String getTitle() {
        return mTitle;
    }

    /*
     *Returns the published date of the news article
     */
    public String getDate() {
        return mDate;
    }

    /*
     *Return the articles related section (ie. "Sports")
     */
    public String getSection() {
        return mSection;
    }

    /*
     *Returns the website URL to return more info on a particular a
     */
    public String getURL() {
        return mURL;
    }

}

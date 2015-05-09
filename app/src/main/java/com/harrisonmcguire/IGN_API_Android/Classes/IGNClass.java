package com.harrisonmcguire.IGN_API_Android.Classes;

/**
 * Created by Harrison on 4/18/2015.
 */

public class IGNClass {

    private String title, duration, urlLink, description, cellCount;

    //public class to use in main activity
    public IGNClass() {
    }

    //class of values to keep all the relevant data
    public IGNClass(String title, String duration, String urlLink, String description, String cellCount) {
        this.title = title;
        this.duration = duration;
        this.urlLink = urlLink;
        this.description = description;
        this.cellCount = cellCount;
    }

    //functions to set and get strings from the IGNClass
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCellCount() {
        return cellCount;
    }

    public void setCellCount(String cellCount) {
        this.cellCount = cellCount;
    }

}

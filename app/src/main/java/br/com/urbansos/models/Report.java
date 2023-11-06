package br.com.urbansos.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {
    private String title;
    private String description;
    private String date;
    private String image;
    private String latitude;
    private String longitude;
    private String situation;
    private int status;
    private int userID;
    private int cityID;

    public Report(String title, String description, String date, String image, String latitude, String longitude, String situation, int status, int userID, int cityID) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.situation = situation;
        this.status = status;
        this.userID = userID;
        this.cityID = cityID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) { this.date = date; }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getTitle() { return title; }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getSituation() {
        return situation;
    }

    public int getStatus() {
        return status;
    }

    public int getUserID() {
        return userID;
    }

    public int getCityID() {
        return cityID;
    }
}

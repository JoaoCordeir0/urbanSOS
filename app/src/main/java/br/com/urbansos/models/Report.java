package br.com.urbansos.models;

public class Report {
    private String title;
    private String description;
    private String image;
    private String latitude;
    private String longitude;
    private int situation;
    private int userID;
    private int cityID;

    public Report(String title, String description, String image, String latitude, String longitude, int situation, int userID, int cityID) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.situation = situation;
        this.userID = userID;
        this.cityID = cityID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public int getSituation() {
        return situation;
    }

    public int getUserID() {
        return userID;
    }

    public int getCityID() {
        return cityID;
    }
}

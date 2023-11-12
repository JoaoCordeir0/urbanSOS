package br.com.urbansos.models;

public class Notification {
    private String title;
    private String date;

    public Notification(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}

package br.com.urbansos.interfaces;

import android.location.Location;

public interface IGPS {
    public Location getLocation();
    public void stopUsingGPS();
    public String getLatitude();
    public String getLongitude();
    public boolean canGetLocation();
    public void showSettingsAlert();
}

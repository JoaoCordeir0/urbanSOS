package br.com.urbansos.interfaces;

import android.location.Location;

public interface IGPS {
    Location getLocation();
    void stopUsingGPS();
    String getLatitude();
    String getLongitude();
    boolean canGetLocation();
}

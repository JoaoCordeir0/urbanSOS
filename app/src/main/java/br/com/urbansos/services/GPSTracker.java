package br.com.urbansos.services;

import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;

import br.com.urbansos.R;
import br.com.urbansos.interfaces.IGPS;

public class GPSTracker extends Service implements LocationListener, IGPS {
    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // A distância mínima para mudar Atualizações em metros - 10 metros
    private static final long MIN_TIME_BW_UPDATES = 0; // O tempo mínimo entre as atualizações em milissegundos - 1 minute=1000 * 60 * 1
    protected LocationManager locationManager;

    public GPSTracker(Context context)
    {
        this.mContext = context;
        getLocation();
    }

    @Override
    @SuppressLint("MissingPermission")
    public Location getLocation()
    {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // obter o status GPS
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // obter o status da rede
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled && isNetworkEnabled) {
                this.canGetLocation = true;
                // Primeiro localização obter de provedor de rede
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this
                    );
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this
                        );
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            this.canGetLocation = false;
            this.showSettingsAlert();
        }
        return location;
    }

    @Override
    public void stopUsingGPS()
    {
        if(locationManager != null)
        {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public String getLatitude()
    {
        if(location != null)
        {
            latitude = location.getLatitude();
        }
        return String.valueOf(latitude);
    }

    @Override
    public String getLongitude()
    {
        if(location != null)
        {
            longitude = location.getLongitude();
        }
        return String.valueOf(longitude);
    }

    @Override
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    @Override
    public void showSettingsAlert()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.alertDialog));
        alert.setIcon(R.drawable.warning)
             .setTitle("Location access")
             .setMessage("Authorize the app to access your location. Do you want to open the configuration menu?")
             .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
             })
             .setNegativeButton("Cancel", null);
        alert.create().show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            getLocation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

package br.com.urbansos.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import br.com.urbansos.interfaces.IReceiver;

public class LocationReceiver implements IReceiver {

    private Context mContext;
    private AppCompatActivity activity;
    private Boolean status;

    public LocationReceiver(Context mContext, AppCompatActivity activity) {
        this.mContext = mContext;
        this.activity = activity;
        this.checkStatus();
    }

    @Override
    public void checkStatus()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            setStatus(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

            if (!getStatus())
            {
                this.requestPermission();
            }
        }
        else
        {
            setStatus(true);
        }
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean getStatus() {
        return this.status;
    }

    @Override
    public void requestPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1002
            );
        }
    }
}

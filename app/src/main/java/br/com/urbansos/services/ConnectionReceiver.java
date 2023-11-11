package br.com.urbansos.services;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import br.com.urbansos.interfaces.IReceiver;

public class ConnectionReceiver implements IReceiver {
    private Context mContext;
    private AppCompatActivity activity;
    private Boolean status;

    public ConnectionReceiver(Context mContext, AppCompatActivity activity) {
        this.mContext = mContext;
        this.activity = activity;
        this.checkStatus();
    }
    @Override
    public void checkStatus()
    {
        // Pega a conectividade do contexto o qual o metodo foi chamado
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Cria o objeto netInfo que recebe as informacoes da NEtwork
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // Se o objeto for nulo ou nao tem conectividade retorna false
        setStatus((netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable()));
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
    public void requestPermission() { }
}
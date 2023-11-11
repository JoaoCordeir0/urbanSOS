package br.com.urbansos.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import br.com.urbansos.interfaces.IConnection;

public class ConnectionReceiver implements IConnection {
    private Context mContext;
    private Boolean connectionStatus;

    public ConnectionReceiver(Context mContext) {
        this.mContext = mContext;
        this.checkConnection();
    }
    @Override
    public void checkConnection()
    {
        // Pega a conectividade do contexto o qual o metodo foi chamado
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Cria o objeto netInfo que recebe as informacoes da NEtwork
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // Se o objeto for nulo ou nao tem conectividade retorna false
        setConnectionStatus((netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable()));
    }

    @Override
    public void setConnectionStatus(Boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    @Override
    public boolean getConnectionStatus() {
        return this.connectionStatus;
    }
}
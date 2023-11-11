package br.com.urbansos.interfaces;

public interface IConnection {
    void checkConnection();
    void setConnectionStatus(Boolean connectionStatus);
    boolean getConnectionStatus();
}

package br.com.urbansos.interfaces;

public interface IReceiver {
    void checkStatus();
    void setStatus(boolean status);
    boolean getStatus();
    void requestPermission();
}

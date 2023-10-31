package br.com.urbansos.models;

import org.json.JSONException;
import org.json.JSONObject;

public interface IVolleyCallback{
    void onSuccess(JSONObject response) throws JSONException;
    void onError(String err);
}
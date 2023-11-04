package br.com.urbansos.interfaces;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public interface IRequests {
    JsonObjectRequest sendRequestPOST(String path, JSONObject jsonObject, IVolleyCallback callback) throws UnsupportedOperationException;
    JsonArrayRequest sendRequestGET(String path, IVolleyCallback callback, String token, String type) throws UnsupportedOperationException;
}

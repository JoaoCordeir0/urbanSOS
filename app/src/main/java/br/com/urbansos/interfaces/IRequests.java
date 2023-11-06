package br.com.urbansos.interfaces;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Map;

public interface IRequests {
    JsonObjectRequest sendRequestPOST(String path, JSONObject jsonObject, IVolleyCallback callback) throws UnsupportedOperationException;
    JsonArrayRequest sendRequestGET(String path, IVolleyCallback callback, String token, String type) throws UnsupportedOperationException;
    StringRequest sendRequestPUT(String path, Map params, String token) throws UnsupportedOperationException;
}

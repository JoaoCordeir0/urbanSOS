package br.com.urbansos.models;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class HttpRequests {
    private String urlApi = "https://api.urbansos.com.br";

    public JsonObjectRequest sendRequestPost(String path, JSONObject jsonObject, IVolleyCallback callback) throws JSONException, UnsupportedOperationException {
        final String requestBody = jsonObject.toString();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                urlApi + path,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString());
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        request.setTag("postRequest");
        return request;
    }
}

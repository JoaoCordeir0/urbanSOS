package br.com.urbansos.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import br.com.urbansos.Main;
import br.com.urbansos.interfaces.IRequests;
import br.com.urbansos.interfaces.IVolleyCallback;

public class Volley implements IRequests {
    @Override
    public JsonObjectRequest sendRequestPOST(String path, JSONObject jsonObject, IVolleyCallback callback) throws UnsupportedOperationException
    {
        final String requestBody = jsonObject.toString();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Main.urlApi + path,
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
                    public void onErrorResponse(VolleyError err) {
                        try {
                            JSONObject response = new JSONObject();
                            response.put("message", "App internal error!");
                            callback.onError(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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

    @Override
    public JsonArrayRequest sendRequestGET(String path, IVolleyCallback callback, String token, String type) throws UnsupportedOperationException
    {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Main.urlApi + path,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsonObject = new JSONObject();
                        for (int c = 0; c < response.length(); c++)
                        {
                            try {
                                jsonObject.put(type + c, response.getJSONObject(c));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            callback.onSuccess(jsonObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            JSONObject response = new JSONObject();
                            response.put("message", "App internal error!");
                            callback.onError(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-type", "application/application/json; charset=UTF-8");
                headers.put("Accept", "application/json; charset=UTF-8");
                headers.put("x-access-token", token);
                return headers;
            }
        };

        request.setTag("getRequest");
        return request;
    }

    public StringRequest sendRequestPUT(String path, Map params, String token)
    {
        StringRequest request = new StringRequest(
                Request.Method.PUT,
                Main.urlApi + path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Nenhuma ação de retorno necessária
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        // Nenhuma ação de retorno necessária
                        System.out.println(err);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-access-token", token);
                return headers;
            }
        };

        request.setTag("putRequest");
        return request;
    }
}

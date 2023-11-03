package br.com.urbansos.http;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import br.com.urbansos.interfaces.IVolleyCallback;

public class Requests {
    private String urlApi = "https://api.urbansos.com.br";

    public JsonObjectRequest sendRequestPost(String path, JSONObject jsonObject, IVolleyCallback callback) throws UnsupportedOperationException
    {
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
                    public void onErrorResponse(VolleyError err) {
                        try {
                            JSONObject response = new JSONObject();
                            response.put("message", "Internal error!");
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
}

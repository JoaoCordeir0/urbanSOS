package br.com.urbansos.models;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class Requests {
    private String urlApi = "https://api.urbansos.com.br";

    public void sendRequestLogin(User user, RequestQueue requestQueue) throws JSONException, UnsupportedOperationException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", user.getUsername());
        jsonObject.put("password", user.getPassword());

        final String requestBody = jsonObject.toString();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                urlApi + "/user/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        request.setTag("postLoginRequest");
        requestQueue.add(request);
    }
}

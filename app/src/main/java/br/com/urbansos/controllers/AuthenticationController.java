package br.com.urbansos.controllers;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.urbansos.models.IVolleyCallback;
import br.com.urbansos.models.HttpRequests;
import br.com.urbansos.models.User;

public class AuthenticationController {
    private static String token;
    private static boolean statusLogin;

    public static boolean validateLogin(User user, RequestQueue requestQueue) {
        statusLogin = false;
        HttpRequests req = new HttpRequests();
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("password", user.getPassword());
            requestQueue.add(req.sendRequestPost("/user/login", jsonObject, new IVolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) throws JSONException {
                    String message = response.getString("message");
                    if (message.equals("Login success!")) {
                        token = response.getString("access_token");
                        statusLogin = true;
                    }
                }
                @Override
                public void onError(String err) {
                    System.out.println(err);
                }
            }));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return statusLogin;
    }

}

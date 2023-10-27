package br.com.urbansos.controllers;

import com.android.volley.RequestQueue;

import org.json.JSONException;

import br.com.urbansos.models.Requests;
import br.com.urbansos.models.User;

public class Login {

    public static boolean validateLogin(User user, RequestQueue requestQueue) {
        Requests req = new Requests();
        try
        {
            req.sendRequestLogin(user, requestQueue);
            return true;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}

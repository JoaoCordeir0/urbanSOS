package br.com.urbansos.functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.ContextThemeWrapper;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.urbansos.Main;
import br.com.urbansos.R;

public class Functions {
    public static JSONObject getParamsLogin(String username, String password) throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("password", password);
        return data;
    }

    public static JSONObject getParamsRegister(String name, String email, String cpf, String password) throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("email", email);
        data.put("cpf", cpf);
        data.put("password", password);
        data.put("status", 1); // Cadastra usuários ativos
        return data;
    }

    public static JSONObject getParamsRecoverPassword(String email) throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("email", email);
        return data;
    }

    public static Map<String, String> getParamsReportRegister(String image, String title, String description, String latitude, String longitude, String situation, String userId, String cityId, String status) throws JSONException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("image", image);
        params.put("title", title);
        params.put("description", description);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("situation", situation);
        params.put("userId", userId);
        params.put("cityId", cityId);
        params.put("status", status);

        return params;
    }

    public static boolean validateLogin(JSONObject response, Boolean rememberme) throws JSONException
    {
        JSONObject user = response.getJSONObject("user");

        if (user.getString("status").equals("1"))
        {
            // Salva a autenticação do usuário em cache caso o usuário escolher ser lembrado
            Functions.setCachedAuth(response, user, rememberme);

            return true;
        }
        return false;
    }

    public static boolean verifyCachedAuth() throws ParseException, Exception {

        String token = Main.prefsAuth.getString("token", null);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Date d1 = df.parse(Main.prefsAuth.getString("tokenDate", null));
        Date d2 = df.parse(Functions.getDate());

        long dt = (d2.getTime() - d1.getTime());
        long days = dt / 86400000L;

        // Autenticação valida caso exista o token e esse token tenha igual ou menos que 15 dias
        if (token.length() > 1 && days <= 15) {
            return true;
        }
        return false;
    }

    public static void setCachedAuth(JSONObject response, JSONObject user, boolean rememberme) throws JSONException
    {
        Functions.cleanCachedAuth();

        SharedPreferences.Editor editor = Main.prefsAuth.edit();

        if (rememberme)
            editor.putString("tokenDate", Functions.getDate());

        editor.putString("token", response.getString("access_token"));
        editor.putString("UserID", user.getString("id"));
        editor.putString("UserName", user.getString("name"));
        editor.putString("UserEmail", user.getString("email"));
        editor.putString("UserCpf", user.getString("cpf"));

        editor.apply();
    }

    public static JSONObject getCachedAuth() throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("token", Main.prefsAuth.getString("token", null));
        data.put("id", Main.prefsAuth.getString("UserID", null));
        data.put("name", Main.prefsAuth.getString("UserName", null));
        data.put("email", Main.prefsAuth.getString("UserEmail", null));
        data.put("cpf", Main.prefsAuth.getString("UserCPF", null));

        return data;
    }

    public static void cleanCachedAuth()
    {
        SharedPreferences.Editor editor = Main.prefsAuth.edit();
        editor.clear();
        editor.commit();
    }

    public static void setCachedPhoto(String path)
    {
        Functions.cleanCachedPhoto();
        SharedPreferences.Editor editor = Main.prefsPhoto.edit();
        editor.putString("PathPhoto", path);
        editor.apply();
    }

    public static JSONObject getCachedPhoto() throws JSONException
    {
        String path_photo = Main.prefsPhoto.getString("PathPhoto", null);
        String[] photo_array = path_photo.split("/");
        String photo = photo_array[photo_array.length - 1];

        JSONObject data = new JSONObject();
        data.put("PathPhoto", path_photo);
        data.put("Photo", photo);

        return data;
    }

    public static void cleanCachedPhoto()
    {
        SharedPreferences.Editor editor = Main.prefsPhoto.edit();
        editor.clear();
        editor.commit();
    }

    public static void setCachedLocation(String latitude, String longitude, String city, String address)
    {
        Functions.cleanCachedLocation();

        SharedPreferences.Editor editor = Main.prefsLocation.edit();
        editor.putString("Latitude", latitude);
        editor.putString("Longitude", longitude);
        editor.putString("CityID", city);
        editor.putString("Address", address);

        editor.apply();
    }

    public static JSONObject getCachedLocation() throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("latitude", Main.prefsLocation.getString("Latitude", null));
        data.put("longitude", Main.prefsLocation.getString("Longitude", null));
        data.put("cityId", Main.prefsLocation.getString("CityID", null));
        data.put("adress", Main.prefsLocation.getString("Address", null));

        return data;
    }

    public static void cleanCachedLocation()
    {
        SharedPreferences.Editor editor = Main.prefsPhoto.edit();
        editor.clear();
        editor.commit();
    }

    public static String getDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dtf.format(LocalDateTime.now());
    }

    public static void alert(Context context, String title, String message, String btnText, Boolean cancelable)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.alertDialog));
        alert.setIcon(title.equals("Successfully") ? R.drawable.success : R.drawable.warning)
             .setTitle(title)
             .setMessage("\n" + message + "\n")
             .setCancelable(cancelable)
             .setNegativeButton(btnText, null);
        alert.create().show();
    }

    public static Boolean validateCPF(String cpf)
    {
        cpf = Functions.removeSpecialCharacters(cpf);

        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (cpf.equals("00000000000") ||
            cpf.equals("11111111111") ||
            cpf.equals("22222222222") ||
            cpf.equals("33333333333") ||
            cpf.equals("44444444444") ||
            cpf.equals("55555555555") ||
            cpf.equals("66666666666") ||
            cpf.equals("77777777777") ||
            cpf.equals("88888888888") ||
            cpf.equals("99999999999") ||
            (cpf.length() != 11))
            return false;

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48);

            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);

            if ((dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10)))
                return true;
            else
                return false;
        } catch (InputMismatchException err) {
            return false;
        }
    }

    public static String removeSpecialCharacters(String doc)
    {
        if (doc.contains(".")) {
            doc = doc.replace(".", "");
        }
        if (doc.contains("-")) {
            doc = doc.replace("-", "");
        }
        return doc;
    }

    public static boolean validateEmail(String email)
    {
        String expression = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static void browseTo(Context context)
    {
        String url = null;
        try
        {
            url = "https://urbansos.com.br/user/" + (Functions.getCachedAuth()).getString("id");
        }
        catch (JSONException e) { throw new RuntimeException(e); }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}

package br.com.urbansos.functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean validateLogin(JSONObject response, SharedPreferences prefsAuth, Boolean rememberme) throws JSONException
    {
        JSONObject user = response.getJSONObject("user");

        if (user.getString("status").equals("1"))
        {
            // Salva a autenticação do usuário em cache caso o usuário escolher ser lembrado
            if (rememberme)
                Functions.setCachedAuth(prefsAuth, response, user);

            return true;
        }
        return false;
    }

    public static boolean verifyCachedAuth(SharedPreferences prefsAuth) throws ParseException {
        String token = prefsAuth.getString("token", null);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Date d1 = df.parse (prefsAuth.getString("tokenDate", null));
        Date d2 = df.parse (Functions.getDate());

        long dt = (d2.getTime() - d1.getTime());
        long days = dt / 86400000L;

        // Autenticação valida caso exista o token e esse token tenha igual ou menos que 15 dias
        if (token.length() > 1 && days <= 15)
        {
            return true;
        }
        return false;
    }

    public static void setCachedAuth(SharedPreferences prefsAuth, JSONObject response, JSONObject user) throws JSONException
    {
        SharedPreferences.Editor editor = prefsAuth.edit();
        editor.putString("token", response.getString("access_token"));
        editor.putString("tokenDate", Functions.getDate());
        editor.putString("UserID", user.getString("id"));
        editor.putString("UserName", user.getString("name"));
        editor.putString("UserEmail", user.getString("email"));
        editor.putString("UserCpf", user.getString("cpf"));
        editor.apply();
    }

    public static void cleanCachedAuth(SharedPreferences prefsAuth)
    {
        SharedPreferences.Editor editor = prefsAuth.edit();
        editor.clear();
        editor.commit();
    }

    public static JSONObject getCachedAuth(SharedPreferences prefsAuth) throws JSONException
    {
        String token = prefsAuth.getString("token", null);
        String id = prefsAuth.getString("UserID", null);
        String name = prefsAuth.getString("UserName", null);
        String email = prefsAuth.getString("UserEmail", null);
        String cpf = prefsAuth.getString("UserCPF", null);

        JSONObject data = new JSONObject();
        data.put("token", token);
        data.put("id", id);
        data.put("name", name);
        data.put("email", email);
        data.put("cpf", cpf);

        return data;
    }

    public static String getDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dtf.format(LocalDateTime.now());
    }

    public static void alert(Context context, String title, String message, String btnText, Boolean cancelable)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.alertDialog));
        alert.setIcon(R.drawable.warning)
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

    public static String removeSpecialCharacters(String doc) {
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
}
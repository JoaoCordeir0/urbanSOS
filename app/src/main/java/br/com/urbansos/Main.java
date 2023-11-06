package br.com.urbansos;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.Manifest;

import com.android.volley.RequestQueue;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.urbansos.functions.Functions;
import br.com.urbansos.fragments.CameraFragment;
import br.com.urbansos.fragments.HomeFragment;
import br.com.urbansos.fragments.NotificationFragment;
import br.com.urbansos.fragments.SettingsFragment;
import br.com.urbansos.http.Volley;
import br.com.urbansos.interfaces.IVolleyCallback;
import br.com.urbansos.models.GPSTracker;

public class Main extends AppCompatActivity {
    public static String urlApi = "https://api.urbansos.com.br";
    public static RequestQueue requestQueue;
    public static SharedPreferences prefsAuth;
    public static SharedPreferences prefsPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preloader);

        // Instância singleton do Volley
        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(getApplicationContext());

        // Atribuição do cache de autenticação na constante prefsAuth
        prefsAuth = getSharedPreferences(getString(R.string.preferences_file_auth), Context.MODE_PRIVATE);

        // Atribuição do cache de reports. Armazena o path das imagens que são tiradas para posteriormente enviar para o AWS
        prefsPhoto = getSharedPreferences(getString(R.string.preferences_file_photo), Context.MODE_PRIVATE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    // Checa se o usuário possuí cache de autenticação
                    if (Functions.verifyCachedAuth())
                    {
                        screenMain(new View(getApplicationContext()));
                    }
                }
                catch (Exception ex)
                {
                    screenLogin(new View(getApplicationContext()));
                }
            }
        }, 1000);
    }

    public void screenMain(View view) throws JSONException
    {
        setContentView(R.layout.main);

        // Responsável por identificar os cliques nos itens da navbar e carregar o fragmento da página
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        setFragment(new HomeFragment(), "My Reports");
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.fragment_home)
                {
                    setFragment(new HomeFragment(), "My Reports");
                    return true;
                }
                else if (id == R.id.fragment_camera)
                {
                    setFragment(new CameraFragment(), "Camera");
                    return true;
                }
                else if (id == R.id.fragment_settings)
                {
                    setFragment(new SettingsFragment(), "Settings");
                    return true;
                }
                return false;
            }
        });

        // Responsável por identificar os cliques na top app bar
        Toolbar topNav = findViewById(R.id.topAppBar);
        topNav.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.fragment_notification)
                {
                    setFragment(new NotificationFragment(), "Notifications");
                    return true;
                }
                else if (id == R.id.action_account)
                {
                    browseTo(view);
                }
                return false;
            }
        });
    }

    public void screenRecoverPassword(View view) { setContentView(R.layout.recoverpassword); }
    public void screenLogin(View view) { setContentView(R.layout.login); }
    public void screenSignup(View view) { setContentView(R.layout.signup); }

    void setFragment(Fragment fragment, String title)
    {
        // Seta o titulo da página
        ((MaterialToolbar) findViewById(R.id.topAppBar)).setTitle(title);

        // Troca o fragmento
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void authLogin(View view) throws Exception
    {
        Button btnLogin = findViewById(R.id.btn_login_1);
        CircularProgressIndicator progressIndicator = findViewById(R.id.progressindicator_login);

        String username = String.valueOf(((TextInputLayout) findViewById(R.id.input_username)).getEditText().getText());
        String password = String.valueOf(((TextInputLayout) findViewById(R.id.input_password)).getEditText().getText());
        Boolean rememberme = ((CheckBox) findViewById(R.id.ckeckbox_rememberme)).isChecked();

        if (username.equals("") || password.equals(""))
        {
            Functions.alert(Main.this, "Warning", "Fill in all the information!", "Try again",true);
            return;
        }

        btnLogin.setVisibility(View.INVISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);

        // Dispara a requisição do login
        requestQueue.add((new Volley()).sendRequestPOST("/user/login", Functions.getParamsLogin(username, password), new IVolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException
            {
                try
                {
                    if (Functions.validateLogin(response, rememberme))
                        screenMain(view);
                }
                catch (Exception e)
                {
                    progressIndicator.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    Functions.alert(Main.this, "Warning", response.getString("message"), "Try again",true);
                }
            }
            @Override
            public void onError(JSONObject response) throws JSONException {
                Functions.alert(Main.this, "Error", response.getString("message"), "Try again",true);
            }
        }));
    }

    public void authSignup(View view) throws Exception
    {
        Button btnSignup = findViewById(R.id.btn_singup_1);
        CircularProgressIndicator progressIndicator = findViewById(R.id.progressindicator_signup);

        String name = String.valueOf(((TextInputLayout) findViewById(R.id.input_name)).getEditText().getText());
        String email = String.valueOf(((TextInputLayout) findViewById(R.id.input_email)).getEditText().getText());
        String cpf = String.valueOf(((TextInputLayout) findViewById(R.id.input_cpf)).getEditText().getText());
        String password = String.valueOf(((TextInputLayout) findViewById(R.id.input_password_singup)).getEditText().getText());
        String password_confirm = String.valueOf(((TextInputLayout) findViewById(R.id.input_password_singup_confirm)).getEditText().getText());

        // Verifica se todos os campos foram preenchidos
        if (name.equals("") || email.equals("") || cpf.equals("") || password.equals("") || password_confirm.equals(""))
        {
            Functions.alert(Main.this, "Warning", "Fill in all the information!", "Try again",true);
            return;
        }

        // Verifica se o E-mail é valido
        if (!Functions.validateEmail(email))
        {
            Functions.alert(Main.this, "Warning", "Invalid email!", "Try again",true);
            return;
        }

        // Verifica se o CPF é valido
        if (!Functions.validateCPF(cpf))
        {
            Functions.alert(Main.this, "Warning", "Invalid CPF!", "Try again",true);
            return;
        }

        // Verifica se as senhas estão iguais
        if (!(password.equals(password_confirm)))
        {
            Functions.alert(Main.this, "Warning", "Passwords don't match!", "Try again",true);
            return;
        }

        btnSignup.setVisibility(View.INVISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);

        // Dispara a requisição na api para cadastrar o usuário
        requestQueue.add((new Volley()).sendRequestPOST("/user/register", Functions.getParamsRegister(name, email, cpf, password), new IVolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException
            {
                btnSignup.setVisibility(View.VISIBLE);
                progressIndicator.setVisibility(View.INVISIBLE);

                if ((response.getString("message").indexOf("successfully")) > 0)
                {
                    Functions.alert(Main.this, "Successfully", response.getString("message"), "Ok",true);
                    screenLogin(view);
                }
                else
                {
                    Functions.alert(Main.this, "Warning", response.getString("message"), "Ok",true);
                }
            }
            @Override
            public void onError(JSONObject response) throws JSONException {
                btnSignup.setVisibility(View.VISIBLE);
                progressIndicator.setVisibility(View.INVISIBLE);
                Functions.alert(Main.this, "Error", response.getString("message"), "Try again",true);
            }
        }));
    }

    public void recoverPassword(View view) throws JSONException
    {
        Button btnRecover = findViewById(R.id.btn_recoverpassword);
        CircularProgressIndicator progressIndicator = findViewById(R.id.progressindicator_recoverpassword);

        String email = String.valueOf(((TextInputLayout) findViewById(R.id.input_email_recoverpassword)).getEditText().getText());

        // Verifica se o email foi preechido
        if (email.equals(""))
        {
            Functions.alert(Main.this, "Warning", "Fill in the email!", "Try again",true);
            return;
        }

        // Verifica se o E-mail é valido
        if (!Functions.validateEmail(email))
        {
            Functions.alert(Main.this, "Warning", "Invalid email!", "Try again",true);
            return;
        }

        btnRecover.setVisibility(View.INVISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
        requestQueue.add((new Volley()).sendRequestPOST("/user/recoverpassword", Functions.getParamsRecoverPassword(email), new IVolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                btnRecover.setVisibility(View.VISIBLE);
                progressIndicator.setVisibility(View.INVISIBLE);

                if (response.getString("message").equals("Email send!"))
                {
                    screenLogin(view);
                    Functions.alert(Main.this, "Successfully", "Email sent. Use the link sent to recover your password!", "Ok",true);
                }
                else
                {
                    Functions.alert(Main.this, "Warning", response.getString("message"), "Ok",true);
                }
            }
            @Override
            public void onError(JSONObject response) throws JSONException {
                btnRecover.setVisibility(View.VISIBLE);
                progressIndicator.setVisibility(View.INVISIBLE);
                Functions.alert(Main.this, "Error", response.getString("message"), "Try again",true);
            }
        }));
    }

    public void sendReport(View view) throws JSONException
    {
        GPSTracker gps = new GPSTracker(Main.this);

        String image = (Functions.getCachedPhoto()).getString("Photo");
        String title = String.valueOf(((TextInputLayout) findViewById(R.id.input_report_title)).getEditText().getText());
        String description = String.valueOf(((TextInputLayout) findViewById(R.id.input_report_description)).getEditText().getText());
        String situation = String.valueOf(((TextInputLayout) findViewById(R.id.input_select_level)).getEditText().getText());
        String userId = (Functions.getCachedAuth()).getString("id");
        String cityId = "1"; // req to get city
        String status = "0";

        // Verifica se todos os campos foram preenchidos
        if (image.equals("") || title.equals("") || description.equals("") || situation.equals("")) {
            Functions.alert(Main.this, "Warning", "Fill in all the information!", "Try again", true);
            return;
        }

        if (gps.canGetLocation()) // Checa se foi possível recuperar a localização do usuário
        {
            String latitude = gps.getLatitude();
            String longitude = gps.getLongitude();

            requestQueue.add((new Volley()).sendRequestPUT( // Envia em segundo plano a requisição de um novo reporte
                    "/report/register",
                    Functions.getParamsReportRegister(image, title, description, latitude, longitude, situation, userId, cityId, status),
                    (Functions.getCachedAuth()).getString("token")
            ));

            Functions.alert(Main.this, "Successfully", "We are sending your report. Thank you for helping maintain the city!", "Ok", true);

            setFragment(new HomeFragment(), "My Reports");
        }
    }

    public void browseTo(View view)
    {
        String url = null;
        try
        {
            url = "https://urbansos.com.br/user/" + (Functions.getCachedAuth()).getString("id");
        }
        catch (JSONException e) { throw new RuntimeException(e); }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void logout(View view)
    {
        Functions.cleanCachedAuth();
        screenLogin(view);
    }
}
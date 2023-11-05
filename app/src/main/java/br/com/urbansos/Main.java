package br.com.urbansos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.urbansos.functions.Functions;
import br.com.urbansos.fragments.CameraFragment;
import br.com.urbansos.fragments.HomeFragment;
import br.com.urbansos.fragments.NotificationFragment;
import br.com.urbansos.fragments.SettingsFragment;
import br.com.urbansos.http.Volley;
import br.com.urbansos.interfaces.IVolleyCallback;
import br.com.urbansos.models.Report;
import br.com.urbansos.models.ReportAdapter;

public class Main extends AppCompatActivity {
    public static String urlApi = "https://api.urbansos.com.br";
    public static RequestQueue requestQueue;
    public static SharedPreferences prefsAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preloader);

        // Instância singleton do Volley
        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(getApplicationContext());

        // Atribuição do cache de autenticação na constante prefsAuth
        prefsAuth = getSharedPreferences(getString(R.string.preferences_file_auth), Context.MODE_PRIVATE);

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
                } catch (Exception ex)
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

    public void screenForgotPassword(View view) { setContentView(R.layout.forgotpassword); }
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
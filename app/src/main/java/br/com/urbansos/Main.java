package br.com.urbansos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import br.com.urbansos.fragments.CameraFragment;
import br.com.urbansos.fragments.HomeFragment;
import br.com.urbansos.fragments.NotificationFragment;
import br.com.urbansos.fragments.SettingsFragment;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preloader);

        try
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    setContentView(R.layout.login);
                }
            }, 3000);
        }
        catch (Exception ex) {}
    }

    public void screenForgotPassword(View view) { setContentView(R.layout.forgotpassword); }
    public void screenLogin(View view) { setContentView(R.layout.login); }
    public void screenSignup(View view) { setContentView(R.layout.signup); }
    public void screenMain(View view)
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
                    Toast.makeText(Main.this, "Redirect Browser", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

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
}
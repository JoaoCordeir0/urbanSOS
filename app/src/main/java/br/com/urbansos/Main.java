package br.com.urbansos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import br.com.urbansos.fragments.CameraFragment;
import br.com.urbansos.fragments.HomeFragment;
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

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        setFragment(new HomeFragment());
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.fragment_home)
                {
                    setFragment(new HomeFragment());
                    return true;
                }
                else if (id == R.id.fragment_camera)
                {
                    setFragment(new CameraFragment());
                    return true;
                }
                else if (id == R.id.fragment_settings)
                {
                    setFragment(new SettingsFragment());
                    return true;
                }
                return false;
            }
        });
    }

    void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
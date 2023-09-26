package br.com.urbansos;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

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
    public void screenHome(View view) { setContentView(R.layout.home); }
}
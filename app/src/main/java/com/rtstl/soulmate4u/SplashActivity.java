package com.rtstl.soulmate4u;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        pref = new Preferences(SplashActivity.this);
        System.out.println("is_login : " + (pref.getStringPreference(SplashActivity.this,"is_login")));
        System.out.println("registration_completed : " + (pref.getStringPreference(SplashActivity.this,
                "registration_completed")));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(pref.getStringPreference(SplashActivity.this,"is_login").equalsIgnoreCase("1")
                        && pref.getStringPreference(SplashActivity.this, "registration_completed")
                        .equalsIgnoreCase("1")){
                    Intent mainIntent = new Intent(SplashActivity.this, MapActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else if(pref.getStringPreference(SplashActivity.this,"is_login").equalsIgnoreCase("1")
                        && !pref.getStringPreference(SplashActivity.this, "registration_completed")
                        .equalsIgnoreCase("1")){
                    Intent mainIntent = new Intent(SplashActivity.this, OtpVerificationActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else{
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, 3000);


    }
}

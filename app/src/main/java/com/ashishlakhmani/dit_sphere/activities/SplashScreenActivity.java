package com.ashishlakhmani.dit_sphere.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.ashishlakhmani.dit_sphere.R;

public class SplashScreenActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBar);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                if (sp.contains("id") && sp.contains("password")) {
                    try {
                        Long.parseLong(sp.getString("id",""));
                        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }catch (NumberFormatException e){
                        Intent intent = new Intent(SplashScreenActivity.this, FacultyActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 700);
    }
}

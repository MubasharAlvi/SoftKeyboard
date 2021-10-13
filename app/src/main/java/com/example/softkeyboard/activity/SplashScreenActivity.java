package com.example.softkeyboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.softkeyboard.R;
import com.example.softkeyboard.ads.InterstitialAdsSingleton;
import com.google.firebase.analytics.FirebaseAnalytics;



public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        FirebaseAnalytics.getInstance(this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
                showInterstitial();

            }
        }, SPLASH_TIME_OUT);
    }

    void showInterstitial() {
        if (InterstitialAdsSingleton.getInstance().getAd().isLoaded()) {
            InterstitialAdsSingleton.getInstance().getAd().show();
        }
    }



}


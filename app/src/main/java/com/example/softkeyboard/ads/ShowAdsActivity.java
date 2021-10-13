package com.example.softkeyboard.ads;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class ShowAdsActivity extends Activity {
    public InterstitialAd interstitialAd;
    public AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showInterstitialAd();
        finish();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showInterstitialAd() {
        if (isNetworkConnected()) {
            if (InterstitialAdsSingleton.getInstance().getAd().isLoaded()) {
                InterstitialAdsSingleton.getInstance().getAd().show();
            }


        }

    }

}
package com.example.softkeyboard.ads;

import android.app.Application;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;

public class InstallReferrer extends Application implements InstallReferrerStateListener {
    InstallReferrerClient referrerClient;

    @Override
    public void onCreate() {
        super.onCreate();
        InterstitialAdsSingleton.init(this);

        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(this);


    }

    @Override
    public void onInstallReferrerSetupFinished(int i) {

    }

    @Override
    public void onInstallReferrerServiceDisconnected() {

    }
}

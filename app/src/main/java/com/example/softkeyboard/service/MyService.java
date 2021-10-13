package com.example.softkeyboard.service;

import android.app.IntentService;
import android.content.Intent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.softkeyboard.activity.MainActivity;

import java.util.List;

public class MyService extends IntentService {

    /**
     * Creates an IntentService
     */

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String packageLocal = getPackageName();
        boolean isInputDeviceEnabled = false;
        while (!isInputDeviceEnabled) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

            // check if our keyboard is enabled as input method
            for (InputMethodInfo inputMethod : list) {
                String packageName = inputMethod.getPackageName();
                if (packageName.equals(packageLocal)) {
                    isInputDeviceEnabled = true;
                }
            }
        }
        // open activity
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setAction(MainActivity.ACTION_STARTED_FROM_SERVICE);
        startActivity(newIntent);
        MainActivity.finishActivity();
    }
}
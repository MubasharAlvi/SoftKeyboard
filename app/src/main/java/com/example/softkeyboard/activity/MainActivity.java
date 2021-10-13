package com.example.softkeyboard.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.example.softkeyboard.R;
import com.example.softkeyboard.UncachedInputMethodManagerUtils;
import com.example.softkeyboard.ads.AdaptiveAds;
import com.example.softkeyboard.firebase.Analytics;
import com.example.softkeyboard.ime.SimpleIME;
import com.example.softkeyboard.service.MyService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;


import static android.app.PendingIntent.getActivity;
import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;
import static com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED;
import static com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE;

public class MainActivity extends AppCompatActivity implements InstallReferrerStateListener {

    public static final String ACTION_STARTED_FROM_SERVICE = "action_service";
    public RelativeLayout settingsButton, finishButton;
    private InputMethodChangeReceiver mReceiver;
    private InputMethodManager mImm;
    int setup_step = 1;
    public ImageView mMenu, rate;
    public InstallReferrerClient referrerClient;
    //    public ImageView about;       // removing in Update versionName "1.0.5"
    private static Activity activity;
    public static boolean isenabled_voice = false;
    public SharedPreferences sharedPreferences;
    public AlertDialog alertDialog;
    boolean exit_msg;
    public static final String PREFS_NAME = "amharickeyboard";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 68;
    public ImageView rateUs,imageView;
    public TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRecordAudioPermission();
        }
        Analytics analytics = new Analytics(MainActivity.this);

        analytics.sendScreenAnalytics(this, "MainScreen");


        AdaptiveAds adaptiveAds = new AdaptiveAds(this);
        FrameLayout adContainerView = findViewById(R.id.adContainerView);
        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_id));
        adContainerView.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(adaptiveAds.getAdSize());
        adView.loadAd(adRequest);


        rateUs = findViewById(R.id.rateUs);

        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(this);

        mReceiver = new InputMethodChangeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED);
        registerReceiver(mReceiver, filter);


        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        activity = this;

        ///////////////     setup input and language activity launch        ////////////////////////

        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        settingsButton = findViewById(R.id.setting_button);
        finishButton = findViewById(R.id.finish_button);
        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView);


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rateApp();

            }
        });


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (setup_step == 1) {

                    /////////////////       launch input and settings activity      ////////////////////

                    enableKeyBoard();
                    Intent intent = new Intent(MainActivity.this, MyService.class);     //  to relaunch app on keyboard selection
                    startService(intent);
                } else {

                    /////////////////       launch input method picker dialog      ////////////////////

                    InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    im.showInputMethodPicker();
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //  exit app
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // This method is called when the  permissions are given
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestRecordAudioPermission() {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explai ning why
        // this permission is needed
        if (this.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            Toast.makeText(this, "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{requiredPermission}, 101);
        }


    }

    //////////////////////////////////      to check custom ime enabled or not       /////////////////////////////////

    public boolean isInputMethodEnabled() {

        String id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);

        ComponentName defaultInputMethod = ComponentName.unflattenFromString(id);

        ComponentName myInputMethod = new ComponentName(this, SimpleIME.class);

        return myInputMethod.equals(defaultInputMethod);

    }

//    public void rateApp() {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
//    }


    public void shareApp(String body) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Voice Keyboard ");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void privacyPolicy() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
    }

    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case OK:
                Log.i("Status", "Connection ok");
                break;
            case FEATURE_NOT_SUPPORTED:
                Log.i("Status", "Not Supported");
                break;

            case SERVICE_UNAVAILABLE:
                Log.i("Status", "Unavailable");
                break;
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {

    }


    //////////////////////////      input method change receiver to listen ime changes      //////////////////////////////////////

    public class InputMethodChangeReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_INPUT_METHOD_CHANGED)) {
                if (isInputMethodEnabled()) {
                    setup_step = 3;
                } else {
                    setup_step = 2;
                }
                setScreen();
            }
        }
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(mReceiver);      //  unregister receiver
        super.onDestroy();
    }

    ///////////////////////     launch input and language settings      //////////////////////////////////

    public void enableKeyBoard() {
        startActivityForResult(new Intent("android.settings.INPUT_METHOD_SETTINGS"), 0);
    }

    //////////////////////      to check custom keyboard is added to settings or not        /////////////////////////////////////

    private void checkKeybordExit() {
        if (UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm)) {
            setup_step = 2;
            if (isInputMethodEnabled()) {
                setup_step = 3;
            }
        } else {
            setup_step = 1;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();


//        findViewById(R.id.toolBar_image).setVisibility(View.VISIBLE);

        ////////////////////////        show current setup step     ///////////////////////////

        checkKeybordExit();
        setScreen();
    }


    /////////////////////////      show current setup screen according to enabled/disabled settings     //////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setScreen() {
        switch (setup_step) {
            case 1:
                setupScreen_1();
                break;
            case 2:
                setupScreen_2();
                break;
            case 3:
                setupScreen_3();
                break;
        }
    }

    public static void finishActivity() {
        activity.finish();
    }

    /////////////////      check availability of network connection     //////////////////////

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        exitAlert();
//        super.onBackPressed();
    }

//    public void exitAlert() {
//
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        String message = "";
//        message = "We would like to know your experience, please give us your feedback.";
//        alertDialogBuilder.setTitle("Rate Us");
//        alertDialogBuilder.setMessage(message);
//
//        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int arg1) {
//
//                rateApp();
//
//            }
//        });
//
//        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int arg1) {
//
//                finish();
//            }
//        });
//        alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//
//    }

    ///////////////////////////     setup screen 1       ////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupScreen_1() {
        findViewById(R.id.step_1).setBackground(getResources().getDrawable(R.drawable.selected_page_drawable));
        findViewById(R.id.step_2).setBackground(getResources().getDrawable(R.drawable.unseleted_page_drawable));
        findViewById(R.id.step_3).setBackground(getResources().getDrawable(R.drawable.unseleted_page_drawable));
        ((TextView) findViewById(R.id.instructions_text_top)).setText(R.string.step_1_instructions);
        settingsButton.setBackground(getResources().getDrawable(R.drawable.settings_button));

        ((TextView) findViewById(R.id.setting_button_text)).setText("Settings");
        findViewById(R.id.instructions_text_bottom).setVisibility(View.GONE);
        finishButton.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.settings_image);

    }

    ///////////////////////////     setup screen 2       ////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupScreen_2() {
        findViewById(R.id.step_1).setBackground(getResources().getDrawable(R.drawable.unseleted_page_drawable));
        findViewById(R.id.step_2).setBackground(getResources().getDrawable(R.drawable.selected_page_drawable));
        findViewById(R.id.step_3).setBackground(getResources().getDrawable(R.drawable.unseleted_page_drawable));
        ((TextView) findViewById(R.id.instructions_text_top)).setText(R.string.step_2_instructions);
        settingsButton.setBackground(getResources().getDrawable(R.drawable.enable_keyboard_button));

        ((TextView) findViewById(R.id.setting_button_text)).setText("Activate");
        findViewById(R.id.instructions_text_bottom).setVisibility(View.GONE);
        finishButton.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.enable_image);
        textView.setVisibility(View.VISIBLE);

    }

    ///////////////////////////     setup screen 3       ////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupScreen_3() {
        findViewById(R.id.step_1).setBackground(getResources().getDrawable(R.drawable.unseleted_page_drawable));
        findViewById(R.id.step_2).setBackground(getResources().getDrawable(R.drawable.unseleted_page_drawable));
        findViewById(R.id.step_3).setBackground(getResources().getDrawable(R.drawable.selected_page_drawable));
        ((TextView) findViewById(R.id.instructions_text_top)).setText(R.string.disable_message);
        ((TextView) findViewById(R.id.setting_button_text)).setText("Disable ");
        settingsButton.setBackground(getResources().getDrawable(R.drawable.disable_keyboard));
        findViewById(R.id.instructions_text_bottom).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.instructions_text_bottom)).setText(R.string.step_3_instructions);
        finishButton.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.done_image);
        textView.setVisibility(View.GONE);
    }

    private boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int writeexternalstorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readexternalstorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (writeexternalstorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readexternalstorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void rateApp() {

        try {
            Intent mintent = new Intent(Intent.ACTION_VIEW);
            mintent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(mintent);
        } catch (Exception e1) {
            try {
                Uri uriUrl = Uri.parse("https://market.android.com/details?id=" + getPackageName());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                finish();
            } catch (Exception e2) {
            }
        }
    }
    public void exitAlert() {



        final AlertDialog.Builder dialogNewFolder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        //this is what I did to added the layout to the alert dialog
        View layout = inflater.inflate(R.layout.exist_dialog, null);
        dialogNewFolder.setView(layout);

        AlertDialog alertDialog = dialogNewFolder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

//        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView rateUs = layout.findViewById(R.id.rateUs);
        ImageView existApp = layout.findViewById(R.id.exit);


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rateApp();
            }
        });

        existApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }

}

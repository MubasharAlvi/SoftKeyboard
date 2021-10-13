package com.example.softkeyboard.ime;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


import com.airbnb.lottie.LottieAnimationView;
import com.example.softkeyboard.Translation;
import com.example.softkeyboard.adapter.CustomAdapter;
import com.example.softkeyboard.ads.AdaptiveAds;
import com.example.softkeyboard.ads.AdsActivity;
import com.example.softkeyboard.ads.ShowAdsActivity;
import com.example.softkeyboard.firebase.Analytics;
import com.example.softkeyboard.service.KeyCodes;
import com.example.softkeyboard.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.inputmethodservice.Keyboard.KEYCODE_DONE;
import static android.inputmethodservice.Keyboard.KEYCODE_SHIFT;


@SuppressWarnings("deprecation")
public class SimpleIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, RecognitionListener {

    public KeyboardView kv;
    private int adsCounter = 0;
    public static RelativeLayout parent, parent_layout;
    private Keyboard keyboardEng, numericKeyboard, numericAhmeric, numericShiftKeyboard, teluguKeyboard, teluguShiftKeyboard, symbols_shift_amharic;
    public LinearLayout settingLayout;
    private ConstraintLayout itemsLayout, translator_items_layout, ll;
    private ImageView openImageView, mic, emoji, search, theme, keyBoardBtn, back, backTheme, backSettings, keyBoardBtnFilled, translate_btn, closeBtn, ok_btn, swapBtn;
    private EditText et_text;
    CardView autoCorrectionCV, emojiCV, floatingKeyboardCV, sizeCV;
    CheckBox autoCorrectionCB, emojiCB, floatingKeyboardCB, sizeCB;
    private EmojiconsPopup popupWindow;
    LinearLayout bottomlinearLayout, themesLayout;
    private StringBuilder mComposing = new StringBuilder();
    private LottieAnimationView lottieAnimationView;
    private boolean caps = false;
    public InputConnection ic;
    private boolean mCompletionOn;
    public SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    boolean y = false;
    GridView gridView;
    private String url = "";
    String translatetoText = "";
    String inputCode, outputCode;
    private int spinnnerLeftPosition = 0;
    private String spinnnerLeftName;
    private String spinnnerRightName;
    private int spinnerRightPosition = 0;
    private Spinner leftSpinner, rightSpinner;
    private String[] country;
    private String[] country_code;
    StringBuilder inputStringbuilder = new StringBuilder();
    public String mInputString;
    public String translatedWord;
    public String[] wordsArray;
    public StringBuilder translationStringbuilder;
    public boolean isTranslatebuttonPress = false;
    private TextView textViewLeft;
    boolean whiteSpinnerTextView;
    View popupView;

    public int builderLength;
    public int poss;
    ExtractedText et;
    private boolean isKeyboardAmharic = false;
    private boolean isAmhericToEng = false, isEngToAmheric = false;

    int keyboardThemePos;

    int logos[] = {R.drawable.grey_keyboard, R.drawable.blue_gradiante, R.drawable.black_theme};

    int keyboardbg;
    private int keyboardHeight;
    private boolean isSpeak;

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        isSpeak = false;
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        kv.setVisibility(View.VISIBLE);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        String speechResultText = " " + Objects.requireNonNull(results.getStringArrayList("results_recognition")).get(0);


        String result = matches.get(0);
        Log.d("***result", result + "");
        result.concat("");
        if (speechResultText != null && speechResultText.length() > 0) {
            if (!isSpeak) {
                isSpeak = true;
                if (isEngToAmheric) {

                    amherictoEng(speechResultText, "en-GB", "mr-IN", true);

                } else {
                    ic = getCurrentInputConnection();
                    ic.commitText(speechResultText + " ", 1);
                }
            }

        } else {

            ic = getCurrentInputConnection();
            ic.commitText(result + " ", 1);
        }


//        amherictoEng(result,"en-GB","am-ET" ,true);


        kv.setVisibility(View.VISIBLE);
        bottomlinearLayout.setVisibility(View.GONE);

    }


    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }


    private void setRecognitionListner(String language) {

        if (isInternetOn()) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(this);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            this.mSpeechRecognizerIntent.putExtra("android.speech.extra.LANGUAGE", "en");
            this.mSpeechRecognizerIntent.putExtra("calling_package", getPackageName());


            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        } else {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
            bottomlinearLayout.setVisibility(View.GONE);
            kv.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public View onCreateInputView() {

        parent = (RelativeLayout) getLayoutInflater().inflate(R.layout.keyboard, null);
        isEngToAmheric = false;
        isEngToAmheric = false;
//        isAmhericToEng = false;

        initializeKeyboards();
        allClicks();
        getDataJson();
        spinner_input();
        spinner_output();

//        updateKeys();
        translationStringbuilder = new StringBuilder();





        return parent;
    }

    private void initializeKeyboards() {

        itemsLayout = parent.findViewById(R.id.items_layout);
        kv = parent.findViewById(R.id.keyboard);
        keyboardHeight = kv.getHeight();
        parent_layout = parent.findViewById(R.id.parent_layout);

        bottomlinearLayout = parent.findViewById(R.id.bottom_linear_layout);
        ll = parent.findViewById(R.id.LL);
//        keyboardBlack=parent.findViewById(R.id.keyboardBlack);
//        keyboardBlue=parent.findViewById(R.id.keyboardBlue);

        themesLayout = parent.findViewById(R.id.themesLayout);
        openImageView = parent.findViewById(R.id.openBtn);
        backTheme = parent.findViewById(R.id.backTheme);
        mic = parent.findViewById(R.id.mic);
        emoji = parent.findViewById(R.id.emoji);
        search = parent.findViewById(R.id.googleSearch);
        back = parent.findViewById(R.id.back);
        theme = parent.findViewById(R.id.theme);
        keyBoardBtn = parent.findViewById(R.id.settings);
        keyBoardBtnFilled = parent.findViewById(R.id.settingsFilled);

        translate_btn = parent.findViewById(R.id.translate_btn);
        keyboardEng = new Keyboard(this, R.xml.qwerty_new_keyboard);
        numericKeyboard = new Keyboard(this, R.xml.symbols_new);
        numericShiftKeyboard = new Keyboard(this, R.xml.symbols_shift_new);
        teluguKeyboard = new Keyboard(this, R.xml.qwerty);
        teluguShiftKeyboard = new Keyboard(this, R.xml.qwerty_shift);
        numericAhmeric = new Keyboard(this, R.xml.symbols_new_ahm);
        symbols_shift_amharic = new Keyboard(this, R.xml.symbols_shift_amharic);
        translator_items_layout = parent.findViewById(R.id.translator_items_layout);
        closeBtn = parent.findViewById(R.id.backBtn);
        et_text = parent.findViewById(R.id.et_text);
        ok_btn = parent.findViewById(R.id.ok_btn);
        leftSpinner = parent.findViewById(R.id.leftSpinner);
        rightSpinner = parent.findViewById(R.id.rightSpinner);
        swapBtn = parent.findViewById(R.id.swapBtn);

        kv.setPreviewEnabled(false);

        gridView = parent.findViewById(R.id.simpleGridView);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), logos);
        gridView.setAdapter(customAdapter);
        kv.setKeyboard(teluguKeyboard);
        kv.setOnKeyboardActionListener(this);
        lottieAnimationView = parent.findViewById(R.id.animation);

        SharedPreferences pref = getSharedPreferences("pos", Context.MODE_PRIVATE);
        keyboardbg = pref.getInt("pos", 0);
        if (keyboardbg == 0) {

            firstTheme();
        } else if (keyboardbg == 1) {

            secondTheme();

        } else if (keyboardbg == 2) {
            thirdTheme();


        } else if (keyboardbg == 3) {
            kv.setBackgroundResource(R.drawable.themefour);
        } else if (keyboardbg == 4) {
            kv.setBackgroundResource(R.color.darkblue);
        } else if (keyboardbg == 5) {
            kv.setBackgroundResource(R.color.lightblue);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void allClicks() {

        lottieAnimationView.setAnimation("mic_animation.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentA, View view, int position, long id) {
                SharedPreferences pref = getSharedPreferences("pos", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("pos", position);
                editor.apply();
                keyboardbg = pref.getInt("pos", 0);

                if (keyboardbg == 0) {

                    firstTheme();


                } else if (keyboardbg == 1) {

                    secondTheme();


                } else if (keyboardbg == 2) {


                    thirdTheme();


                } else if (keyboardbg == 3) {
                    kv.setBackgroundResource(R.drawable.themefour);
                    itemsLayout.setBackgroundColor(R.drawable.themefour);
                    translator_items_layout.setBackgroundColor(R.drawable.themefour);

                    openImageView.setBackgroundResource(R.drawable.themefour);
                    themesLayout.setVisibility(View.GONE);
                    kv.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.VISIBLE);
                } else if (keyboardThemePos == 4) {
                    kv.setBackgroundResource(R.color.darkblue);
                    itemsLayout.setBackgroundResource(R.color.darkblue);
                    translator_items_layout.setBackgroundResource(R.color.darkblue);
                    themesLayout.setBackgroundColor(getResources().getColor(R.color.darkblue));
                    themesLayout.setVisibility(View.GONE);
                    kv.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.VISIBLE);
                } else if (keyboardThemePos == 5) {
                    kv.setBackgroundResource(R.color.lightblue);
                    itemsLayout.setBackgroundResource(R.color.lightblue);
                    translator_items_layout.setBackgroundResource(R.color.lightblue);

                    openImageView.setBackgroundResource(R.color.lightblue);
                    themesLayout.setVisibility(View.GONE);
                    kv.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.VISIBLE);
                }

            }
        });

        openImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (itemsLayout.getVisibility() == View.VISIBLE) {
                    openImageView.setImageResource(R.drawable.ic_add_circle_black_24dp);

                    itemsLayout.setVisibility(View.GONE);
                } else {
                    openImageView.setImageResource(R.drawable.ic_back);
                    itemsLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String urlString = "http://www.google.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputStringbuilder.delete(0, inputStringbuilder.length());
                showEmoticons();
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (kv.getVisibility() == View.VISIBLE) {
                    if (isInternetOn()) {
                        setRecognitionListner("en");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        kv.setVisibility(View.GONE);
                        bottomlinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(SimpleIME.this, "no internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    kv.setVisibility(View.VISIBLE);
                    bottomlinearLayout.setVisibility(View.GONE);
                }
            }
        });

        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mSpeechRecognizer.startListening(intent);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomlinearLayout.setVisibility(View.GONE);
                mSpeechRecognizer.stopListening();
                kv.setVisibility(View.VISIBLE);
                isTranslatebuttonPress = false;
            }
        });

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kv.setVisibility(View.GONE);
                ll.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.GONE);
                themesLayout.setVisibility(View.VISIBLE);
            }
        });
        backTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themesLayout.setVisibility(View.GONE);
                openImageView.setImageResource(R.drawable.ic_add_circle_black_24dp);
                ll.setVisibility(View.VISIBLE);
                kv.setVisibility(View.VISIBLE);
            }
        });

        keyBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kv.setKeyboard(keyboardEng);
                keyBoardBtnFilled.setVisibility(View.VISIBLE);
                keyBoardBtn.setVisibility(View.GONE);
                changeEngkeyboardIcons(keyboardEng);
            }
        });

        keyBoardBtnFilled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                kv.setKeyboard(teluguKeyboard);
                keyBoardBtnFilled.setVisibility(View.GONE);
                keyBoardBtn.setVisibility(View.VISIBLE);
                changeEngkeyboardIcons(teluguKeyboard);
            }
        });

        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                translator_items_layout.setVisibility(View.VISIBLE);
                openImageView.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.GONE);
                isTranslatebuttonPress = true;
//                et_text.setFocusable(true);
                et_text.requestFocus();

                et_text.requestFocus(et_text.getText().toString().length());
                et_text.setFocusable(true);
                et_text.setTextIsSelectable(true);


//                startActivity(new Intent(SimpleIME.this, TranslatorActivity.class));
            }
        });


        closeBtn.setOnClickListener(v -> {
            isTranslatebuttonPress = false;

            itemsLayout.setVisibility(View.VISIBLE);
            translator_items_layout.setVisibility(View.GONE);
            openImageView.setVisibility(View.VISIBLE);
            mComposing = new StringBuilder();
//                kv.setKeyboard(keyboardEng);
            et_text.setText("");
            et_text.setFocusable(false);


        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetOn()) {


                    if (et_text.getText().toString().isEmpty()) {

                        Toast.makeText(SimpleIME.this, "Enter text to translate", Toast.LENGTH_SHORT).show();
                    } else {

                        String aaa = et_text.getText().toString().trim();
                        try {
                            translateText(aaa, inputCode, outputCode);
                            et_text.setText("");
                            mComposing.delete(0, mComposing.length());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    Toast.makeText(SimpleIME.this, "no internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        et_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                et_text.setTextIsSelectable(true);
                et_text.setFocusable(true);
                et_text.requestFocus();
                // et_text.requestFocusFromTouch();
                et_text.setFocusableInTouchMode(true);
                et_text.setCursorVisible(true);
                return false;
            }
        });


        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                leftSpinner.setSelection(spinnerRightPosition);
                rightSpinner.setSelection(spinnnerLeftPosition);

//                if (spinnnerLeftPosition == 1) {
//
//                    kv.setKeyboard(keyboardEng);
//
//                } else if (spinnnerLeftPosition == 14) {
//                    kv.setKeyboard(amhericKeyboard);
//                } else {
//
//                    kv.setKeyboard(keyboardEng);
//                }

            }
        });


    }


    private void thirdTheme() {
        kv = parent.findViewById(R.id.keyboardBlack);
        kv.setKeyboard(teluguKeyboard);
        kv.setPreviewEnabled(true);
        kv.setBackgroundResource(R.drawable.ic_background_black);
        itemsLayout.setBackgroundResource(R.drawable.ic_background_black);
        bottomlinearLayout.setBackgroundResource(R.drawable.ic_background_black);
        translator_items_layout.setBackgroundResource(R.drawable.ic_background_black);
        openImageView.setImageResource(R.drawable.ic_add_circle_black_24dp);
        search.setImageResource(R.drawable.search_white_btn);
        emoji.setImageResource(R.drawable.emoji_white_clr);
        mic.setImageResource(R.drawable.mic_white);
        keyBoardBtn.setImageResource(R.drawable.keyboard_white);
        theme.setImageResource(R.drawable.theme_white_icon);
        translate_btn.setImageResource(R.drawable.translation_btn_white);
        ok_btn.setImageResource(R.drawable.ic_translation_white_btn);
        themesLayout.setVisibility(View.GONE);
        itemsLayout.setVisibility(View.VISIBLE);
        themesLayout.setBackgroundResource(R.drawable.ic_background_black);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);
        ll.setVisibility(View.VISIBLE);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;
        ok_btn.setImageResource(R.drawable.ic_translation_new);
        ok_btn.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        search.setImageResource(R.drawable.ic_search_new);
        search.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        emoji.setImageResource(R.drawable.ic_emojis_new);
        emoji.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        mic.setImageResource(R.drawable.ic_main_mic_green);
        keyBoardBtn.setImageResource(R.drawable.ic_keyboard_new);
        keyBoardBtn.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        theme.setImageResource(R.drawable.ic_themes_new);
        theme.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        translate_btn.setImageResource(R.drawable.ic_translation_new);
        translate_btn.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);


        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(24);
        kv.invalidateKey(34);
        kv.invalidateKey(35);
        kv.invalidateKey(39);

        keys.get(34).icon = getResources().getDrawable(R.drawable.ic_delete_white);
        keys.get(39).icon = getResources().getDrawable(R.drawable.enter_white_clr);
        keys.get(35).icon = getResources().getDrawable(R.drawable.ic_123_white);
        keys.get(24).icon = getResources().getDrawable(R.drawable.ic_shift_white);

        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void secondTheme() {
        kv = parent.findViewById(R.id.keyboardBlue);
        kv.setKeyboard(teluguKeyboard);
        kv.setPreviewEnabled(true);
        kv.setBackgroundResource(R.drawable.keyboard_bg_updated);
        itemsLayout.setBackgroundResource(R.color.itemLayoutbgClr);
        translator_items_layout.setBackgroundResource(R.color.itemLayoutbgClr);
        openImageView.setImageResource(R.drawable.ic_add_circle_black_24dp);
        themesLayout.setVisibility(View.GONE);
        itemsLayout.setVisibility(View.VISIBLE);
        bottomlinearLayout.setBackgroundResource(R.drawable.keyboard_bg_updated);
        themesLayout.setBackgroundResource(R.drawable.keyboard_bg_updated);
        ll.setVisibility(View.VISIBLE);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;
        search.setImageResource(R.drawable.search_white_btn);
        emoji.setImageResource(R.drawable.emoji_white_clr);
        mic.setImageResource(R.drawable.mic_white);
        keyBoardBtn.setImageResource(R.drawable.keyboard_white);
        theme.setImageResource(R.drawable.theme_white_icon);
        translate_btn.setImageResource(R.drawable.translation_btn_white);
        ok_btn.setImageResource(R.drawable.ic_translation_white_btn);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();

        kv.invalidateKey(24);
        kv.invalidateKey(34);
        kv.invalidateKey(35);
        kv.invalidateKey(39);

        keys.get(34).icon = getResources().getDrawable(R.drawable.ic_delete_white);
        keys.get(39).icon = getResources().getDrawable(R.drawable.enter_white_clr);
        keys.get(35).icon = getResources().getDrawable(R.drawable.ic_123_white);
        keys.get(24).icon = getResources().getDrawable(R.drawable.ic_shift_white);


        search.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        ok_btn.setImageResource(R.drawable.ic_translation_new);
        ok_btn.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        search.setImageResource(R.drawable.ic_search_new);
        search.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        emoji.setImageResource(R.drawable.ic_emojis_new);
        emoji.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        mic.setImageResource(R.drawable.ic_main_mic_green);
        keyBoardBtn.setImageResource(R.drawable.ic_keyboard_new);
        keyBoardBtn.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        theme.setImageResource(R.drawable.ic_themes_new);
        theme.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        translate_btn.setImageResource(R.drawable.ic_translation_new);
        translate_btn.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void firstTheme() {

        kv = parent.findViewById(R.id.keyboard);
        kv.setKeyboard(teluguKeyboard);
        kv.setPreviewEnabled(false);
        kv.setBackgroundResource(R.color.keybg);
        openImageView.setBackgroundColor(getResources().getColor(R.color.keybg));
        openImageView.setImageResource(R.drawable.ic_add_circle_black_24dp);
        openImageView.setBackgroundColor(getResources().getColor(R.color.white));
        themesLayout.setBackgroundResource(R.color.keybg);
        itemsLayout.setBackgroundResource(R.color.keybg);
        itemsLayout.setVisibility(View.VISIBLE);
        bottomlinearLayout.setBackgroundColor(getResources().getColor(R.color.keybg));
        themesLayout.setVisibility(View.GONE);
        translator_items_layout.setBackgroundResource(R.color.keybg);
        kv.setVisibility(View.VISIBLE);
        openImageView.setVisibility(View.VISIBLE);
        ll.setVisibility(View.VISIBLE);

        ok_btn.setImageResource(R.drawable.ic_translation_new);
        ok_btn.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);

        search.setImageResource(R.drawable.ic_search_new);
        search.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);

        emoji.setImageResource(R.drawable.ic_emojis_new);
        emoji.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);

        mic.setImageResource(R.drawable.ic_main_mic_green);
        //ok_btn.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);

        keyBoardBtn.setImageResource(R.drawable.ic_keyboard_new);
        keyBoardBtn.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);

        theme.setImageResource(R.drawable.ic_themes_new);
        theme.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);

        translate_btn.setImageResource(R.drawable.ic_translation_new);
        translate_btn.setColorFilter(ContextCompat.getColor(this, R.color.greenColor), android.graphics.PorterDuff.Mode.SRC_IN);


        whiteSpinnerTextView = false;
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();

        kv.invalidateKey(24);
        kv.invalidateKey(34);
        kv.invalidateKey(35);
        kv.invalidateKey(39);

        keys.get(34).icon = getResources().getDrawable(R.drawable.ic_delete_icon_green);
        keys.get(39).icon = getResources().getDrawable(R.drawable.ic_enter_green);
        keys.get(35).icon = getResources().getDrawable(R.drawable.ic_123_green);
        keys.get(24).icon = getResources().getDrawable(R.drawable.ic_shit_green);

        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        ic = getCurrentInputConnection();
        char code = (char) primaryCode;
        et = ic.getExtractedText(new ExtractedTextRequest(), 0);
        if (et.text.toString().length() == 0) {
            translationStringbuilder.delete(0, translationStringbuilder.length());
        }
        poss = et.startOffset + et.selectionStart;
        mInputString = inputStringbuilder.toString();

        playClick(primaryCode);
        switch (primaryCode) {

            case -135:
                kv.setKeyboard(teluguShiftKeyboard);
                changeEngkeyboardIcons(teluguShiftKeyboard);
                break;
            case -7:
                kv.setKeyboard(teluguKeyboard);
                changeEngkeyboardIcons(teluguKeyboard);
                break;

            case KeyCodes.TOGGLECODEENGLISH:

                ToggleEngAmh(kv, R.drawable.ic_telugu_on, R.drawable.ic_telugu_off);
                break;

            case KeyCodes.AmharicNumericKeyboard:
                kv.setKeyboard(numericAhmeric);
                changenumericAhmaricIcons();

                break;
            case KeyCodes.AmharicNumericShiftKeyboard:
                kv.setKeyboard(symbols_shift_amharic);
                changeAmharicnumericShiftIcons();
                break;
            case KeyCodes.SYMBOLSHIFTAMHBTN:
                kv.setKeyboard(teluguKeyboard);
                break;

            case KeyCodes.NUMERICAMHARICKEYBOARD:
                kv.setKeyboard(numericAhmeric);
                changenumericAhmaricIcons();
                break;

            case KeyCodes.AMHARICKEYBOARD:
                kv.setKeyboard(teluguKeyboard);
                break;

            case KeyCodes.DEL:
                handleBackspace();
                ic.deleteSurroundingText(0, 0);

                if (inputStringbuilder.length() > 0) {
                    String text = inputStringbuilder.toString().substring(0, inputStringbuilder.length() - 1);
                    inputStringbuilder.delete(0, inputStringbuilder.length());
                    inputStringbuilder.append(text);
                    if (inputStringbuilder.length() != 0) {
                        inputStringbuilder.setLength(inputStringbuilder.length() - 1);
                    }
                } else {
                    inputStringbuilder.delete(0, inputStringbuilder.length());
                }

                if (translationStringbuilder.length() > 0) {
                    String text = translationStringbuilder.toString().substring(0, translationStringbuilder.length() - 1);
                    translationStringbuilder.delete(0, translationStringbuilder.length());
                    translationStringbuilder.append(text);
                    if (translationStringbuilder.length() != 0) {
                        translationStringbuilder.setLength(translationStringbuilder.length() - 1);
                    }
                } else {
                    translationStringbuilder.delete(0, translationStringbuilder.length());
                }

                et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                String backspacedText = et.text.toString();
                translationStringbuilder.delete(0, translationStringbuilder.length());
                translationStringbuilder.append(backspacedText);
                poss = et.startOffset + et.selectionStart;
                builderLength = translationStringbuilder.length();


                break;

            case KEYCODE_SHIFT:
                caps = !caps;
                kv.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case KeyCodes.DONE:
//                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));


                keyDownUp(KeyEvent.KEYCODE_ENTER);


                break;
            case KeyCodes.QWERTY:
                kv.setKeyboard(keyboardEng);
//                changenumericIcons();
                break;
            case KeyCodes.numericKeyboard:
                kv.setKeyboard(numericKeyboard);
                changenumericIcons();

                break;

            case KeyCodes.numericShiftKeyboard:
                kv.setKeyboard(numericShiftKeyboard);
                changenumericShiftIcons();
                break;
            case KeyCodes.SPACECODE:
                Log.d("**space", code + "");

                space_new(code);
                break;

            default:

                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }

                if (et_text.isFocusable()) {
                    Log.d("ISFOCUSEd", et_text.isFocusable() + "");
                    mComposing.append(String.valueOf(code)).toString();
                    et_text.setText(mComposing.toString());
                    et_text.setCursorVisible(true);
                    et_text.setSelection(mComposing.length());

                } else {

                    ic.commitText(String.valueOf(code), 1);
                    inputStringbuilder.append(code);

                }
        }
    }

    @Override
    public void onPress(int primaryCode) {
        Log.d("ONPRESS", primaryCode + "");
        if (primaryCode == KeyCodes.SPACECODE || primaryCode == -135 || primaryCode == -7 || primaryCode == -211 || primaryCode == KeyCodes.QWERTY || primaryCode == KeyCodes.SYMBOLSHIFTAMHBTN || primaryCode == KeyCodes.AMHARICKEYBOARD || primaryCode == KeyCodes.NUMERICAMHARICKEYBOARD || primaryCode == KeyCodes.AmharicNumericShiftKeyboard || primaryCode == KeyCodes.DEL || primaryCode == KEYCODE_SHIFT || primaryCode == KeyCodes.numericKeyboard || primaryCode == KeyCodes.DONE || primaryCode == KeyCodes.numericShiftKeyboard || primaryCode == KeyCodes.TOGGLECODEENGLISH) {
            kv.setPreviewEnabled(false);
        } else {
            kv.setPreviewEnabled(true);
        }


    }

    @Override
    public void onRelease(int primaryCode) {
        Log.d("ONRELESE", primaryCode + "");
        kv.setPreviewEnabled(false);


    }

    @Override
    public void onText(CharSequence text) {

        Log.d("885655", text.toString());
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
        kv.setKeyboard(numericKeyboard);

    }

    @Override
    public void swipeRight() {
        kv.setKeyboard(keyboardEng);


    }

    @Override
    public void swipeUp() {
    }


    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case -4:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case -5:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        kv.setPreviewEnabled(false);
        kv.setKeyboard(teluguKeyboard);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);
        bottomlinearLayout.setVisibility(View.GONE);
        kv.setVisibility(View.VISIBLE);
        et_text.setFocusable(false);
        isTranslatebuttonPress = false;
        translator_items_layout.setVisibility(View.GONE);
        openImageView.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.VISIBLE);

        Analytics analytics = new Analytics(SimpleIME.this);

        analytics.sendEventAnalytics("Show Keyboard", "On_start_input");


        try {
            popupWindow.dismiss();
            mComposing.delete(0, mComposing.length());
            et_text.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }


        if(!getCurrentInputEditorInfo().packageName.contains("google")&&!getCurrentInputEditorInfo().packageName.equalsIgnoreCase("com.android.vending")&&!getCurrentInputEditorInfo().packageName.equalsIgnoreCase("com.android.chrome")) {
            parent.findViewById(R.id.mainFrame).setVisibility(View.VISIBLE);
            showAdaptiveAd();
            adsCounter++;
            if (adsCounter == 3) {
                startActivity(new Intent(SimpleIME.this, ShowAdsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                adsCounter = 0;
            }
        }
        else
            parent.findViewById(R.id.mainFrame).setVisibility(View.GONE);

    }
    private  void showAdaptiveAd(){
        AdaptiveAds adaptiveAds = new AdaptiveAds(this);
        FrameLayout adContainerView = parent.findViewById(R.id.adContainerView);
        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_id));
        adContainerView.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.setAdSize(adaptiveAds.getAdSize());
        adView.loadAd(adRequest);
    }
    public void showEmoticons() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.emoji_listview_layout, null);
        popupWindow = new EmojiconsPopup(popupView, this);
        if (layoutInflater != null) {
            popupWindow.setSizeForSoftKeyboard();
            popupWindow.setSize(ViewGroup.LayoutParams.MATCH_PARENT, parent_layout.getHeight());
            popupWindow.showAtLocation(kv, Gravity.BOTTOM, 0, 0);

            // If the text keyboard closes, also dismiss the emoji popup
            popupWindow.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

                @Override
                public void onKeyboardOpen(int keyBoardHeight) {

                    Log.d("keyBOARDHEIGHT", keyBoardHeight + "");

                }

                @Override
                public void onKeyboardClose() {

                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
            popupWindow.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
                @Override
                public void onEmojiconClicked(Emojicon emojicon) {
                    inputStringbuilder.append(emojicon.getEmoji());

                    commitTyped(getCurrentInputConnection());

                }
            });
            popupWindow.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {
                @Override
                public void onEmojiconBackspaceClicked(View v) {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    handleBackspace();
                }
            });
        }
    }

    private void commitTyped(InputConnection inputConnection) {
        if (inputStringbuilder.length() > 0) {
            inputConnection.commitText(inputStringbuilder, inputStringbuilder.length());
            inputStringbuilder.setLength(0);


        }
    }

    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }

    }


    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);


//           getCurrentInputConnection().setComposingText(mComposing, 0);
//
            et_text.setText("");
            et_text.setText(mComposing);
            et_text.setSelection(mComposing.length());
        } else if (length > 0) {
            et_text.setText("");
            mComposing.setLength(0);
            et_text.setSelection(mComposing.length());
            getCurrentInputConnection().commitText("", 0);
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
    }


    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }


    public String translateText(String query, String inputCode, String outputCode)
            throws ExecutionException, InterruptedException {


        url = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=" + inputCode + "&" +
                "tl=" + outputCode +
                "&dt=t&q=" + query.trim().replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8";
//

        OkHttpHandler okHttpHandlers = new OkHttpHandler();
        String responses = okHttpHandlers.execute(url).get();
        return responses;
    }

    private class OkHttpHandler extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String[] objects) {

            Request.Builder builder = new Request.Builder();
            builder.url(objects[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                Log.i("***", "doInBackground: -=>" + response.body().toString());
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONArray jsonArray2 = jsonArray.getJSONArray(0);
                JSONArray jsonArray3 = jsonArray2.getJSONArray(0);

                String data = "";
                data = data + jsonArray3.getString(0);
                Log.i("***", "PARSED Data Response -=>>>" + data);
                // tranlateToText=data;
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Log.i("***", "OkHttpHandler Response -=>>>" + s);
            translatetoText = s;
            ic = getCurrentInputConnection();
            ic.commitText(translatetoText + " ", 1);


        }
    }


    public void spinner_input() {
        try {
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Setting the ArrayAdapter data on the Spinner
            leftSpinner.setAdapter(aa);
            leftSpinner.setSelection(14);

            leftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Object item = parent.getItemAtPosition(pos);

                    if (whiteSpinnerTextView == true) {
                        TextView textViewLeft = (TextView) leftSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.white));
                    } else if (whiteSpinnerTextView == false) {
                        TextView textViewLeft = (TextView) leftSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.black));
                    }

                    inputCode = country_code[pos];
                    spinnnerLeftName = country.toString();
                    spinnnerLeftPosition = pos;
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {

        }
    }

    private void spinner_output() {
        try {
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            rightSpinner.setAdapter(aa);
            rightSpinner.setSelection(56);

            rightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Object item = parent.getItemAtPosition(pos);
                    if (whiteSpinnerTextView == true) {
                        TextView textViewLeft = (TextView) rightSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.white));
                    } else if (whiteSpinnerTextView == false) {
                        TextView textViewLeft = (TextView) rightSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.black));
                    }
                    outputCode = country_code[pos];
                    spinnnerRightName = country.toString();
                    spinnerRightPosition = pos;
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
        }
    }

    void getDataJson() {
        InputStream inputStream = getResources().openRawResource(R.raw.languages);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Text Data", byteArrayOutputStream.toString());
        try {

            JSONArray jsonArray = new JSONArray(byteArrayOutputStream.toString());
            country = new String[jsonArray.length()];
            country_code = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                String name = jObj.getString("name");
                String code = jObj.getString("code");
                Log.d("***name", name);
                country[i] = name;
                spinnnerLeftName = name;
                country_code[i] = code;
            }
            try {

            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isInternetOn() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }


    public void ToggleEngAmh(@NonNull KeyboardView mKeyboardView, int amhermicToggleOnicon, int amhermicToggleOfficon) {

        //Get All keys codes and check in loop
        Keyboard currentKeyboard = mKeyboardView.getKeyboard();
        List<Keyboard.Key> keys = currentKeyboard.getKeys();


        if (isEngToAmheric) {
            mKeyboardView.invalidateKey(29);
            keys.get(29).icon = getResources().getDrawable(amhermicToggleOfficon);
            isEngToAmheric = false;

        } else {
            mKeyboardView.invalidateKey(29);
            keys.get(29).icon = getResources().getDrawable(amhermicToggleOnicon);
            isEngToAmheric = true;
        }
    }


    private void space_new(char code) {
        // mInputString = inputStringbuilder.toString();

//        inputStringbuilder.delete(0, inputStringbuilder.length());

        try {

            if (code == KeyCodes.SPACECODE) {


                if (mInputString != null && mInputString.length() > 0) {

                    if (isAmhericToEng) {
                        mInputString = mInputString.trim();
                        amherictoEng(mInputString, "am-ET", "en-GB", false);
                    } else if (isEngToAmheric) {
                        if (isInternetOn()) {
                            mInputString = mInputString.trim();
                            amherictoEng(mInputString, "en-GB", "mr", false);
                        } else {
                            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
                            if (Character.isLetter(code) && caps) {
                                code = Character.toUpperCase(code);
                            }

                            if (isTranslatebuttonPress) {
                                et_text.setText(mComposing.append(" "));
                                et_text.setSelection(mComposing.length());
                            } else {
                                ic.commitText(String.valueOf(code), 1);
                            }
                        }


                    } else {

                        if (Character.isLetter(code) && caps) {
                            code = Character.toUpperCase(code);
                        }

                        if (isTranslatebuttonPress) {
                            et_text.setText(mComposing.append(" "));
                            et_text.setSelection(mComposing.length());
                        } else {
                            ic.commitText(String.valueOf(code), 1);
                        }

//                            ic.commitText(String.valueOf(code), 1);
                    }

                } else {
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code);
                    }

                    if (isTranslatebuttonPress) {
                        et_text.setText(mComposing.append(" "));
                        et_text.setSelection(mComposing.length());
                    } else {
                        ic.commitText(String.valueOf(code), 1);
                    }


                }


            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SPACEE@@", e.getMessage());
        }
    }


    private void amherictoEng(String getinputtext, String inputLangCode, String outputLangCod, Boolean voice) {

        ic = getCurrentInputConnection();
        inputStringbuilder.delete(0, inputStringbuilder.length());

        if (voice) {
            Translation translationforvoice = new Translation(this);
            translationforvoice.runTranslation(getinputtext, outputLangCod, inputLangCode);
            translationforvoice.setTranslationComplete((translation, language) ->
            {
                ic.commitText(translation, 1);
            });
        } else {
            Translation translation = new Translation(this);
            translation.runTranslation(getinputtext, outputLangCod, inputLangCode);
            translation.setTranslationComplete((translation1, language) ->
            {

                if (translation1 == "0") {
                    Toast.makeText(this, "internet connection error", Toast.LENGTH_SHORT).show();
                } else {
                    int cursorPosition = ic.getTextBeforeCursor(1024, 0).length();
                    ic.deleteSurroundingText(mInputString.length(), 0);
                    int wordLength = translation1.length();  //4
                    ic = getCurrentInputConnection();

                    if (builderLength > poss) {
                        // int cursor = ic.getTextAfterCursor(1024, 0).length();
                        ic.deleteSurroundingText(cursorPosition, 0);
                        Log.i("Committed", "Before insertion " + translationStringbuilder.toString());
                        translationStringbuilder.insert(poss - (wordLength), translation1 + " ");
                        Log.i("Committed", "After Committed text " + translationStringbuilder.toString());
                        String text = translationStringbuilder.toString();
                        String full_text = ic.getExtractedText(new ExtractedTextRequest(), 0).text.toString();
                        //get whole text before the cursor]
                        CharSequence afterCursorText = ic.getTextAfterCursor(full_text.length(), 0);
                        ic.deleteSurroundingText(0, afterCursorText.length());
                        ic.commitText(text, 0);
                    } else {
                        ic.deleteSurroundingText(cursorPosition, 0);
                        translationStringbuilder.append(translation1 + " ");
                        ic.commitText(translationStringbuilder.toString(), 0);
                    }
                    translationStringbuilder.delete(0, translationStringbuilder.length());
                    et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                    String outputText = et.text.toString();
                    translationStringbuilder.append(outputText);
                    poss = et.startOffset + et.selectionStart;
                    builderLength = translationStringbuilder.length();
                    // Toast.makeText(this, "AM-en: " + translation1, Toast.LENGTH_SHORT).show();
                }

            });
        }


    }

    private void updateKeys() {
        List<Keyboard.Key> keysList = kv.getKeyboard().getKeys();
        kv.invalidateKey(0);

        keysList.get(0).icon = getResources().getDrawable(R.drawable.key_tranprent);

        kv.invalidateAllKeys();
    }


    private void setKeyboardIcons(Keyboard mKeyboard, KeyboardView mKeyboardView, int ic_shift, int ic_del,
                                  int ic_num, int ic_abc, int ic_eng_black, int ic_english_white, int ic_amharic_white, int ic_amharic_black_, int ic_enter) {
        if (mKeyboard.equals(keyboardEng)) {

            mKeyboardView.setKeyboard(mKeyboard);
            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
            mKeyboardView.invalidateKey(19);
            mKeyboardView.invalidateKey(27);
            mKeyboardView.invalidateKey(28);
            mKeyboardView.invalidateKey(29);
            mKeyboardView.invalidateKey(32);
            keys.get(19).icon = getResources().getDrawable(ic_shift);
            keys.get(27).icon = getResources().getDrawable(ic_del);
            keys.get(28).icon = getResources().getDrawable(ic_num);
//            keys.get(29).icon = getResources().getDrawable(R.drawable.ic_eng_black_toggleoff);
            keys.get(32).icon = getResources().getDrawable(ic_enter);


            mKeyboardView.invalidateAllKeys();

        } else if (mKeyboard.equals(teluguKeyboard)) {

            mKeyboardView.setKeyboard(mKeyboard);
            List<Keyboard.Key> keys = kv.getKeyboard().getKeys();

            kv.invalidateKey(24);
            kv.invalidateKey(34);
            kv.invalidateKey(35);
            kv.invalidateKey(39);

            keys.get(34).icon = getResources().getDrawable(ic_del);
            keys.get(39).icon = getResources().getDrawable(ic_enter);
            keys.get(35).icon = getResources().getDrawable(ic_num);
            keys.get(24).icon = getResources().getDrawable(ic_shift);

            mKeyboardView.invalidateAllKeys();

        } else if (mKeyboard.equals(teluguShiftKeyboard)) {

            mKeyboardView.setKeyboard(mKeyboard);
            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
            kv.invalidateKey(30);
            kv.invalidateKey(39);
            kv.invalidateKey(40);
            kv.invalidateKey(44);

            keys.get(39).icon = getResources().getDrawable(ic_del);
            keys.get(44).icon = getResources().getDrawable(ic_enter);
            keys.get(40).icon = getResources().getDrawable(ic_num);
            keys.get(30).icon = getResources().getDrawable(ic_shift);

            mKeyboardView.invalidateAllKeys();

        } else if (mKeyboard.equals(numericKeyboard)) {

            mKeyboardView.setKeyboard(mKeyboard);
            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
            mKeyboardView.invalidateKey(20);
            mKeyboardView.invalidateKey(28);
            mKeyboardView.invalidateKey(33);
            keys.get(20).icon = getResources().getDrawable(ic_shift);
            keys.get(28).icon = getResources().getDrawable(ic_del);
            keys.get(33).icon = getResources().getDrawable(ic_enter);

            mKeyboardView.invalidateAllKeys();

        } else if (mKeyboard.equals(numericShiftKeyboard)) {
//
            mKeyboardView.setKeyboard(numericShiftKeyboard);
            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
            mKeyboardView.invalidateKey(20);
            mKeyboardView.invalidateKey(28);
            mKeyboardView.invalidateKey(32);
            keys.get(20).icon = getResources().getDrawable(ic_shift);
            keys.get(28).icon = getResources().getDrawable(ic_del);
            keys.get(32).icon = getResources().getDrawable(ic_enter);

            mKeyboardView.invalidateAllKeys();

        } else if (mKeyboard.equals(numericAhmeric)) {

            mKeyboardView.setKeyboard(numericAhmeric);
            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
            mKeyboardView.invalidateKey(20);
            mKeyboardView.invalidateKey(28);
            mKeyboardView.invalidateKey(33);
            keys.get(20).icon = getResources().getDrawable(ic_shift);
            keys.get(28).icon = getResources().getDrawable(ic_del);
            keys.get(33).icon = getResources().getDrawable(ic_enter);

            mKeyboardView.invalidateAllKeys();

        } else if (mKeyboard.equals(symbols_shift_amharic)) {
//
            mKeyboardView.setKeyboard(symbols_shift_amharic);
            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
            mKeyboardView.invalidateKey(20);
            mKeyboardView.invalidateKey(28);
            mKeyboardView.invalidateKey(32);
            keys.get(20).icon = getResources().getDrawable(ic_shift);
            keys.get(28).icon = getResources().getDrawable(ic_del);
            keys.get(32).icon = getResources().getDrawable(ic_enter);

            mKeyboardView.invalidateAllKeys();


        }


//            else{
//
//                mKeyboardView.setKeyboard(keyboard_symbols_shift);
//                List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
//                mKeyboardView.invalidateKey(20);
//                mKeyboardView.invalidateKey(28);
//                mKeyboardView.invalidateKey(32);
//                keys.get(20).icon = getResources().getDrawable(ic_shift);
//                keys.get(28).icon = getResources().getDrawable(ic_del);
//                keys.get(32).icon = getResources().getDrawable(ic_enter_green);
//            }
//
//        }else if(mKeyboard.equals(keyboard_hindi_more)){
//
//            mKeyboardView.setKeyboard(mKeyboard);
//            List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
//            mKeyboardView.invalidateKey(37);
//            mKeyboardView.invalidateKey(38);
//            mKeyboardView.invalidateKey(40);
//            mKeyboardView.invalidateKey(43);
//            keys.get(37).icon = getResources().getDrawable(ic_del);
//            keys.get(38).icon = getResources().getDrawable(ic_num);
//            keys.get(40).icon = getResources().getDrawable(ic_abc);
//            keys.get(43).icon = getResources().getDrawable(ic_english_on);
//
//            mKeyboardView.invalidateAllKeys();

    }

    private void changeAmhericIcons() {
        switch (keyboardbg) {
            case 0:
                setKeyboardIcons(teluguKeyboard, kv, 0, R.drawable.ic_delete_icon_green, R.drawable.ic_123_green, 0, 0, 0, 0,
                        0, R.drawable.ic_enter_green);
                break;

            case 1:

            case 2:

                setKeyboardIcons(teluguKeyboard, kv, 0, R.drawable.ic_delete_white, R.drawable.ic_123_white, 0, 0, 0, 0,
                        0, R.drawable.enter_white_clr);
                break;
        }

    }


    private void changeEngkeyboardIcons(Keyboard mkeyboard) {
        switch (keyboardbg) {
            case 0:
                setKeyboardIcons(mkeyboard, kv, R.drawable.ic_shit_green, R.drawable.ic_delete_icon_green, R.drawable.ic_123_green, 0, 0, 0, 0,
                        0, R.drawable.ic_enter_green);
                break;

            case 1:

            case 2:

                setKeyboardIcons(mkeyboard, kv, R.drawable.ic_shift_white, R.drawable.ic_delete_white, R.drawable.ic_123_white, 0, 0, 0, 0,
                        0, R.drawable.enter_white_clr);
                break;
        }

    }


    private void changenumericIcons() {
        switch (keyboardbg) {

            case 0:
                setKeyboardIcons(numericKeyboard, kv, R.drawable.ic_shit_green, R.drawable.ic_delete_icon_green, R.drawable.ic_123_green, 0, 0, 0,
                        0, 0, R.drawable.ic_enter_green);
                break;

            case 1:
                setKeyboardIcons(numericKeyboard, kv, R.drawable.ic_shift_white, R.drawable.ic_delete_white, R.drawable.ic_123_white, 0, 0, 0,
                        0, 0, R.drawable.enter_white_clr);
                break;

            case 2:
                setKeyboardIcons(numericKeyboard, kv, R.drawable.ic_shift_white, R.drawable.ic_delete_white, R.drawable.ic_123_white, 0, 0, 0,
                        0, 0, R.drawable.enter_white_clr);
                break;
        }
    }


    private void changenumericShiftIcons() {
        switch (keyboardbg) {

            case 0:
                setKeyboardIcons(numericShiftKeyboard, kv, R.drawable.ic_shit_green, R.drawable.ic_delete_icon_green, 0, 0, 0, 0,
                        0, 0, R.drawable.ic_enter_green);
                break;

            case 1:

            case 2:
                setKeyboardIcons(numericShiftKeyboard, kv, R.drawable.ic_shift_white, R.drawable.ic_delete_white, 0, 0, 0, 0,
                        0, 0, R.drawable.enter_white_clr);
                break;
        }
    }


    private void changenumericAhmaricIcons() {
        switch (keyboardbg) {

            case 0:
                setKeyboardIcons(numericAhmeric, kv, R.drawable.ic_shit_green, R.drawable.ic_delete_icon_green, R.drawable.ic_123_green, 0, R.drawable.ic_marathi_off, 0,
                        R.drawable.ic_marathi_on, 0, R.drawable.ic_enter_green);
                break;

            case 1:

            case 2:
                setKeyboardIcons(numericAhmeric, kv, R.drawable.ic_shift_white, R.drawable.ic_delete_white, R.drawable.ic_123_white, 0, 0, 0,
                        0, 0, R.drawable.enter_white_clr);
                break;
        }
    }


    private void changeAmharicnumericShiftIcons() {
        switch (keyboardbg) {

            case 0:
                setKeyboardIcons(symbols_shift_amharic, kv, R.drawable.ic_shit_green, R.drawable.ic_delete_icon_green, 0, 0, 0, 0,
                        0, 0, R.drawable.ic_enter_green);
                break;

            case 1:

            case 2:
                setKeyboardIcons(symbols_shift_amharic, kv, R.drawable.ic_shift_white, R.drawable.ic_delete_white, 0, 0, 0, 0,
                        0, 0, R.drawable.enter_white_clr);
                break;
        }
    }


}


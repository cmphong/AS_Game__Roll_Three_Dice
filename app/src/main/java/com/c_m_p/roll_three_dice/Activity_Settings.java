package com.c_m_p.roll_three_dice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.roll_three_dice.Billing_Consumable.Activity_IAP;
import com.c_m_p.roll_three_dice.Cheat.AdapterCheat;
import com.c_m_p.roll_three_dice.Cheat.Cheat;
import com.c_m_p.roll_three_dice.Login.User;
import com.c_m_p.roll_three_dice.MyUtil.MyConstants;
import com.c_m_p.roll_three_dice.MyUtil.MyUtil;
import com.c_m_p.roll_three_dice.MyUtil.Prefs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Activity_Settings extends AppCompatActivity implements TextView.OnEditorActionListener{

    private ImageView iv_ok;
    private ImageView iv_create_new_password;
    private ImageView iv_get_reward;
    public static TextView tv_reward_amount;
    private RecyclerView rv_cheat;
    private FrameLayout fl_banner_ad_container;

    private Dialog dialog                ;
    private EditText edt_new_password      ;
    private EditText edt_again_new_password;
    private EditText edt_password_restore  ;
    private TextView tv_password_isnt_match;
    private Button btn_save_new_password   ;
    private Button btn_cancel_new_password ;
    private LinearLayout ll_dialog_new_password_content,
            pb_dialog_new_password_container;
    private SwitchCompat sw_shake_on_off;


    private Prefs prefs_rewarded, prefs_login, prefs_settings;

    private List<Cheat> listCheats = new ArrayList<>();
    private AdapterCheat adapter;

    private AdView adView;
    private AdRequest adRequest;
    private MyBroadcastReceiver myBroadcastReceiver;
    private boolean isNetworkAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Logd("onCreate()");

        iv_ok                   = findViewById(R.id.iv_ok);
        iv_get_reward           = findViewById(R.id.iv_get_reward);
        iv_create_new_password  = findViewById(R.id.iv_create_new_password);
        tv_reward_amount        = findViewById(R.id.tv_rewarded_amount);
        rv_cheat                = findViewById(R.id.rv_cheat);
        sw_shake_on_off         = findViewById(R.id.sw_shake_on_off);
        fl_banner_ad_container  = findViewById(R.id.fl_ad_view_container);

        prefs_settings  = new Prefs(this, MyConstants.STORE_SETTINGS);
        prefs_rewarded  = new Prefs(this, MyConstants.STORE_REWARDED);
        prefs_login     = new Prefs(this, MyConstants.STORE_LOGIN);


        // [AdMob #1] - initial Ads
        initialAds();


        // START - BOTTOM BANNER AD ================================
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(BuildConfig.AM_UNIT_ID_BANNER_AD);
        fl_banner_ad_container.addView(adView);
        adView.setAdSize(getAdSize());
        myBroadcastReceiver = new MyBroadcastReceiver(new IEventNetwork() {
            @Override
            public void onResultNetworkAvailable(boolean isAvailable) {
                if(isAvailable) loadBanner();
                isNetworkAvailable = isAvailable;
            }
        });
        // END - BOTTOM BANNER AD =========================================

        // [AdMob #2] - Load Rewarded Ad
        // Load ad when activity start
//        loadRewardedAd();

        // [AdMob #3] - show Rewarded Ad ===========================================
        MyUtil.setImageButton(iv_get_reward, R.drawable.img_btn_get_waiting_vi, R.drawable.img_btn_get_waiting_en);
        iv_get_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable) {
                    MyUtil.setImageButton(iv_get_reward, R.drawable.img_btn_get_pressed_vi, R.drawable.img_btn_get_pressed_en);
                    startActivity(new Intent(Activity_Settings.this, Activity_IAP.class));
                }else{
                    showToast(getString(R.string.Please_check_your_internet_connection_and_try_again));
                }
            }
        });

        iv_ok.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        iv_ok.setImageResource(R.drawable.img_btn_ok_pressed);
                        finish();
                        break;
                    case MotionEvent.ACTION_UP:
                        iv_ok.setImageResource(R.drawable.img_btn_ok_waiting);
                        break;
                }

                return true;
            }
        });


        boolean sf_accelerometer_sensor = prefs_settings.getBoolean(MyConstants.ACCELEROMETER_SENSOR, true);
        if(sf_accelerometer_sensor) sw_shake_on_off.setChecked(true);
        else sw_shake_on_off.setChecked(false);
        sw_shake_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    prefs_settings.setBoolean(MyConstants.ACCELEROMETER_SENSOR, true);
                else
                    prefs_settings.setBoolean(MyConstants.ACCELEROMETER_SENSOR, false);

            }
        });


        String login_password = prefs_login.getString(MyConstants.LOGIN_PASSWORD, "");
        if(login_password.length()>0){
            MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_edit_waiting_vi, R.drawable.img_btn_edit_waiting_en);
        }else{
            MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_set_password_waiting_vi, R.drawable.img_btn_set_password_waiting_en);
        }

        iv_create_new_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        if(login_password.length()>0){
                            MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_edit_presssed_vi, R.drawable.img_btn_edit_presssed_en);
                        }else{
                            MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_set_password_pressed_vi, R.drawable.img_btn_set_password_pressed_en);
                        }


                        dialog = new Dialog(Activity_Settings.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_create_new_password);
                        dialog.show();

                        edt_new_password                = dialog.findViewById(R.id.edt_dialog_new_password);
                        edt_again_new_password          = dialog.findViewById(R.id.edt_dialog_again_new_password);
                        edt_password_restore            = dialog.findViewById(R.id.edt_dialog_password_restore);
                        tv_password_isnt_match          = dialog.findViewById(R.id.tv_dialog_password_is_not_match);
                        btn_save_new_password           = dialog.findViewById(R.id.btn_dialog_save_new_password);
                        btn_cancel_new_password         = dialog.findViewById(R.id.btn_dialog_cancel_new_password);
                        ll_dialog_new_password_content  = dialog.findViewById(R.id.ll_dialog_new_password_content);
                        pb_dialog_new_password_container = dialog.findViewById(R.id.pb_dialog_new_password_container);

                        btn_cancel_new_password.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btn_save_new_password.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setNewPassword(iv_create_new_password.getRootView());
                            }
                        });

                        edt_new_password.setOnEditorActionListener(Activity_Settings.this::onEditorAction);
                        edt_again_new_password.setOnEditorActionListener(Activity_Settings.this::onEditorAction);
                        edt_password_restore.setOnEditorActionListener(Activity_Settings.this::onEditorAction);

                        break;

                    case MotionEvent.ACTION_UP:
                        if(login_password.length()>0){
                            MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_edit_waiting_vi, R.drawable.img_btn_edit_waiting_en);
                        }else{
                            MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_set_password_waiting_vi, R.drawable.img_btn_set_password_waiting_en);

                        }

                        break;
                }

                return true;
            }
        });


        // duplicate into onRestart()
        listCheats = new Gson().fromJson(
                prefs_rewarded.getString(MyConstants.LIST_CHEATS, initialList()),
                new TypeToken<List<Cheat>>(){}.getType()
        );
        tv_reward_amount.setText(String.valueOf(listCheats.size()));

        adapter = new AdapterCheat(this);

        adapter.setData(listCheats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        );
        rv_cheat.setLayoutManager(linearLayoutManager);

        rv_cheat.setAdapter(adapter);

    }


    // LIFECYCLE ========================================================
    @Override
    protected void onRestart() {
        super.onRestart();

//        Logd("onRestart()");
        // set text for tv_rewardd_count
        listCheats = new Gson().fromJson(
                prefs_rewarded.getString(MyConstants.LIST_CHEATS, initialList()),
                new TypeToken<List<Cheat>>(){}.getType()
        );
        adapter.setData(listCheats);
        tv_reward_amount.setText(String.valueOf(listCheats.size()));

        // set lại ảnh cho button GET
        MyUtil.setImageButton(iv_get_reward, R.drawable.img_btn_get_waiting_vi, R.drawable.img_btn_get_waiting_en);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Logd("onStart()");

        // REGISTER BROADCAST RECEIVER
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Logd("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Logd("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Logd("onStop()");

        unregisterReceiver(myBroadcastReceiver);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (v.getId()){
            case R.id.edt_dialog_new_password:
            case R.id.edt_dialog_again_new_password:
            case R.id.edt_dialog_password_restore:

                // Hide soft keyboard
                MyUtil.hideSoftKeyboard(Activity_Settings.this, v);

                setNewPassword(iv_create_new_password.getRootView());

                break;
        }

        return true;
    }



    // MY FUNCTION ==============================================
    // START - ADMOB ==============================================
    private void initialAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Mobile Ads init completed we may show Ad now
                Logdln("onCreate(): " +initializationStatus, 408);
            }
        });
    }
    // .... START - AdMob Adaptive Banner ===========================================================
    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        adRequest = new AdRequest.Builder().build();

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Logdln("Called when a click is recorded for an ad.", 367);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Logdln("Called when the user is about to return to the application after clicking on an ad.", 373);

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Logdln("Called when an ad request failed.", 380);
                fl_banner_ad_container.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Logdln("Called when an impression is recorded for an ad.", 386);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Logdln("Called when an ad is received.", 392);
                fl_banner_ad_container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Logdln("Called when an ad opens an overlay that covers the screen.", 399);

            }
        });

    }
    // .... END - AdMob Adaptive Banner ===========================================================
    // END - ADMOB ===========================================================


    private void setNewPassword(View view){
        User user = new User(edt_new_password.getText().toString(), edt_password_restore.getText().toString());


        if(user.getPassword().equals(edt_again_new_password.getText().toString())) {
            if(!user.isValidPassword() || !user.isValidPasswordRestore()){
                tv_password_isnt_match.setVisibility(View.VISIBLE);
                tv_password_isnt_match.setText(R.string.Passwords_must_be_at_least_6_characters);
                return;
            }

            prefs_login.setString(MyConstants.LOGIN_PASSWORD, edt_new_password.getText().toString());
            prefs_login.setString(MyConstants.LOGIN_PASSWORD_RESTORE, edt_password_restore.getText().toString());

            if(tv_password_isnt_match.getVisibility() == View.VISIBLE){
                tv_password_isnt_match.setVisibility(View.INVISIBLE);
            }

            if(edt_new_password.getText().toString().length() > 0) {
                MyUtil.setImageButton(iv_create_new_password, R.drawable.img_btn_edit_waiting_vi, R.drawable.img_btn_edit_waiting_en);
            }

            // show ProgressBar
            new CountDownTimer(3000, 1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                    MyUtil.showSnackBarTopSuccessful(Activity_Settings.this, view, getString(R.string.New_password_set_successfully));

                }
            }.start();

            ll_dialog_new_password_content.setVisibility(View.GONE);
            pb_dialog_new_password_container.setVisibility(View.VISIBLE);


        }else{
            tv_password_isnt_match.setVisibility(View.VISIBLE);
            tv_password_isnt_match.setText(getString(R.string.Password_do_not_match));
        }

    }

    private String initialList(){
        List<Cheat> result = new ArrayList<>();
        result.add(new Cheat());
        result.add(new Cheat());
        return new Gson().toJson(result);
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Settings.java ================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Settings.java - line: " + n + " ================\n" + str);
    }
    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

}
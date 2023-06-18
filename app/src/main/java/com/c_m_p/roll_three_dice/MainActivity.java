package com.c_m_p.roll_three_dice;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.roll_three_dice.Cheat.Cheat;
import com.c_m_p.roll_three_dice.Login.LoginView;
import com.c_m_p.roll_three_dice.Login.PresenterLogin;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Todo<br>
 * <b style="color:#EB2415">{@code [ ]}</b> Khởi động app lâu<br />
 * <b style="color:#EB2415">{@code [ ]}</b> Change Key Real to upload Play Store<br />
 * <b style="color:#EB2415">{@code [ ]}</b> Activity_Purchases_2 gặp Lỗi nạp thêm product khi quay lại màn hình Purchase từ Windows<br />
 * <b style="color:#ffff00">{@code [x]}</b> Activity_Purchases_3 gặp Lỗi nạp product khi lockscreen: sau khi user đã hoàn tất giao dịch thành công<br />
 *      nhấn nút Back => lockscreen => tầm khoản gần 1 phút thì các method sau được gọi:<br />
 *      showProducts() * 4 => posted delayed => 4 number of products ...<br />
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static WeakReference<MainActivity> weakReference;

    ImageView iv_shake;
    ImageView iv_1, iv_2, iv_3,
            iv_plate,
            iv_light;
    RelativeLayout rl_dice;
    LinearLayout ll_dialog_content;
    EditText edt_password;
    TextView tv_forget_password;
    Button btn_exit;
    Button btn_login;
    ProgressBar pb_dialog, pb_settings;

    Random random;

    MediaPlayer mediaPlayer_start,
            mediaPlayer_finish_1,
            mediaPlayer_finish_2,
            mediaPlayer_finish_3;

    Sensor sensor;
    SensorManager sensorManager;
    boolean isAccelerometerSensorAvailable,
            itIsNotFirstTime = false;
    float currentX, currentY, currentZ,
            lastX, lastY, lastZ,
            xDifference, yDifference, zDifference,
            shakeThreshold = 5f;

    VibratorManager vibratorManager;
    Vibrator vibrator;

    Dialog dialog_login;
    TextView tv_incorrect_password;

    Prefs prefs_rewarded;


    // Gesture Drag =========================
    float widthParent, heightParent;
    float xDown = 0, yDown = 0,
            movedX, movedY,
            density;
    boolean isCollision         = false,
            isCollisionTop      = false,
            isCollisionRight    = false,
            isCollisionBottom   = false,
            isCollisionLeft     = false,
            isCollisionTL       = false,
            isCollisionTR       = false,
            isCollisionBL       = false,
            isCollisionBR       = false;
    float collisionTop, collisionRight, collisionBottom, collisionLeft;


    List<Cheat> listCheats = new ArrayList<>();
    List<Cheat> listRunCheat = new ArrayList<>();

    PresenterLogin presenterLogin;

    int count_sensorChange = 0;

    // ADMOB ===========================
    private FrameLayout fl_ad_view_container;
    private AdView adView;
    private AdRequest adRequest;

    MyBroadcastReceiver myBroadcastReceiver;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();

        prefs_rewarded = new Prefs(this, MyConstants.STORE_REWARDED);

        tv_version.setText("v" +BuildConfig.VERSION_NAME);

        // START - AdMob ========================================================
        initialAdMob();
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(BuildConfig.AM_UNIT_ID_BANNER_AD);
        fl_ad_view_container.addView(adView);
        adView.setAdSize(getAdSize());
        myBroadcastReceiver = new MyBroadcastReceiver(new IEventNetwork() {
            @Override
            public void onResultNetworkAvailable(boolean isAvailable) {
                if(isAvailable) loadBanner();
            }
        });
        // END - AdMob ========================================================

        density = getResources().getDisplayMetrics().density;

        random = new Random();
        mediaPlayer_start    = MediaPlayer.create(this, R.raw.spin_prize_wheel_sound_effect);
        mediaPlayer_finish_1 = MediaPlayer.create(this, R.raw.bell_ding);
        mediaPlayer_finish_2 = MediaPlayer.create(this, R.raw.bell_ding);
        mediaPlayer_finish_3 = MediaPlayer.create(this, R.raw.bell_ding);


        // START - SHAKE DEVICE =====================================================
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        }else{
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        }else{
            isAccelerometerSensorAvailable = false;
        }
        // END - SHAKE DEVICE ======================================================

        presenterLogin = new PresenterLogin(new LoginView() {
            @Override
            public void loginFail() {
                // Hide ProgressBar
                tv_incorrect_password.setVisibility(View.VISIBLE);
                ll_dialog_content.setVisibility(View.VISIBLE);
                pb_dialog.setVisibility(View.GONE);
            }

            @Override
            public void loginSuccessful() {
                showToast(getString(R.string.Login_successfully));
                tv_incorrect_password.setVisibility(View.GONE);
//                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, Activity_Settings.class));


            }
        });

        MyUtil.setImageButton(iv_shake, R.drawable.img_btn_shake_waiting_vi, R.drawable.img_btn_shake_waiting_en);
        iv_shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shake();
            }
        });

        /* START - DRAG & DROP ==================================== */
        rl_dice.post(new Runnable() {
            @Override
            public void run() {
                widthParent = rl_dice.getWidth();
                heightParent = rl_dice.getHeight();
            }
        });
        iv_plate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()){
                    // The user just put his finger down on the image
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getX();
                        yDown = event.getY();

                        break;
                    // The user moved his finger
                    case MotionEvent.ACTION_MOVE:
                        movedX = event.getX();
                        movedY = event.getY();

                        // Calculate how much the user moved his finger
                        float distanceX = movedX - xDown;
                        float distanceY = movedY - yDown;


                        checkCollision();


                        if(isCollision){

                            if(isCollisionTL){
                                iv_plate.setX(collisionLeft);
                                iv_plate.setY(collisionTop);
                                if(distanceX > 0) iv_plate.setX(iv_plate.getX() + distanceX);
                                if(distanceY > 0) iv_plate.setY(iv_plate.getY() + distanceY);
                                break;
                            }
                            if(isCollisionTR){
                                iv_plate.setX(collisionRight);
                                iv_plate.setY(collisionTop);
                                if(distanceX < 0) iv_plate.setX(iv_plate.getX() + distanceX);
                                if(distanceY > 0) iv_plate.setY(iv_plate.getY() + distanceY);
                                break;
                            }
                            if(isCollisionBL){
                                iv_plate.setX(collisionLeft);
                                iv_plate.setY(collisionBottom);
                                if(distanceX > 0) iv_plate.setX(iv_plate.getX() + distanceX);
                                if(distanceY < 0) iv_plate.setY(iv_plate.getY() + distanceY);
                                break;
                            }
                            if(isCollisionBR){
                                iv_plate.setX(collisionRight);
                                iv_plate.setY(collisionBottom);
                                if(distanceX < 0) iv_plate.setX(iv_plate.getX() + distanceX);
                                if(distanceY < 0) iv_plate.setY(iv_plate.getY() + distanceY);
                                break;
                            }

                            if(isCollisionLeft){
                                iv_plate.setX(collisionLeft);
                                if(distanceX > 0) iv_plate.setX(iv_plate.getX() + distanceX);
                                iv_plate.setY(iv_plate.getY() + distanceY);
                            }

                            if(isCollisionTop){
                                iv_plate.setY(collisionTop);
                                if(distanceY > 0) iv_plate.setY(iv_plate.getY() + distanceY);
                                iv_plate.setX(iv_plate.getX() + distanceX);
                            }

                            if(isCollisionRight){
                                iv_plate.setX(collisionRight);
                                if(distanceX < 0) iv_plate.setX(iv_plate.getX() + distanceX);
                                iv_plate.setY(iv_plate.getY() + distanceY);
                            }

                            if(isCollisionBottom){
                                iv_plate.setY(collisionBottom);
                                if(distanceY < 0) iv_plate.setY(iv_plate.getY() + distanceY);
                                iv_plate.setX(iv_plate.getX() + distanceX);
                            }

                        }else{
                            iv_plate.setX(iv_plate.getX() +distanceX);
                            iv_plate.setY(iv_plate.getY() +distanceY);
                        }

                        break;
                }
                return true;
            }
        });
        /* END - DRAG & DROP ==================================== */


        iv_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRunCheat.clear();
                Prefs prefs_user = new Prefs(MainActivity.this, MyConstants.STORE_LOGIN);
                String password_login = prefs_user.getString(MyConstants.LOGIN_PASSWORD, "");
                if(password_login.length()>=4) {
                    showDialogLogin();
                }else{
                    showProgressBar();
                    startActivity(new Intent(MainActivity.this, Activity_Settings.class));
                }
            }
        });
    }

    public static MainActivity getMyIntanceActivity(){
        return weakReference.get();
    }
    public static void showSnackBarTopRestorePasswordSuccessful(){
        MainActivity mainActivity = getMyIntanceActivity();
        MyUtil.showSnackBarTopSuccessful(
                mainActivity,
                mainActivity.iv_shake.getRootView(),
                mainActivity.getString(R.string.Your_password_has_been_reset_successfully));
    }

    // START - AdMob Adaptive Banner ===========================================================
    private void initialAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



    }
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
        // Step FINAL - Start loading the ad in the background.
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Logdln("Called when a click is recorded for an ad.", 380);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Logdln("Called when the user is about to return to the application after clicking on an ad.", 386);

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Logdln("Called when an ad request failed.", 393);
                fl_ad_view_container.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Logdln("Called when an impression is recorded for an ad.", 400);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Logdln("Called when an ad is received.", 406);
                fl_ad_view_container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Logdln("Called when an ad opens an overlay that covers the screen.", 413);

            }
        });

        Logdln("adView.isLoading: " +adView.isLoading(), 418);
    }
    // END - AdMob Adaptive Banner ===========================================================


    private void showProgressBar(){
        iv_light.setVisibility(View.GONE);
        pb_settings.setVisibility(View.VISIBLE);
    }

    private void showDialogLogin() {
        dialog_login = new Dialog(this);
        dialog_login.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_login.setContentView(R.layout.dialog_login);
        dialog_login.show();

        ll_dialog_content   = dialog_login.findViewById(R.id.ll_dialog_login_content);
        pb_dialog           = dialog_login.findViewById(R.id.pb_dialog_login_password);
        edt_password        = dialog_login.findViewById(R.id.edt_dialog_password);
        tv_forget_password  = dialog_login.findViewById(R.id.tv_dialog_forget_password);
        tv_incorrect_password = dialog_login.findViewById(R.id.tv_dialog_incorrect_password);
        btn_exit            = dialog_login.findViewById(R.id.btn_dialog_exit);
        btn_login           = dialog_login.findViewById(R.id.btn_dialog_login);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_login.dismiss();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                MyUtil.hideSoftKeyboard(MainActivity.this, v);

                handleLogin();

                return true;
            }
        });

        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_login.dismiss();
                startActivity(new Intent(MainActivity.this, Activity_Restore_Password.class));
            }
        });
    }

    private void handleLogin(){
        // Show ProgressBar
        ll_dialog_content.setVisibility(View.GONE);
        pb_dialog.setVisibility(View.VISIBLE);

        String str_password = edt_password.getText().toString();

        Prefs prefs_user = new Prefs(this, MyConstants.STORE_LOGIN);
        String login_password = prefs_user.getString(MyConstants.LOGIN_PASSWORD, "");


        presenterLogin.login(str_password, login_password);

    }


    private void checkCollision(){
        collisionRight  = widthParent - (iv_plate.getWidth()*1/4);
        collisionLeft   = -(iv_plate.getWidth()*3/4);
        collisionTop    = -(iv_plate.getWidth()*3/4);
        collisionBottom = heightParent - (iv_plate.getHeight()*1/4);
        if(
                iv_plate.getX() >= collisionRight ||
                        iv_plate.getX() <= collisionLeft ||
                        iv_plate.getY() <= collisionTop ||
                        iv_plate.getY() >= collisionBottom ||

                        (iv_plate.getY() <= collisionTop && iv_plate.getX() <= collisionLeft) ||
                        (iv_plate.getY() <= collisionTop && iv_plate.getX() >= collisionRight) ||
                        (iv_plate.getY() >= collisionBottom && iv_plate.getX() >= collisionRight) ||
                        (iv_plate.getY() >= collisionBottom && iv_plate.getX() <= collisionLeft)
        ){
            isCollision = true;
            isCollisionLeft   = iv_plate.getX() <= collisionLeft;
            isCollisionRight  = iv_plate.getX() >= collisionRight;
            isCollisionTop    = iv_plate.getY() <= collisionTop;
            isCollisionBottom = iv_plate.getY() >= collisionBottom;

            isCollisionTL = isCollisionTop && isCollisionLeft;
            isCollisionTR = isCollisionTop && isCollisionRight;
            isCollisionBR = isCollisionBottom && isCollisionRight;
            isCollisionBL = isCollisionBottom && isCollisionLeft;
        }else{
            isCollision       = false;
            isCollisionRight  = false;
            isCollisionLeft   = false;
            isCollisionBottom = false;
            isCollisionTop    = false;
            isCollisionTL     = false;
            isCollisionTR     = false;
            isCollisionBL     = false;
            isCollisionBR     = false;
        }
    }


    private void randomDiceRotate(ImageView iv){
        iv.setRotation(random.nextInt(34) * 10 + (random.nextInt(6) + 11));
    }

    private void randomAnimal(ImageView iv, int timer, MediaPlayer mediaPlayer_finish){
        new CountDownTimer(timer, 40) {
            @Override
            public void onTick(long millisUntilFinished) {
                iv.setImageResource(getResources().getIdentifier(
                        "img_" + (random.nextInt(6) + 1),
                        "drawable",
                        getPackageName()
                ));
            }

            @Override
            public void onFinish() {

                mediaPlayer_finish.start();
                iv.setImageResource(getResources().getIdentifier(
                        "img_" + (random.nextInt(6) + 1),
                        "drawable",
                        getPackageName()
                ));
                if(iv.getId() == R.id.iv_3){

                    count_sensorChange = 0;

                    if(mediaPlayer_start.isPlaying()) {
                        mediaPlayer_start.stop();
                        try {
                            mediaPlayer_start.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    MyUtil.setImageButton(iv_shake, R.drawable.img_btn_shake_waiting_vi, R.drawable.img_btn_shake_waiting_en);
                    iv_shake.setEnabled(true);
//                    btn_open_close.setBackgroundColor(getResources().getColor(R.color.redA200));
//                    btn_open_close.setEnabled(true);
                    iv_plate.setClickable(true);
                    iv_light.setClickable(true);

                }

            }
        }.start();

    }

    private void runCheatAnimal(ImageView iv, int timer, MediaPlayer mediaPlayer_finish, int resourceImage){
        new CountDownTimer(timer, 40) {
            @Override
            public void onTick(long millisUntilFinished) {
                iv.setImageResource(getResources().getIdentifier(
                        "img_" + (random.nextInt(6) + 1),
                        "drawable",
                        getPackageName()
                ));
            }

            @Override
            public void onFinish() {

                mediaPlayer_finish.start();
                iv.setImageResource(resourceImage);

                if(iv.getId() == R.id.iv_3){

                    count_sensorChange = 0 ;

                    if(mediaPlayer_start.isPlaying()) {
                        mediaPlayer_start.stop();
                        try {
                            mediaPlayer_start.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    MyUtil.setImageButton(iv_shake, R.drawable.img_btn_shake_waiting_vi, R.drawable.img_btn_shake_waiting_en);
                    iv_shake.setEnabled(true);
//                    btn_open_close.setBackgroundColor(getResources().getColor(R.color.redA200));
//                    btn_open_close.setEnabled(true);
                    iv_plate.setClickable(true);
                    iv_light.setClickable(true);

                    if(timer == 6000 && listRunCheat.size() == 0)
                        iv_light.setColorFilter(Color.WHITE);
                }

            }
        }.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if(itIsNotFirstTime){
            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            if((xDifference > shakeThreshold && yDifference > shakeThreshold)
                    || (xDifference > shakeThreshold && zDifference > shakeThreshold)
                    || (yDifference > shakeThreshold && zDifference > shakeThreshold)
            ){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    vibrator.vibrate(500);
                    // deprecated in API 26
                }

                shake();

            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;

        itIsNotFirstTime = true;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void shake(){
        count_sensorChange++;
        Logdln("shake shake\ncount_sensorChange: " +count_sensorChange, 563);
        if(count_sensorChange == 1) {
            mediaPlayer_start.start();// no need to call prepare(); create() does that for you
            MyUtil.setImageButton(iv_shake, R.drawable.img_btn_shake_pressed_vi, R.drawable.img_btn_shake_pressed_en);
            iv_shake.setEnabled(false);
            iv_light.setClickable(false);

            randomDiceRotate(iv_1);
            randomDiceRotate(iv_2);
            randomDiceRotate(iv_3);

            if (listRunCheat != null && listRunCheat.size() != 0) {

                if (listRunCheat.get(0).getDiceLeft() != R.drawable.img_7) {
                    runCheatAnimal(iv_1, 3000, mediaPlayer_finish_1, listRunCheat.get(0).getDiceLeft());
                } else {
                    randomAnimal(iv_1, 3000, mediaPlayer_finish_1);
                }

                if (listRunCheat.get(0).getDiceCenter() != R.drawable.img_7) {
                    runCheatAnimal(iv_2, 4500, mediaPlayer_finish_2, listRunCheat.get(0).getDiceCenter());
                } else {
                    randomAnimal(iv_2, 4500, mediaPlayer_finish_2);
                }

                if (listRunCheat.get(0).getDiceRight() != R.drawable.img_7) {
                    runCheatAnimal(iv_3, 6000, mediaPlayer_finish_3, listRunCheat.get(0).getDiceRight());
                } else {
                    randomAnimal(iv_3, 6000, mediaPlayer_finish_3);
                }

                if (listCheats.contains(listRunCheat.get(0)))
                    listCheats.remove(listRunCheat.get(0));

                listRunCheat.remove(0);

                String strListCheats = new Gson().toJson(listCheats);
                prefs_rewarded.setString(MyConstants.LIST_CHEATS, strListCheats);

            } else {

                randomAnimal(iv_1, 3000, mediaPlayer_finish_1);
                randomAnimal(iv_2, 4500, mediaPlayer_finish_2);
                randomAnimal(iv_3, 6000, mediaPlayer_finish_3);

            }
        }
    }



    // ===== LIFECYCLE ACTIVITY ==================
    @Override
    protected void onStart() {
        super.onStart();
        // REGISTER BROADCAST RECEIVER
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);

        iv_light.setVisibility(View.VISIBLE);
        pb_settings.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Off sensor here
        Prefs prefs_sensor = new Prefs(MainActivity.this, MyConstants.STORE_SETTINGS);
        boolean sf_accelerometer_sensor = prefs_sensor.getBoolean(MyConstants.ACCELEROMETER_SENSOR, true);
        if(sf_accelerometer_sensor) {
            if (isAccelerometerSensorAvailable) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }else{
            sensorManager.unregisterListener(this);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(MyConstants.STORE_REWARDED, MODE_PRIVATE);

        listCheats = new Gson().fromJson(
                sharedPreferences.getString(MyConstants.LIST_CHEATS, ""),
                new TypeToken<List<Cheat>>(){}.getType());

        if(listCheats != null) {
            for (Cheat item : listCheats) {
                if (item.isPlay()) {
                    listRunCheat.add(item);
                }
            }
        }
        if(listRunCheat != null && listRunCheat.size() > 0)
            iv_light.setColorFilter(Color.YELLOW);
        else
            iv_light.setColorFilter(Color.WHITE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerSensorAvailable){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logd("onStop()");

        listRunCheat.clear();

        if (dialog_login != null) {
            ll_dialog_content.setVisibility(View.VISIBLE);
            pb_dialog.setVisibility(View.GONE);
            if(dialog_login.isShowing()) dialog_login.dismiss();
        }

        unregisterReceiver(myBroadcastReceiver);

    }


    private void initial() {
        weakReference = new WeakReference<>(MainActivity.this);

        pb_settings     = findViewById(R.id.pb_settings);
        tv_version      = findViewById(R.id.tv_version);
        iv_light        = findViewById(R.id.iv_light);
        iv_shake        = findViewById(R.id.iv_shake);
//        btn_open_close  = findViewById(R.id.btn_open_close);
        iv_1            = findViewById(R.id.iv_1);
        iv_2            = findViewById(R.id.iv_2);
        iv_3            = findViewById(R.id.iv_3);
        iv_plate        = findViewById(R.id.iv_plate);
        rl_dice         = findViewById(R.id.rl_dice);

        fl_ad_view_container = findViewById(R.id.fl_ad_view_container);
    }

    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== MainActivity.java ================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== MainActivity.java - line: " + n + " ================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== MainActivity.java ================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== MainActivity.java - line: " + n + " ================\n" + str);
    }
    private void showSnackBarTop(int color, String str){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), str, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        view.setBackgroundColor(color);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }
    private void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
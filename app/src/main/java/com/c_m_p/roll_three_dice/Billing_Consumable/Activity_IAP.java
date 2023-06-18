package com.c_m_p.roll_three_dice.Billing_Consumable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c_m_p.roll_three_dice.BuildConfig;
import com.c_m_p.roll_three_dice.Cheat.Cheat;
import com.c_m_p.roll_three_dice.IEventNetwork;
import com.c_m_p.roll_three_dice.MyBroadcastReceiver;
import com.c_m_p.roll_three_dice.MyUtil.MyConstants;
import com.c_m_p.roll_three_dice.MyUtil.MyUtil;
import com.c_m_p.roll_three_dice.MyUtil.Prefs;
import com.c_m_p.roll_three_dice.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** TODO: 14(2.6.3)<br />
 * TRƯỚC KHI PUBLIC:<br />
 * <b style="color:#EB2415">{@code [ ]}</b> Gỡ bỏ tv_last_purchase.setOnClickListener()<br />
 * <b style="color:#ffff00">{@code [x]}</b> Update lại Real Key for AdMob Banner Bottom<br />
 * <b style="color:#ffff00">{@code [x]}</b> Update lại Real Key for AdMob Video Regard<br />
 * <b style="color:#ffff00">{@code [x]}</b> Update lại countdownTimer của RewardAd<br />
 * <b style="color:#EB2415">{@code [ ]}</b> Clean code<br />
 * <b style="color:#ffff00">{@code [x]}</b> Check lại ngôn ngữ EN-VI<br />
 *
 * <b style="color:#ffff00">{@code [x]}</b> Lỗi các xúc xắc đều hiện Tôm<br />
 * <b style="color:#ffff00">{@code [x]}</b> Lỗi không ẩn ProgressBar khi mua nhiều Cheat khác nhau<br />
 * <b style="color:#ffff00">{@code [x]}</b> Trong khi Server đang xử lý sau khi hoàn thành việc mua,<br />
 *     Nếu thực hiện onStop() => onCreate() thì bị mất dấu của purchase<br />
 *     => Sugguest: Sử dụng Service để thực hiện Verify Purchase<br />
 * <span style="color:#ffff00">    => Tạm thời hiện dialog thông báo cho người dùng</span><br />
 * <b style="color:#ffff00">{@code [x]}</b> Fix responsive trên màn hình xxhdpi 480dpi<br />
 * <b style="color:#EB2415">{@code [ ]}</b> Chưa consume nếu kết nối bị lỗi khi mua lần đầu<br />
 * <b style="color:#ffff00">{@code [x]}</b> Thêm ProgressBar sau khi hiện thông báo mua hàng thành công từ Google Pay<br />
 * <b style="color:#ffff00">{@code [x]}</b> Hiện Dialog sau khi hiện thông báo mua hàng thành công từ Google<br />
 * <b style="color:#ffff00">{@code [x]}</b> Hiện Dialog nếu kết nối tới Play Billing quá 6 seconds<br />
 * <b style="color:#ffff00">{@code [x]}</b> Tăng tốc verify<br />
 * <b style="color:#ffff00">{@code [x]}</b> Thêm xử lý consume cho các purchases bị lỗi CONSUME thất bại ở lần<br />
 *     mua trước để tránh báo lỗi "Bạn đã sở hữu mặt hàng này"<br />
 * <b style="color:#ffff00">{@code [x]}</b> Lấy token từ Logcat trong Android Studio POST lên server Firebase_Emulator<br />
 *     qua file testAPI.http => Báo lỗi "LINE 98: Error: The product purchase is not owned by the user.<br />
 * <b style="color:#ffff00">{@code [x]}</b> Xữ lý vấn đề không consume khi lỡ mất kết nối trong lúc giao dịch<br />
 *     Khi chạy lại app cũng không consume<br />
 *     Và tại sao lại gọi _verifyPurchase 2 lần:<br />
 *         ở onPurchasesUpdated() trong onCreate()<br />
 *         và ở queryPurchasesAsync() trong onResume()<br />
 *
 * <b style="color:#ffff00">{@code [x]}</b> Google Play Billing không phản hồi khi để lâu, lúc đó nhấn vào giá tiền<br />
 *     thì không hoạt động<br />
 *     - LÀ DO HÀM ONSTOP() DÃ GỌI ENDCONNECTION()<br />
 *     - XỬ LÝ BẰNG CÁCH GỌI HÀM FINISH() ĐỂ QUAY LẠI Activity_Setting.java<br />
 * <b style="color:#ffff00">{@code [x]}</b> Fix layout bị margin Top khi danh sách Purchase được loaded<br />
 * <b style="color:#ffff00">{@code [x]}</b> Lỗi khi watchAdCount = 0 và thời gian của CountDownTimer chưa hết, thì khi khởi<br />
 *     động lại app, watchAdCount được gán = 2. Trong khi respect là 0 cho<br />
 *     đến khi thời gian CountDownTimer đã hết thì mới gán = 2<br />
 */
public class Activity_IAP extends AppCompatActivity {
    String LOCALHOST_1 = "http://192.168.111.254:5001/fir-emulator-tester-86145/us-central1";
    String LOCALHOST_2 = "http://192.168.111.254:5001/fish-prawn-crab-2022-speci-248/us-central1";
    String LOCALHOST_3 = "http://10.0.2.2:5001/fir-emulator-tester-86145/us-central1";
    String LOCALHOST_4 = "http://10.0.2.2:5001/fish-prawn-crab-2022-speci-248/us-central1";
    String FIREBASEHOST = "https://us-central1-fish-prawn-crab-2022-speci-248.cloudfunctions.net";
    String URL = FIREBASEHOST;

    private MyBroadcastReceiver myBroadcastReceiver;
    private boolean isNetworkAvailable = false;
    private ProgressDialog progressDialog;
    private RewardedAd mRewardedAd;

    private BillingClient billingClient;
    private Handler handler;
    private Activity_IAP activity;

    private Prefs prefs_rewarded;

    BuyCheatAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tv_watch_ad_count;
    private TextView tv_timer;
    private ImageView iv_get_free;
    private ImageView iv_ok;
    private ProgressBar progressBar_purchase;
    private LinearLayout ll_contain_last_purchase;
    private TextView tv_last_purchase_time,
            tv_last_purchase_name,
            tv_last_purchase_price;

    private int watchAdCount = 2,
            _7m              = 7*60,
            _90m             = 90*60;

    private long countDownSecond  = 0;
    private CountDownTimer obj_CountDownTimer;
    private boolean isCountDownTimerRunning = false;

    ImmutableList<QueryProductDetailsParams.Product> productList;

    List<Cheat> listCheats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);
        Logd("onCreate()");
        Base64.decode("thong20", Base64.DEFAULT);

        initial();

        prefs_rewarded = new Prefs(this, MyConstants.STORE_REWARDED);

        myBroadcastReceiver = new MyBroadcastReceiver(new IEventNetwork() {
            @Override
            public void onResultNetworkAvailable(boolean isAvailable) {
                isNetworkAvailable = isAvailable;
            }
        });


        // Get listCheats from SharedPreferences
        listCheats = new Gson().fromJson(
                prefs_rewarded.getString(MyConstants.LIST_CHEATS, initialList()),
                new TypeToken<List<Cheat>>(){}.getType()
        );

        // START - Run CountDownTimer =======================================================
        // Run CountDownTimer in SharedPreferences
        // endTime là thời gian sẽ kết thúc ở tương lai
        // countDownSecond là biến GLOBAL, là thời đếm ngược của timer, ví dụ sẽ chạy trong 7 phút hoặc 90 phút
        long currentTime = System.currentTimeMillis();

        long endTime = prefs_rewarded.getLong(MyConstants.END_TIME, 0);
        countDownSecond = (endTime - currentTime) / 1000;
        if(countDownSecond > 0){
            iv_get_free.setEnabled(false);
            tv_timer.setVisibility(View.VISIBLE);
            startWatch();
        }else{
            countDownSecond = 0;
            isCountDownTimerRunning = false; // đề phòng trường hợp hàm "onFinish()" của CountDownTimer
            // không thể chạy do ứng dụng đã "destroy" hoặc do hệ thống
            // kill trước khi chạy hàm "onFinish()"
        }

        // END - Run CountDownTimer =======================================================

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


        iv_get_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable) {
                    iv_get_free.setEnabled(false);
                    iv_get_free.setImageResource(R.drawable.img_btn_pressed_green);
                    showDialogWatchAd();

                }else{
                    showToast(getString(R.string.Please_check_your_internet_connection_and_try_again));
                }
            }
        });


        handler = new Handler();
        activity = this;

        // [IAP #1] Initialize a BillingClient with PurchasesUpdatedListener
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() { // sẽ được gọi sau khi user mua hàng
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> list) {
                        // verifying the Purchase with Back-end
                        // link: https://youtu.be/KYFM2z5KPq0?t=1124
                        Logdln("billingResult.getResponseCode(): " +billingResult.getResponseCode(), 282);
                        if(
                                billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                        && !list.isEmpty()
                        ){
                            for(Purchase purchase : list){

                                // 0: unspedified    1: purchased    2: pending
                                // Trường hợp:
                                //      - MUA MỚI
                                //      - ĐÃ MUA MÀ CHƯA ACKNOWLEDGE
                                //        Xảy ra trong trường hợp:
                                //          -- Rớt mạng ngay thời điểm nhấn nút BUY lúc này, dữ liệu không gửi được
                                //             lên server để verify và acknowledge
                                //          -- Gửi dữ liệu lên server sai URL nên ko thể verify và acknowledge
                                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                                    Logdln("call _verifyPurchase()" , 312);
                                    _verifyPurchaseAsync(purchase); // gửi lên Server để verify
                                }

                            }
                        }
                        // .................. ↓ response code = 1
                        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
                            Logdln("User canceled the purchase", 297);
//                              showToast("Purchase has not been made");
                        }
                        // .................. ↓ response code = 7
                        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
                            Logdln("The user already owns this item", 300);
                            MyUtil.showSnackBarTopWarning(
                                    Activity_IAP.this,
                                    iv_get_free.getRootView(),
                                    "You already owns this item"
                            );
                            String listSize = list != null ? "NOT NULL" : "NULL";
                            Logdln("list.size: " +listSize, 327);
                            billingClient.queryPurchasesAsync(
                                    QueryPurchasesParams
                                            .newBuilder()
                                            .setProductType(BillingClient.ProductType.INAPP)
                                            .build(),
                                    new PurchasesResponseListener() {
                                        @Override
                                        public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                                            if(list != null) for(Purchase i : list) _consumeAsync(i); // ITEM_ALREADY_OWNED
                                        }
                                    }
                            );
                        }
                        // .................. ↓ response code = 5
                        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR){
                            Logdln("Developer error means that Google Play does not recognize the configuration. If you are just\n" +
                                            "getting started, make sure you have configured the application correctly in the Google Play Console.\n" +
                                            "The SKU product ID must match and the APK you are using must be signed with release keys.",
                                    311
                            );
                        }
                        else {
                            Logdln("BillingResult [" +billingResult.getResponseCode()+ "]: " +billingResult.getDebugMessage(), 315);
                        }

                    }
                })
                .build();

        // [IAP #2] establish a connection to Google Play
        connectGooglePlayBilling();

    }


    // LIFECYCLE ACTIVITY
    @Override
    protected void onStart() {
        super.onStart();
        Logd("onStart()");
        // REGISTER BROADCAST RECEIVER
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logd("onResume()");
        // Update UI
        watchAdCount = prefs_rewarded.getInt(MyConstants.WATCH_AD_COUNT, 2);
        // Tránh trường hợp watchAdCount có giá trị âm do hàm "onFinish()" của
        // CountDownTimer không được gọi do ứng dụng đã "destroy" hoặc do hệ
        // thống kill trước khi chạy hàm "onFinish()"
        if(!isCountDownTimerRunning && watchAdCount == 0){
            watchAdCount = 2;
        }
        tv_watch_ad_count.setText(String.valueOf(watchAdCount));


        // link: https://developer.android.com/google/play/billing/integrate#fetch
        // TRÁNH TRƯỜNG HỢP CÁC SẢN PHẨM MUA RỒI MÀ CHƯA CONSUME DO RỚT MẠNG
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        Logdln(".getResponseCode: " +billingResult.getResponseCode(), 399);
                        Logdln("list size: " +list.size(), 199);
                        if(
                                billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                        && list != null
                        ){
                            for(Purchase purchase : list){
                                // [5] Verify Purchase
                                Logdln("purchase.getPurchaseState: " +purchase.getPurchaseState(), 404);
                                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    Logdln("purchase isAcknownledge: " +purchase.isAcknowledged(), 406);
                                    if (!purchase.isAcknowledged()) { // được thừa nhận hoặc consume hay không?
                                        _consumeAsync(purchase); // onResume()
                                    }
                                }
                            }
                        }
                        else{
                            Logdln("Response Code: " + billingResult.getResponseCode(), 414);
                        }
                    }
                }
        );

//        billingClient.queryProductDetailsAsync(
//                QueryProductDetailsParams.newBuilder()
//                        .setProductList(productList)
//                        .build(),
//                new ProductDetailsResponseListener() {
//                    @Override
//                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
//                        if(!list.isEmpty()){
//                            Logdln("PRICE: " +list.get(0).getOneTimePurchaseOfferDetails().getFormattedPrice(), 477);
//                        }
//                    }
//                }
//        );



    }

    @Override
    protected void onStop() {
        super.onStop();
        Logd("onStop()");

        unregisterReceiver(myBroadcastReceiver);
        billingClient.endConnection();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logd("onDestroy()");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private String initialList(){
        List<Cheat> result = new ArrayList<>();
        result.add(new Cheat());
        result.add(new Cheat());
        return new Gson().toJson(result);
    }



    private CountDownTimer getCountDownTimer(){
        NumberFormat f = new DecimalFormat("00");
        return new CountDownTimer(countDownSecond * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long _second = countDownSecond % 60;
                long _minute = countDownSecond / 60 % 60;
                long _hour   = countDownSecond / 60 / 60 % 24;

                countDownSecond--;
                if(_hour > 0){
                    tv_timer.setText(
                            _hour + ":" +
                                    f.format(_minute) + ":" +
                                    f.format(_second)
                    );
                }else{
                    tv_timer.setText(
                            f.format(_minute) + ":" +
                                    f.format(_second)
                    );
                }

            }

            @Override
            public void onFinish() {
                tv_timer.setText("0:00");
                tv_timer.setVisibility(View.INVISIBLE);
                isCountDownTimerRunning = false;
                // Update UI Button Free
                iv_get_free.setEnabled(true);
                iv_get_free.setImageResource(R.drawable.img_btn_waiting_green);

                if(watchAdCount <= 0) {
                    watchAdCount = 2;
                    tv_watch_ad_count.setText(String.valueOf(watchAdCount));
                    prefs_rewarded.setInt(MyConstants.WATCH_AD_COUNT, watchAdCount);
                }

            }
        };
    }

    private void startWatch() {
        // Update UI
        isCountDownTimerRunning = true;
        iv_get_free.setEnabled(false);
        iv_get_free.setImageResource(R.drawable.img_btn_pressed_green);
        // run CountDownTimer
        if(obj_CountDownTimer != null) obj_CountDownTimer.cancel();
        obj_CountDownTimer = getCountDownTimer();
        obj_CountDownTimer.start();
    }

    /**
     * Thêm phần thưởng sau đó:
     * - Xác nhận lên Server
     * - Lưu vào SharedPreferences<br />
     * => ở Activity_Settings.java sẽ lấy dữ liệu trong SharedPreferences<br />
     * để hiện listCheats và tv_reward_amount
     */
    private void addRewarded(int quantity){
        for(int i = 0 ; i < quantity ; i++) {
            listCheats.add(new Cheat());
        }
        storeListCheats(listCheats);
    }

    private void confirmGrantEntitlement(Purchase purchase) {
        String ENDPOINT = "/confirmGrantEntitlement";
        String requestUrl_confirmGrantEntitlement = URL + ENDPOINT
                +"?purchaseToken=" +purchase.getPurchaseToken();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                requestUrl_confirmGrantEntitlement,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logdln("GRANTED ENTITLEMNET", 572);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logdln(error.toString(), 579);
                    }
                }
        );
        // Set awaiting time để tránh gặp lỗi timeoutError
        setTimeoutError(stringRequest);
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void storeListCheats(List<Cheat> listCheats) {
        Logdln("storeList()", 356);
        Gson gson = new Gson();
        String json = gson.toJson(listCheats);

        prefs_rewarded.setString(MyConstants.LIST_CHEATS, json);
    }

    private void reloadRewardedAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, BuildConfig.AM_UNIT_ID_REWARDED_AD,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
//                        showToast(loadAdError.toString());
                        Logdln(loadAdError.toString(), 422);
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Logdln("Ad was reloaded successful.", 429);
                    }
                });
    }

    private void showRewardedAd() {
        if (mRewardedAd != null) {
            // [AdMob #3.1] set fullscreen Ad
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Logdln("Ad was clicked.", 441);
//                    addRewarded();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Logdln("Ad dismissed fullscreen content.", 425);
                    mRewardedAd = null;
                    reloadRewardedAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Logdln("Ad failed to show fullscreen content.", 435);
                    mRewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Logdln("Ad recorded an impression.", 442);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Logdln("Ad showed fullscreen content.", 448);
                }
            });

            // [AdMob #3.2] Show Ad
            Activity activityContext = Activity_IAP.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Logdln("The user earned the reward.", 453);

                    watchAdCount--;
                    int waiting_time = watchAdCount > 0 ? _7m*1000 : _90m*1000; // milliseconds

                    // kiểm tra logic biến waiting_time ở hàm onResume()
                    long currentTime = System.currentTimeMillis();
                    long endTime = currentTime + waiting_time;
                    countDownSecond = waiting_time/1000;
                    // Storage
                    prefs_rewarded.setLong(MyConstants.END_TIME, endTime);
                    prefs_rewarded.setInt(MyConstants.WATCH_AD_COUNT, watchAdCount);
                    // UPDATE UI
                    tv_watch_ad_count.setText(String.valueOf(watchAdCount));
                    iv_get_free.setEnabled(false);
                    iv_get_free.setImageResource(R.drawable.img_btn_pressed_green);
                    tv_timer.setVisibility(View.VISIBLE);

                    startWatch();
                    addRewarded(1);
                }
            });
        } else {
            Logdln("The rewarded ad wasn't ready yet.", 485);
        }
    }

    private void loadAndShowRewardedAd(){
        // progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.Please_wait));
        progressDialog.setMessage(getString(R.string.Loading_Rewarded_Ad));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, BuildConfig.AM_UNIT_ID_REWARDED_AD,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        mRewardedAd = null;
                        progressDialog.dismiss();
                        showToast(getString(R.string.Please_try_again_later));
                        iv_get_free.setEnabled(true);
//                        showToast(loadAdError.toString());
//                        showToast("Ad wasn't loaded Ad " +loadAdError.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Logdln("Ad was loaded.", 517);
                        progressDialog.dismiss();
                        showRewardedAd();
                    }
                });
    }

    private void showDialogWatchAd(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.To_continue_You_need_to_watch_a_promotion_video));
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadAndShowRewardedAd();
            }
        });
        dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iv_get_free.setEnabled(true);
                iv_get_free.setImageResource(R.drawable.img_btn_waiting_green);
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                iv_get_free.setEnabled(true);
                iv_get_free.setImageResource(R.drawable.img_btn_waiting_green);
            }
        });
        dialog.show();
    }


    // GOOGLE PLAY BILLING V5 =================================================
    boolean isConnectedToBillingService = false;
    void connectGooglePlayBilling(){
        // connect to Google Play
        Logd("connectGooglePlayBilling()");

        CountDownTimer countDownTimerConnection = new CountDownTimer(9000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Logdln("CONNECTING TO GOOGLE PLAY BILLING ............", 759);
            }

            @Override
            public void onFinish() {
                Logdln("Timer Connect FINISH", 765);
                showDialog_error_connect_Billing_Service();
                progressBar_purchase.setVisibility(View.GONE);
            }
        };

        countDownTimerConnection.start();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // retry connect to Google Play
                Logdln("onBillingServiceDisconnected()", 573);
                Logdln("getConnectionState: " +billingClient.getConnectionState(), 577);
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                // connect successful
                isConnectedToBillingService = true;
                countDownTimerConnection.cancel();
                Logdln("CONNECT SUCCESSFUL", 640);
                Logdln("billingResult.getResponseCode(): " +billingResult.getResponseCode(), 641);
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Logd("case OK");
                    // [IAP #3] Show products
                    showProducts();
                    showLastPurchaseInfo();
                }
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
                    Logd("case BILLING_UNAVAILABLE");
                    runOnUiThread(new Runnable() { // update UI when system Lock UI
                        @Override
                        public void run() {
                            showDialog_error_connect_Billing_Service();
                            progressBar_purchase.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }



    private void showLastPurchaseInfo() {
        Logd("showLastPurchaseInfo()");
        // Sử dụng Thread để thay thế AsynTask bị Deprecated
        // Trong khối code này ta chủ yếu lấy price của last purchase
        // Để lấy dược price ta phải xử lý trong .queryProductDetailsAsync()
        // vì .queryPurchaseHistoryAsync() không thể lấy được price, mà chỉ
        // lấy được productID, time, token mà thôi
        new Thread(new Runnable() {
            @Override
            public void run() {
                // doInBackground stuff here
                // ...
                billingClient.queryPurchaseHistoryAsync(
                        QueryPurchaseHistoryParams.newBuilder()
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build(),
                        new PurchaseHistoryResponseListener() {
                            @Override
                            public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, @Nullable List<PurchaseHistoryRecord> listPurchaseHistory) {

                                billingClient.queryProductDetailsAsync(
                                        QueryProductDetailsParams.newBuilder().setProductList(productList).build(),
                                        new ProductDetailsResponseListener() {
                                            @Override
                                            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> listProductDetails) {
                                                // my override => VERY IMPORTANT
                                                runOnUiThread(new Runnable() { // update UI when system Lock UI
                                                    @Override
                                                    public void run() {

                                                        // onPostExecute stuff here
                                                        // ...
                                                        Logdln("onPurchaseHistoryResponse()", 852);
                                                        if (listPurchaseHistory != null && !listPurchaseHistory.isEmpty()) {
                                                            if (ll_contain_last_purchase.getVisibility() != View.VISIBLE){
                                                                ll_contain_last_purchase.setVisibility(View.VISIBLE);
                                                            }
                                                            PurchaseHistoryRecord iPurchaseHistory = listPurchaseHistory.get(0);
                                                            String purchaseHistory_name = iPurchaseHistory.getProducts().get(0);

                                                            Logdln("orderID: " +iPurchaseHistory.getOriginalJson(), 805);

                                                            // LẤY NAME ================================
                                                            switch(purchaseHistory_name){
                                                                case "product_consume_03_cheats":
                                                                    tv_last_purchase_name.setText(getString(R.string._3_cheats));
                                                                    break;
                                                                case "product_consume_07_cheats":
                                                                    tv_last_purchase_name.setText(getString(R.string._7_cheats));
                                                                    break;
                                                                case "product_consume_20_cheats":
                                                                    tv_last_purchase_name.setText(getString(R.string._20_cheats));
                                                                    break;
                                                                case "product_consume_30_cheats":
                                                                    tv_last_purchase_name.setText(getString(R.string._30_cheats));
                                                                    break;

                                                            }

                                                            // LẤY PRICE ================================
                                                            for(ProductDetails i : listProductDetails){
                                                                if(i.getProductId().equals(purchaseHistory_name)){
                                                                    tv_last_purchase_price.setText(i.getOneTimePurchaseOfferDetails().getFormattedPrice());
                                                                }
                                                            }

                                                            // LẤY TIME ================================
                                                            String purchaseHistory_time = MyUtil.convertMilliToDate(iPurchaseHistory.getPurchaseTime());
                                                            tv_last_purchase_time.setText(purchaseHistory_time);

                                                        }
                                                        else{
                                                            Logdln("listPurchaseHistory is NULL", 872);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                );
                            }
                        }
                );
            }
        }).start();
    }


    // import this lib to usage ImmutableList class:
    // implementation 'com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava'
    // implementation 'com.google.guava:guava:24.1-jre'
    @SuppressLint("SetTextI18n")
    void showProducts(){

        Logdln("showProducts()", 235);
        productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_consume_03_cheats") // id_03_cheats : là giá trị trong cột "Product ID" trong Play Console
                        .setProductType(BillingClient.ProductType.INAPP) // kiểu của Product là in-app purchase
                        .build(),
                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_consume_07_cheats")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_consume_20_cheats")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                //Product 4
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_consume_30_cheats")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
        // to show Product, use method .queryProductDetailsAsync()
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() { //
                    @Override
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> listProducts) {
                        Logd("onProductDetailsResponse()");

//                        productDetailsList.clear();

                        for(ProductDetails i : listProducts) Logdln(i.getName(), 881);


                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Logdln("posted delayed", 734);

                                if(listProducts != null){
//                                    for(ProductDetails i : listProducts) Logdln(i.getProductId(), 890);

//                                    List<ProductDetails> sortedList = new ArrayList<>(listProducts);
//                                    sortedList = sortByPrice(sortedList);
//
//                                    for(ProductDetails i : sortedList) Logdln(i.getProductId(), 895);
                                    adapter = new BuyCheatAdapter(listProducts, Activity_IAP.this);

                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Activity_IAP.this, LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(adapter);

                                    // ===================================
                                    progressBar_purchase.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }


                            }
                        }, 2000);
                    }
                });




    }

    private List<ProductDetails> sortByPrice(List<ProductDetails> list){
        List<ProductDetails> result = new ArrayList<>(list);
        Collections.sort(result, new Comparator<ProductDetails>() {
            @Override
            public int compare(ProductDetails o1, ProductDetails o2) {
                String price_o1 = o1.getOneTimePurchaseOfferDetails().getFormattedPrice();
                String price_o2 = o2.getOneTimePurchaseOfferDetails().getFormattedPrice();
                return price_o1.compareTo(price_o2);
            }
        });
        return result;
    }


    // [IAP #4] Launch the purchase flow ===========================
    // run when user onclick Price Button
    public void launchPurchaseFlow(ProductDetails productDetails) {
        Logd("launchPurchaseFlow()");
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
//                .setObfuscatedAccountId() // giúp Google liên kết accountGoogle với account trong App tránh user gian lận
//                .setObfuscatedProfileId() // giúp Google liên kết accountGoogle với account trong App tránh user gian lận
                .build();

        // Launch the billing flow
//        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }



    // Link trigger on Firebase Function:
    // https://us-central1-fish-prawn-crab-2022-speci-248.cloudfunctions.net/verifyPurchases
    void _verifyPurchaseAsync(Purchase purchase) { // id_progressBar
        Dialog dialog_Processing = get_Dialog_processing();
        dialog_Processing.show();
        Logd("_verifyPurchaseAsync()");

        String ENDPOINT = "/validate";
        String requestUrl_validate = URL + ENDPOINT
                +"?purchaseToken=" +purchase.getPurchaseToken()
                +"&productId=" +purchase.getProducts().get(0)
                +"&packageName=" +getPackageName();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                requestUrl_validate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog_Processing.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Logdln(String.valueOf(jsonObject), 853);
                            String purchaseId = purchase.getProducts().get(0);
                            String[] arrString = purchaseId.split("_");
                            if(jsonObject.isNull("error")){
                                if(jsonObject.getBoolean("isValid")) { // Hợp lệ
                                    // GRANT ENTITLEMENT FOR USER
                                    grantEntitlement(purchase);

                                    confirmGrantEntitlement(purchase);

                                    showLastPurchaseInfo();

                                    _consumeAsync(purchase); // isValid: true

                                    showDialog_successful_purchased(Integer.parseInt(arrString[2]));
                                }
                                else{
                                    Logd("KHÔNG HỢP LỆ");

                                    showDialog_error_purchased();
                                }
                            }
                            else {
                                Logdln("Couldn't complete your purchase", 917);
                                showDialog_error_purchased();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logdln(error.toString(), 933);
                    }
                }
        );
        // Set awaiting time để tránh gặp lỗi timeoutError
        setTimeoutError(stringRequest);


        Volley.newRequestQueue(this).add(stringRequest);

    }

    void _consumeAsync(Purchase purchase) {
        Logd("_consumeAsync()");

        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Logdln("Consume OK", 791);
                    _updateDB(purchase);
                }
                else{
                    Logdln("Consume NOT OK: " +billingResult.getResponseCode(), 793);
                }
            }
        };

        // Nếu consume thành công thì purchase cũng được acknowledge luôn
        billingClient.consumeAsync(consumeParams, listener);

    }

    void _updateDB(Purchase purchase){
        String ENDPOINT = "/update";
        String requestUrl_update = URL + ENDPOINT
                +"?purchaseToken=" +purchase.getPurchaseToken()
                +"&productId=" +purchase.getProducts().get(0)
                +"&packageName=" +getPackageName();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                requestUrl_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logdln("Updated Database", 874);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logdln(error.toString(), 880);
                    }
                }
        );
        // Set awaiting time để tránh gặp lỗi timeoutError
        setTimeoutError(stringRequest);


        Volley.newRequestQueue(this).add(stringRequest);
    }

    void setTimeoutError(StringRequest stringRequest){
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                1,
                1.0f
        ));
    }

    @SuppressLint("SetTextI18n")
    void grantEntitlement(Purchase purchase) {
        Logd("giveUserCheats()");
        String strId = purchase.getProducts().get(0);
        String[] arr = strId.split("_");

        addRewarded(Integer.parseInt(arr[2]));
    }

    public Dialog get_Dialog_processing() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_purchase_processing);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void showDialog_error_connect_Billing_Service() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_connection_to_billing_service_error);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // onclick event
        Button btn_ok = dialog.findViewById(R.id.btn_dialog_OK);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing()) dialog.dismiss();
                finish();
            }
        });
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        if(!isConnectedToBillingService){
            billingClient.endConnection();
            Logdln("billingClient.isReady: " +billingClient.isReady(), 769);
        }

    }

    private void showDialog_error_purchased(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_purchase_error);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // onclick event
        Button btn_ok = dialog.findViewById(R.id.btn_dialog_OK);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing()) dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    };

    private void showDialog_successful_purchased(int n) {
        Logdln("Dialog " +n, 1015);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_purchase_successful);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // onclick event
        Button btn_ok = dialog.findViewById(R.id.btn_dialog_OK);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing()) dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private void initial() {
        recyclerView        = findViewById(R.id.recyclerView);
        iv_get_free         = findViewById(R.id.iv_get_free);
        tv_watch_ad_count   = findViewById(R.id.tv_watch_ad_count);
        tv_timer            = findViewById(R.id.tv_timer_get_reward);
        progressBar_purchase = findViewById(R.id.progressBar_purchase);

        iv_ok               = findViewById(R.id.iv_ok);

        ll_contain_last_purchase = findViewById(R.id.ll_contain_last_purchase);
        tv_last_purchase_time   = findViewById(R.id.tv_last_purchase_time);
        tv_last_purchase_name   = findViewById(R.id.tv_last_purchase_name);
        tv_last_purchase_price  = findViewById(R.id.tv_last_purchase_price);

    }




    // ====================================================================
    private void Logd(String str){
        Log.d("Log.d", "=== Activity_IAP.java ==============================\n" + str);
    }
    private void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_IAP.java - line: " + n + " ==============================\n" + str);
    }
    private void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

}
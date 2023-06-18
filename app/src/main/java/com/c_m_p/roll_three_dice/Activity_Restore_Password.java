package com.c_m_p.roll_three_dice;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.c_m_p.roll_three_dice.MyUtil.MyConstants;
import com.c_m_p.roll_three_dice.MyUtil.Prefs;
import com.google.android.material.snackbar.Snackbar;

public class Activity_Restore_Password extends AppCompatActivity {

    EditText edt_password_restore;
    Button btn_password_restore_next;
    ProgressBar pb_reset_password;
    TextView tv_incorrect_key;

    String str_password_restore;

    Prefs prefs_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        edt_password_restore = findViewById(R.id.edt_password_restore);
        btn_password_restore_next = findViewById(R.id.btn_password_restore_next);
        pb_reset_password = findViewById(R.id.pb_reset_password);
        tv_incorrect_key = findViewById(R.id.tv_incorrect_key);

        prefs_login = new Prefs(this, MyConstants.STORE_LOGIN);
        str_password_restore = prefs_login.getString(MyConstants.LOGIN_PASSWORD_RESTORE, "");
        String hint = str_password_restore.charAt(0) + "***" + str_password_restore.charAt(str_password_restore.length() - 1);
        edt_password_restore.setHint(hint);

        btn_password_restore_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleReset();
            }
        });

        edt_password_restore.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                handleReset();

                return true;
            }
        });


    }

    private void handleReset() {

        pb_reset_password.setVisibility(View.VISIBLE);
        tv_incorrect_key.setVisibility(View.GONE);
        new CountDownTimer(2000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(edt_password_restore.getText().toString().equals(str_password_restore)){
                    prefs_login.clear();
                    MainActivity.showSnackBarTopRestorePasswordSuccessful();
                    finish();
                }else{
                    if(pb_reset_password.isShown()){
                        pb_reset_password.setVisibility(View.GONE);
                    }
                    tv_incorrect_key.setVisibility(View.VISIBLE);

                }
            }
        }.start();
    }


    // ====================================================================
    private void Logd(String str){
        Log.d("Log.d", "=== Activity_Restore_Password.java ==============================\n" + str);
    }
    private void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Restore_Password.java - line: " + n + " ==============================\n" + str);
    }
    private static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Restore_Password.java ==============================\n" + str);
    }
    private static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Restore_Password.java - line: " + n + " ==============================\n" + str);
    }
    private void showSnackBarTop(String str){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), str, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        view.setBackgroundColor(getResources().getColor(R.color.redA400));
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }
    private void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
package com.c_m_p.roll_three_dice.MyUtil;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.c_m_p.roll_three_dice.R;
import com.android.billingclient.api.ProductDetails;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MyUtil {
    public static void hideSoftKeyboard(Activity activity, View v){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    public static void showSnackBarTopSuccessful(Activity activity, View view, String str){

        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
        View v = snackbar.getView();
        // inflate the custom_snackbar_view created previously
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.snackbar_custom, null);
        TextView tv_content = customSnackView.findViewById(R.id.tv_snackbar_content);
        LinearLayout container = customSnackView.findViewById(R.id.ll_snackbar_container);
        container.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.green600));
        tv_content.setText(str);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.topMargin = 100;
        v.setLayoutParams(params);
        snackbar.show();
    }

    public static void showSnackBarTopError(Activity activity, View view, String str){

        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
        View v = snackbar.getView();
        // inflate the custom_snackbar_view created previously
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.snackbar_custom, null);
        TextView tv_content = customSnackView.findViewById(R.id.tv_snackbar_content);
        LinearLayout container = customSnackView.findViewById(R.id.ll_snackbar_container);
        container.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.redA400));
        tv_content.setText(str);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.topMargin = 100;
        v.setLayoutParams(params);
        snackbar.show();
    }

    public static void showSnackBarTopWarning(Activity activity, View view, String str){

        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
        View v = snackbar.getView();
        // inflate the custom_snackbar_view created previously
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.snackbar_custom, null);
        TextView tv_content = customSnackView.findViewById(R.id.tv_snackbar_content);
        LinearLayout container = customSnackView.findViewById(R.id.ll_snackbar_container);
        container.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.orangeDeepA700));
        tv_content.setText(str);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.topMargin = 100;
        v.setLayoutParams(params);
        snackbar.show();
    }

    public static void setImageButton(ImageView iv, int resId_vi, int resId_en){
        if (Locale.getDefault().getLanguage().equals(new Locale("vi").getLanguage()))
            iv.setImageResource(resId_vi);
        else
            iv.setImageResource(resId_en);
    }



    public static List<ProductDetails> sortByID(List<ProductDetails> list){
        List<ProductDetails> result = new ArrayList<>();
        result.addAll(list);
        Log.d("Log.d", "START Result:");
        logList(result);
        Log.d("Log.d", "Android API: " +Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("Log.d", "TRUE");

            Collections.sort(result, new Comparator<ProductDetails>() {
                @Override
                public int compare(ProductDetails o1, ProductDetails o2) {
                    return o1.getProductId().compareTo(o2.getProductId());
                }
            });
        }
        else{
            Log.d("Log.d", "FALSE");
        }
        Log.d("Log.d", "END Result:");
        logList(result);
        return result;
    }

    private static void logList(List<ProductDetails> list){
        for(ProductDetails i : list) {
            Log.d("Log.d", i.getProductId());
        }
    }

    public static String convertMilliToDate(long purchaseTime) {
        String result = "";
        // Date Object: Tue Apr 25 10:45:37 GMT+07:00 2023
        String dateTimePattern = "";
        if(Locale.getDefault().getLanguage().equals("vi")){
            dateTimePattern = "EEE dd/MMM/yyyy HH:mm:ss"; //
        }else {
            dateTimePattern = "EEE MMM dd, yyyy HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimePattern);
        result = sdf.format(purchaseTime);

        return result;
    }

}

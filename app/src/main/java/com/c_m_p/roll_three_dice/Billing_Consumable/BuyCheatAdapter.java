package com.c_m_p.roll_three_dice.Billing_Consumable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.roll_three_dice.R;
import com.android.billingclient.api.ProductDetails;

import java.util.List;
import java.util.Locale;

public class BuyCheatAdapter extends RecyclerView.Adapter<BuyCheatAdapter.BuyCheatViewHolder>{

    List<ProductDetails> list;
    Activity_IAP activity_IAP;


    public BuyCheatAdapter(List<ProductDetails> list, Activity_IAP activity) {
//        this.context = context;
        this.activity_IAP = activity;
        for(ProductDetails i : list){Log.d("Log.d", i.getProductId());}
        this.list = list;
//        for(ProductDetails i : this.list){Log.d("Log.d", i.getProductId());}
    }

    @NonNull
    @Override
    public BuyCheatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchases, parent, false);

        return new BuyCheatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyCheatViewHolder holder, int position) {
        ProductDetails itemProduct = list.get(position);

        String productName = itemProduct.getName(); // Add 3 cheats
        if(Locale.getDefault().getLanguage().equals("vi")){
            String[] arrStr = productName.split(" ");
            holder.tv_name.setText("Thêm " +arrStr[1] +" lượt cài");
        }else {
            holder.tv_name.setText(productName);
        }


        holder.tv_price.setText(itemProduct.getOneTimePurchaseOfferDetails().getFormattedPrice());

        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_IAP.launchPurchaseFlow(itemProduct);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list != null) return list.size();
        return 0;
    }

    class BuyCheatViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout btn_buy;
        TextView tv_name, tv_price;

        public BuyCheatViewHolder(@NonNull View v) {
            super(v);

            btn_buy = v.findViewById(R.id.rl_btn_buy_cheat);
            tv_name = v.findViewById(R.id.tv_name_purchase);
            tv_price = v.findViewById(R.id.tv_price_purchase);

        }
    }






    // ====================================================================
    private void Logd(String str){
        Log.d("Log.d", "=== Adapter.java ==============================\n" + str);
    }
    private void Logdln(String str, int n){
        Log.d("Log.d", "=== Adapter.java - line: " + n + " ==============================\n" + str);
    }
    private static void LogdStatic(String str){
        Log.d("Log.d", "=== Adapter.java ==============================\n" + str);
    }
    private static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Adapter.java - line: " + n + " ==============================\n" + str);
    }
    private void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}

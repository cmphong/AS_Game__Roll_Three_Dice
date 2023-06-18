package com.c_m_p.roll_three_dice.Cheat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.roll_three_dice.Activity_Settings;
import com.c_m_p.roll_three_dice.MyUtil.MyConstants;
import com.c_m_p.roll_three_dice.MyUtil.Prefs;
import com.c_m_p.roll_three_dice.R;
import com.google.gson.Gson;

import java.util.List;

public class AdapterCheat extends RecyclerView.Adapter<AdapterCheat.ViewHolder> {
    private String LEFT = "LEFT", CENTER = "CENTER", RIGHT = "RIGHT";

    private Context context;
    private List<Cheat> listCheats;

    public AdapterCheat(Context context) {
        this.context = context;
    }

    public void setData(List<Cheat> listCheat){
        this.listCheats = listCheat;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_rewarded, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cheat cheatObj = listCheats.get(position);

        holder.diceLeft.setImageResource(cheatObj.getDiceLeft());
        holder.diceCenter.setImageResource(cheatObj.getDiceCenter());
        holder.diceRight.setImageResource(cheatObj.getDiceRight());

        if(cheatObj.isPlay()){ // is pressed
            holder.iv_play.setEnabled(false);
            holder.iv_play.setImageResource(R.drawable.ic_play_disable);
            holder.iv_stop.setEnabled(true);
            holder.iv_stop.setImageResource(R.drawable.ic_stop_enable);
            holder.rl_item_container.setBackground(context.getDrawable(R.drawable.bg_white_argb_33ffffff));

            holder.diceLeft.setEnabled(false);
            holder.diceCenter.setEnabled(false);
            holder.diceRight.setEnabled(false);

        }else{
            holder.iv_play.setEnabled(true);
            holder.iv_play.setImageResource(R.drawable.ic_play_enable);
            holder.iv_stop.setEnabled(false);
            holder.iv_stop.setImageResource(R.drawable.ic_stop_disable);
            holder.rl_item_container.setBackground(null);

            holder.diceLeft.setEnabled(true);
            holder.diceCenter.setEnabled(true);
            holder.diceRight.setEnabled(true);

        }

        holder.diceLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelectDice(LEFT, cheatObj, holder.diceLeft);
            }
        });
        holder.diceCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelectDice(CENTER, cheatObj, holder.diceCenter);
            }
        });
        holder.diceRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelectDice(RIGHT, cheatObj, holder.diceRight);
            }
        });

        holder.iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCheat(position);
            }
        });

        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheatObj.setPlay(true);
                cheatObj.setStop(false);
                if (cheatObj.isPlay()) {
                    holder.iv_play.setEnabled(false);
                    holder.iv_stop.setEnabled(true);

                    holder.iv_play.setImageResource(R.drawable.ic_play_disable);
                    holder.iv_stop.setImageResource(R.drawable.ic_stop_enable);

                    holder.rl_item_container.setBackground(context.getDrawable(R.drawable.bg_white_argb_33ffffff));

                    holder.diceLeft.setEnabled(false);
                    holder.diceCenter.setEnabled(false);
                    holder.diceRight.setEnabled(false);
                }

                saveList();

            }
        });


        // Lỗi không onclick khi từ ActivityMain vào Activity_Rewarded
        // thì các item chưa enable isPlay sẽ xuất hiện lỗi, còn các
        // item đã enable isPlay vẫn onclick bình thường
        holder.iv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheatObj.setStop(true);
                cheatObj.setPlay(false);
                if (cheatObj.isStop()) {
                    holder.iv_play.setEnabled(true);
                    holder.iv_stop.setEnabled(false);

                    holder.iv_play.setImageResource(R.drawable.ic_play_enable);
                    holder.iv_stop.setImageResource(R.drawable.ic_stop_disable);

                    holder.rl_item_container.setBackground(null);

                    holder.diceLeft.setEnabled(true);
                    holder.diceCenter.setEnabled(true);
                    holder.diceRight.setEnabled(true);

                }
                saveList();
                Logdln("stop stop stop", 127);
            }
        });
    }

    private void clearCheat(int position) {
        listCheats.remove(position);
        notifyDataSetChanged();
        Activity_Settings.tv_reward_amount.setText(String.valueOf(listCheats.size()));
        saveList();
    }

    private void saveList() {
        Gson gson = new Gson();
        String json = gson.toJson(listCheats);

        Prefs prefs_Rewarded = new Prefs(context, MyConstants.STORE_REWARDED);
        prefs_Rewarded.setString(MyConstants.LIST_CHEATS, json);
    }

    private void showDialogSelectDice(String dicePosition, Cheat cheatObj, ImageView imageView){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_dice);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        ImageView img_1, img_2, img_3, img_4, img_5, img_6, img_7;
        img_1 = dialog.findViewById(R.id.iv_dialog_1);
        img_2 = dialog.findViewById(R.id.iv_dialog_2);
        img_3 = dialog.findViewById(R.id.iv_dialog_3);
        img_4 = dialog.findViewById(R.id.iv_dialog_4);
        img_5 = dialog.findViewById(R.id.iv_dialog_5);
        img_6 = dialog.findViewById(R.id.iv_dialog_6);
        img_7 = dialog.findViewById(R.id.iv_dialog_7);

        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_1);
                dialog.dismiss();
            }
        });
        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_2);
                dialog.dismiss();
            }
        });
        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_3);
                dialog.dismiss();
            }
        });
        img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_4);
                dialog.dismiss();
            }
        });
        img_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_5);
                dialog.dismiss();
            }
        });
        img_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_6);
                dialog.dismiss();
            }
        });
        img_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage(dicePosition, cheatObj, imageView, R.drawable.img_7);
                dialog.dismiss();
            }
        });

    }

    private void setImage(String dicePosition, Cheat cheatObj, ImageView imageView, int imgResource){
        if (dicePosition.equals(LEFT)) cheatObj.setDiceLeft(imgResource);
        if (dicePosition.equals(CENTER)) cheatObj.setDiceCenter(imgResource);
        if (dicePosition.equals(RIGHT)) cheatObj.setDiceRight(imgResource);
        imageView.setImageResource(imgResource);

        saveList();

    }

    @Override
    public int getItemCount() {
        if(listCheats != null) return listCheats.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout rl_item_container;
        ImageView diceLeft, diceCenter, diceRight;
        ImageView iv_stop, iv_play, iv_clear;

        public ViewHolder(@NonNull View v) {
            super(v);

            rl_item_container = v.findViewById(R.id.rl_item_container);
            diceLeft = v.findViewById(R.id.iv_cheat_dice_left);
            diceCenter = v.findViewById(R.id.iv_cheat_dice_center);
            diceRight = v.findViewById(R.id.iv_cheat_dice_right);
            iv_stop = v.findViewById(R.id.iv_cheat_stop);
            iv_play = v.findViewById(R.id.iv_cheat_play);
            iv_clear = v.findViewById(R.id.iv_cheat_clear);
        }
    }





    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== AdapterCheat.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== AdapterCheat.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== AdapterCheat.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== AdapterCheat.java - line: " + n + " ==============================\n" + str);
    }
}

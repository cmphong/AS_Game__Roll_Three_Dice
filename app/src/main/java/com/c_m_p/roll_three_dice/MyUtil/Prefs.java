package com.c_m_p.roll_three_dice.MyUtil;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public Prefs(Context context, String keyName) {
        sharedPreferences = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void setString(String key,String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void setPremium(int value) {
        editor.putInt("Premium", value);
        editor.apply();
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPreferences.getBoolean(key, def);
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }


    public String getString(String key, String def) {
        return sharedPreferences.getString(key, def);
    }
    public long getLong(String key, long def) {
        return sharedPreferences.getLong(key, def);
    }

    public void clear(){
        editor.clear().apply();
    }

    public int getPremium() {
        return sharedPreferences.getInt("Premium", 0);
    }

    public boolean isRemoveAd(){
        return  getBoolean("isRemoveAd", false);
    }

    public void setIsRemoveAd(boolean value){
        editor.putBoolean("isRemoveAd",value);
        editor.apply();
    }

}
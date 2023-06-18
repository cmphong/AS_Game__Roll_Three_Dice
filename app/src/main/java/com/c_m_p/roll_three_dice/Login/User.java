package com.c_m_p.roll_three_dice.Login;

import android.text.TextUtils;

public class User {
    private String password;
    private String password_restore;

    public User(String password, String password_restore) {
        this.password = password;
        this.password_restore = password_restore;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_restore() {
        return password_restore;
    }

    public void setPassword_restore(String password_restore) {
        this.password_restore = password_restore;
    }

    public boolean isValidPassword(){
        return !TextUtils.isEmpty(password) && password.length() >= 6;

    }
    public boolean isValidPasswordRestore(){
        return !TextUtils.isEmpty(password_restore) && password_restore.length() >= 6;

    }
}

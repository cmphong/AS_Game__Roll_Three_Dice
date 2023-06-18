package com.c_m_p.roll_three_dice.Login;

public class PresenterLogin {

    LoginView loginView;

    public PresenterLogin(LoginView loginView) {
        this.loginView = loginView;
    }

    public void login(String password, String login_password){

        if (password.equalsIgnoreCase(login_password)) {
            loginView.loginSuccessful();
        } else {
            loginView.loginFail();
        }
    }
}

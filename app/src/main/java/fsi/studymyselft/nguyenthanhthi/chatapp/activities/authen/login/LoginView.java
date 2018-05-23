package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login;

import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseView;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.AuthView;

/**
 * Created by thanhthi on 23/05/2018.
 */

public interface LoginView extends BaseView, AuthView {

    public void showProgress();

    public void hideProgress();

    public void setUsernameError();

    public void setPasswordError();

    public void navigateToSignUp();

    public void navigateToHome();

}

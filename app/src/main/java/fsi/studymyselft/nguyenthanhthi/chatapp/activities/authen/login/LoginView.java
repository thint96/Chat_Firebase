package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login;

import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseView;

/**
 * Created by thanhthi on 23/05/2018.
 */

public interface LoginView extends BaseView {

    public void setUsernameError();

    public void setPasswordError();

    public void navigateToSignUp();

}

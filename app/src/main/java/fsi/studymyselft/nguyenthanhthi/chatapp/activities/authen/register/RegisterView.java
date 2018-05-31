package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.register;

import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseView;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.AuthView;

/**
 * Created by thanhthi on 23/05/2018.
 */

public interface RegisterView extends BaseView, AuthView {

    public void setUsernameError();

    public void setPasswordError();

    public void navigateToLogIn();

    public void navigateToHome();

}

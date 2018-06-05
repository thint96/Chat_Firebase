package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.register;

import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseView;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.AuthActivity;

/**
 * Created by thanhthi on 23/05/2018.
 */

public interface RegisterView extends BaseView {

    public void setUsernameError();

    public void setPasswordError();

    public void navigateToLogIn();

}

package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen;

import android.widget.Toast;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseActivity;

/**
 * Created by thanhthi on 23/05/2018.
 */

public abstract class AuthActivity extends BaseActivity {

    public void showAuthError() {
        Toast.makeText(getContext(), R.string.auth_error, Toast.LENGTH_LONG).show();
    }

}
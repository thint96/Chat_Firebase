package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.Toast;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.register.RegisterActivity;

/**
 * Created by thanhthi on 23/05/2018.
 */

public abstract class AuthActivity extends BaseActivity {

    public void showAuthError() {
        Toast.makeText(getContext(), R.string.auth_error, Toast.LENGTH_LONG).show();
    }

    public void navigateAuth(Context context, Class otherActivityClass) {
        Intent intent = new Intent(getContext(), otherActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void setUsernameError(TextInputLayout textInputLayoutEmail, TextInputEditText edtEmail) {
        String email = edtEmail.getText().toString().trim();

        if (email.equals("") || TextUtils.isEmpty(email)) { //the string is null or 0-length
            textInputLayoutEmail.setError(getString(R.string.email_can_not_be_blank));
        }
        else if (!email.contains("@")) {
            textInputLayoutEmail.setError(getString(R.string.invalid_email));
        }
    }

    public void setPasswordError(TextInputLayout textInputLayoutPassword, TextInputEditText edtPassword) {
        String password = edtPassword.getText().toString().trim();

        if (password.equals("") || TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError(getString(R.string.password_can_not_be_blank));
        }
        else if (password.length() < 6) {
            textInputLayoutPassword.setError(getString(R.string.password_must_have_min_6_characters));
        }
    }

    public Boolean hasError(TextInputLayout textInputLayoutEmail, TextInputLayout textInputLayoutPassword,
                            TextInputEditText edtEmail, TextInputEditText edtPassword) {
        Boolean hasError = false;

        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        //check email
        if (email.equals("") || TextUtils.isEmpty(email) || !email.contains("@")) {
            setUsernameError(textInputLayoutEmail, edtEmail);
            hasError = true;
        }

        //check password
        if (pass.equals("") || TextUtils.isEmpty(pass) || pass.length() < 6) {
            setPasswordError(textInputLayoutPassword, edtPassword);
            hasError = true;
        }

        return hasError;
    }
}
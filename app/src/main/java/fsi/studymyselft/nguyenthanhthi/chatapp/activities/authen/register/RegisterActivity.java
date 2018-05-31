package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login.LoginActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.InternetChecking;

public class RegisterActivity extends AppCompatActivity implements RegisterView, View.OnClickListener {

    private final String TAG = "RegisterActivity";

    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutConfirmPassword;
    private TextInputEditText edtEmail, edtPassword, edtConfirmPassword;
    private Button buttonRegister;
    private TextView goToLogin;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        bindViews();
    }

    @Override
    public void showAuthError() {
        Toast.makeText(getContext(), "Invalid username and password combination.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void bindViews() {
        showErrorInternetCheckingIfExist();

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.til_email);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.til_password);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.til_confirm_password);

        edtEmail = (TextInputEditText) findViewById(R.id.edt_email);
        edtPassword = (TextInputEditText) findViewById(R.id.edt_password);
        edtConfirmPassword = (TextInputEditText) findViewById(R.id.edt_confirm_password);

        buttonRegister = (Button) findViewById(R.id.btn_register);
        goToLogin = (TextView) findViewById(R.id.goToLogin);

        buttonRegister.setOnClickListener(this);
        goToLogin.setOnClickListener(this);
    }

    @Override
    public Context getContext() {
        return RegisterActivity.this;
    }

    @Override
    public void showErrorInternetCheckingIfExist() {
        InternetChecking.checkInternet(getContext(), TAG);
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(getContext(), "Registering", "Please wait...");
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void setUsernameError() {
        String email = edtEmail.getText().toString().trim();

        if (email.equals("") || TextUtils.isEmpty(email)) { //the string is null or 0-length
            textInputLayoutEmail.setError("Email can't be blank!");
        }
        else if (!email.contains("@")) {
            textInputLayoutEmail.setError("Invalid email!");
        }
    }

    @Override
    public void setPasswordError() {
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        boolean isPasswordFieldBlank = false;

        if (password.equals("") || TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError("Password can't be blank!");
            isPasswordFieldBlank = true;
        }

        if (confirmPassword.equals("") || TextUtils.isEmpty(confirmPassword)) {
            textInputLayoutConfirmPassword.setError("Confirm Password can't be blank!");
            isPasswordFieldBlank = true;
        }

        if (!confirmPassword.equals(password) && !isPasswordFieldBlank) {
            textInputLayoutConfirmPassword.setError("Password is not duplicated!");
        }
    }

    @Override
    public void navigateToLogIn() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToHome() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register) {
            register();
        } else if (v.getId() == R.id.goToLogin) {
            //go to Login Activity
            navigateToLogIn();
        }
    }

    /**
     * do action register when click button Register
     */
    private void register() {
        //input email and password of user
        String inputEmail = edtEmail.getText().toString().trim();
        String inputPass = edtPassword.getText().toString().trim();
        String inputPass2 = edtConfirmPassword.getText().toString().trim();

        if (!hasError(inputEmail, inputPass, inputPass2)) {
            showProgress();
            registerWithEmailPassword(inputEmail, inputPass);
        }
    }

    private boolean hasError(String email, String password, String confirmPassword) {
        Boolean hasError = false;

        //check email
        if (email.equals("") || TextUtils.isEmpty(email) || !email.contains("@")) {
            setUsernameError();
            hasError = true;
        }

        //check password
        if (password.equals("") || TextUtils.isEmpty(password) || !confirmPassword.equals(password)
                || confirmPassword.equals("") || TextUtils.isEmpty(confirmPassword)) {
            setPasswordError();
            hasError = true;
        }

        return hasError;
    }

    private void registerWithEmailPassword(final String email, final String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailPassword:success");
                            Toast.makeText(getContext(), "Register successfully!", Toast.LENGTH_SHORT).show();

                            //go to List User Activity
                            startActivity(new Intent(getContext(), ListUserActivity.class));

                            hideProgress();
                        }
                        else {
                            Log.w(TAG, "createUserEmailPassword:failure", task.getException());
                            Toast.makeText(getContext(), "Register failed!", Toast.LENGTH_SHORT).show();

                            showAuthError();
                            hideProgress();
                        }

                    }
                });
    }
}
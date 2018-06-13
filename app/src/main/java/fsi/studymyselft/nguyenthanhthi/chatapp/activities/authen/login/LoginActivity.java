package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.AuthActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.register.RegisterActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

public class LoginActivity extends AuthActivity implements LoginView, View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private TextInputEditText edtEmail, edtPassword;
    private Button buttonLogin;
    private TextView goToRegister;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference rootReference, usersReference;

    private ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            auth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    public void bindViews() {
        getSupportActionBar().hide();

        super.showErrorInternetCheckingIfExist(TAG);

        //check to auto-login
        checkUserHasSignedIn();

        userList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        usersReference = rootReference.child("Users");

        pushDataUsersToList();

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.til_email);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.til_password);

        edtEmail = (TextInputEditText) findViewById(R.id.edt_email);
        edtPassword = (TextInputEditText) findViewById(R.id.edt_password);

        buttonLogin = (Button) findViewById(R.id.btn_login);
        goToRegister = (TextView) findViewById(R.id.goToRegister);

        buttonLogin.setOnClickListener(this);
        goToRegister.setOnClickListener(this);
    }

    @Override
    public Context getContext() {
        return LoginActivity.this;
    }

    @Override
    public void setUsernameError() {
        String email = edtEmail.getText().toString().trim();

        if (email.equals("") || TextUtils.isEmpty(email)) { //the string is null or 0-length
            textInputLayoutEmail.setError(getString(R.string.email_can_not_be_blank));
        }
        else if (!email.contains("@")) {
            textInputLayoutEmail.setError(getString(R.string.invalid_email));
        }
    }

    @Override
    public void setPasswordError() {
        String password = edtPassword.getText().toString().trim();

        if (password.equals("") || TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError(getString(R.string.password_can_not_be_blank));
        }
        else if (password.length() < 6) {
            textInputLayoutPassword.setError(getString(R.string.password_must_have_min_6_characters));

        }
    }

    @Override
    public void navigateToSignUp() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            login();
        }
        else if (v.getId() == R.id.goToRegister) {
            //go to Register Activity
            navigateToSignUp();
        }
    }

    private void pushDataUsersToList() {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        userList.add(user);
                    }
                    Log.d(TAG, "total user in list user = " + userList.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * do action login when click button Login
     */
    private void login() {
        //input email and password of user
        String inputEmail = edtEmail.getText().toString().trim();
        String inputPass = edtPassword.getText().toString().trim();

        //set default email and password
        if (inputEmail.length() == 0 && inputPass.length() == 0) {
            setUsernameError();
            setPasswordError();

            String defaultEmail = "q@gmail.com";
            String defaultPassword = "123456";

            edtEmail.setText(defaultEmail);
            edtPassword.setText(defaultPassword);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int timeSleep = 0;
                        int totalSleep = 2000;
                        do {
                            Thread.sleep(100);
                            timeSleep += 100;
                        } while (timeSleep < totalSleep);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            login();
        }

        if (!hasError(inputEmail, inputPass)) {
            super.showProgress(getString(R.string.signing_in), getString(R.string.please_wait));
            loginWithEmailPassword(inputEmail, inputPass);
        }
    }

    private Boolean hasError(String email, String pass) {
        Boolean hasError = false;

        //check email
        if (email.equals("") || TextUtils.isEmpty(email) || !email.contains("@")) {
            setUsernameError();
            hasError = true;
        }

        //check password
        if (pass.equals("") || TextUtils.isEmpty(pass)) {
            setPasswordError();
            hasError = true;
        }

        return hasError;
    }

    private void loginWithEmailPassword(final String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailPassword:success:");
                            Toast.makeText(getContext(), R.string.login_successfully, Toast.LENGTH_SHORT).show();

                            //show greeting
                            Toast.makeText(getContext(), getString(R.string.welcome) + " " + email.toString(), Toast.LENGTH_SHORT).show();

                            //go to List Users Activity
                            startActivity(new Intent(getContext(), ListUserActivity.class));

                            LoginActivity.super.hideProgress();
                        }
                        else {
                            Log.w(TAG, "signInWithEmailPassword:failure", task.getException());
                            Toast.makeText(getContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();

                            LoginActivity.super.showAuthError();
                            LoginActivity.super.hideProgress();
                        }
                    }
                });
    }

    /**
     * check user has logged in but don't logout
     * if true then user mustn't login
     */
    private void checkUserHasSignedIn() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userSignedIn = firebaseAuth.getCurrentUser();
                if (userSignedIn != null) {
                    //user have login but don't logout
                    LoginActivity.super.showProgress(getString(R.string.loading), getString(R.string.please_wait));
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + userSignedIn.getUid());
                    startActivity(new Intent(LoginActivity.this, ListUserActivity.class));
                    LoginActivity.super.hideProgress();
                    finish();
                }
                else {
                    Log.d(TAG, "onAuthStateChanged:sign_out");
                }
            }
        };
    }
}
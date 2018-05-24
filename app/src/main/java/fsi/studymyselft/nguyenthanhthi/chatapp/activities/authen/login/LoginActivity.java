package fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.Random;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.register.RegisterActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

import static fsi.studymyselft.nguyenthanhthi.chatapp.R.color.gray;

public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener {

    private final String TAG = "LoginActivity";

    private EditText edtEmail, edtPassword;
    private Button buttonLogin;
    private TextView goToRegister;
    private ProgressBar progressBar;

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

        userList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        usersReference = rootReference.child("Users");

        bindViews();

        //check to auto-login
        checkUserHaveSignedIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (authStateListener != null) {
//            auth.removeAuthStateListener(authStateListener);
//        }
    }

    @Override
    public void showAuthError() {
        Toast.makeText(getContext(), "Invalid username and password combination.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void bindViews() {
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        buttonLogin = (Button) findViewById(R.id.btn_login);
        goToRegister = (TextView) findViewById(R.id.goToRegister);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        buttonLogin.setOnClickListener(this);
        goToRegister.setOnClickListener(this);
    }

    @Override
    public Context getContext() {
        return LoginActivity.this;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

        //disable the user interaction
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

        //get user interaction back
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void setUsernameError() {
        String email = edtEmail.getText().toString().trim();

        if (email.equals("") || TextUtils.isEmpty(email)) { //the string is null or 0-length
            edtEmail.setError("Email can't be blank!");
        } else if (!email.contains("@")) {
            edtEmail.setError("Invalid email!");
        }
    }

    @Override
    public void setPasswordError() {
        String password = edtPassword.getText().toString().trim();

        if (password.equals("") || TextUtils.isEmpty(password)) {
            edtPassword.setError("Password can't be blank!");
        } else if (password.length() < 6) {
            edtPassword.setError("Password must have more than 5 characters");
        }
    }

    @Override
    public void navigateToSignUp() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToHome() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            login();
        } else if (v.getId() == R.id.goToRegister) {
            //go to Register Activity
            navigateToSignUp();
        }
    }

    /**
     * do action login when click button Login
     */
    private void login() {
        //input email and password of user
        String inputEmail = edtEmail.getText().toString().trim();
        String inputPass = edtPassword.getText().toString().trim();

        //automatic login - set default email and password
        if (inputEmail.isEmpty() && inputPass.isEmpty()) {
            //check database
            if (usersReference == null) {
                Toast.makeText(getContext(), "Database Users have not created!", Toast.LENGTH_SHORT).show();
                return;
            }

            //push data users to userList
            pushDataUsersToList();

            int total = 0;
//            do {
                total = userList.size(); //total user records in database Users
//            } while (total > 0);

            //check total users in database
            if (total == 0) {
//                Toast.makeText(getContext(), "Do not have data users in database!", Toast.LENGTH_SHORT).show();
                return;
            }

            //random users in database to login
            Random rd = new Random();
            int index;
            do {
                index = rd.nextInt(total);
            } while (index < 0 || index >= total);
            User user = userList.get(index);
            inputEmail = user.getEmail();
            inputPass = "123456";
        }

        if (!hasError(inputEmail, inputPass)) {
            loginWithEmailPassword(inputEmail, inputPass);
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        showProgress();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailPassword:success:");
                            Toast.makeText(getContext(), "Login successfully!", Toast.LENGTH_SHORT).show();

                            //show greeting
                            Toast.makeText(getContext(), "Welcome " + email.toString(), Toast.LENGTH_SHORT).show();

                            //go to List Users Activity
                            startActivity(new Intent(getContext(), ListUserActivity.class));

                            hideProgress();
                        }
                        else {
                            Log.w(TAG, "signInWithEmailPassword:failure", task.getException());
                            Toast.makeText(getContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();

                            showAuthError();
                            hideProgress();
                        }
                    }
                });
    }

    /**
     * check user have login but don't logout
     * if true then user must'n login
     */
    private void checkUserHaveSignedIn() {
//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser userSignedIn = firebaseAuth.getCurrentUser();
//                if (userSignedIn != null) {
//                    //user have login but don't logout
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + userSignedIn.getUid());
//                    startActivity(new Intent(LoginActivity.this, ListUserActivity.class));
//                    finish();
//                }
//                else {
//                    Log.d(TAG, "onAuthStateChanged:sign_out");
//                }
//            }
//        };
    }

}
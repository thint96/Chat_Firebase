package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button buttonLogin;
    private TextView goToRegister;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        buttonLogin = findViewById(R.id.btn_login);
        goToRegister = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();

        //go to Register Activity
        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email and password of user
                String inputEmail = edtEmail.getText().toString().trim();
                String inputPass = edtPassword.getText().toString().trim();

                if (!hasError(inputEmail, inputPass)) {
                    loginWithEmailPassword(inputEmail, inputPass);
                }
            }
        });

        //check user have login but don't logout
        //if true then user must'n login
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

    @Override
    protected void onStart() {
        super.onStart();
//        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
//            auth.removeAuthStateListener(authStateListener);
        }
    }

    private void loginWithEmailPassword(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailPassword:success:");
                            Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();

                            //go to List Users Activity
                            startActivity(new Intent(LoginActivity.this, ListUserActivity.class));
                        }
                        else {
                            Log.w(TAG, "signInWithEmailPassword:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.login_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Boolean hasError(String email, String pass) {
        Boolean hasError = false;

        //check email
        if (email.equals("") || TextUtils.isEmpty(email)) { //the string is null or 0-length
            edtEmail.setError("Email can't be blank!");
            hasError = true;
        }
        else if (!email.contains("@")) {
            edtEmail.setError("Invalid email!");
            hasError = true;
        }

        //check password
        if (pass.equals("") || TextUtils.isEmpty(pass)) {
            edtPassword.setError("Password can't be blank!");
            hasError = true;
        }

        return hasError;
    }

}

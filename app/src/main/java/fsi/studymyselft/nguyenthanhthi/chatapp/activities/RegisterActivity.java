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

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";

    private EditText edtEmail, edtPassword, edtPassword2;
    private Button buttonRegister;
    private TextView goToLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtPassword2 = findViewById(R.id.edt_password_again);
        buttonRegister = findViewById(R.id.btn_register);
        goToLogin = findViewById(R.id.login);

        auth = FirebaseAuth.getInstance();

        //go to Login Activity
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email and password of user
                String inputEmail = edtEmail.getText().toString().trim();
                String inputPass = edtPassword.getText().toString().trim();
                String inputPass2 = edtPassword2.getText().toString().trim();

                if (!hasError(inputEmail, inputPass, inputPass2)) {
                    registerWithEmailPassword(inputEmail, inputPass);
                }
            }
        });
    }

    private void registerWithEmailPassword(final String email, final String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailPassword:success");
                            Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();

                            //go to Chat Activity
                            startActivity(new Intent(RegisterActivity.this, ListUserActivity.class));
                        }
                        else {
                            Log.w(TAG, "createUserEmailPassword:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private boolean hasError(String email, String password, String password2) {
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
        if (password.equals("") || TextUtils.isEmpty(password)) {
            edtPassword.setError("Password can't be blank!");
            hasError = true;
        }
        if (!password2.equals(password)) {
            edtPassword2.setError("Password is not duplicated!");
            hasError = true;
        }

        return hasError;
    }
}

package fsi.studymyselft.nguyenthanhthi.chatapp.activities.login;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.RegisterActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button buttonLogin;
    private TextView goToRegister;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference rootReference, usersReference;

    private ArrayList<User> userList;

    private final String TAG = "LoginActivity";
    private final int totalProgressTime = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        buttonLogin = findViewById(R.id.btn_login);
        goToRegister = findViewById(R.id.register);

        userList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        usersReference = rootReference.child("Users");

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
                showProgressBar();
            }
        });

    }

    private void showProgressBar() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                int jumpTime = 0;

                loginWithEmailPassword();

                while(jumpTime < totalProgressTime) {
                    try {
                        Thread.sleep(500);
                        jumpTime += 20;
                        progressDialog.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                progressDialog.dismiss();
            }
        }).start();
    }

    private void loginWithEmailPassword() {
        //input email and password of user
        String inputEmail = edtEmail.getText().toString().trim();
        String inputPass = edtPassword.getText().toString().trim();

        //automatic login - set default email and password
        if (inputEmail.isEmpty() && inputPass.isEmpty()) {
            //check database
            if (usersReference == null) {
                Toast.makeText(LoginActivity.this, "Database Users have not created!", Toast.LENGTH_SHORT).show();
                return;
            }

            //push data users to userList
            pushDataUsersToList();
            int total = userList.size(); //total user records in database Users

            //check total users in database
            if (total == 0) {
//                        Toast.makeText(LoginActivity.this, "Do not have data users in database!", Toast.LENGTH_SHORT).show();
                return;
            }

            Random rd = new Random(total);
            int index = rd.nextInt(total);
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

    private void loginWithEmailPassword(final String email, final String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailPassword:success:");
                            Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();

                            //show greeting
                            Toast.makeText(LoginActivity.this, "Wellcome " + email.toString(), Toast.LENGTH_SHORT).show();

                            //go to List Users Activity
                            startActivity(new Intent(LoginActivity.this, ListUserActivity.class));

                        }
                        else {
                            Log.w(TAG, "signInWithEmailPassword:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.login_failed,
                                    Toast.LENGTH_SHORT).show();
                            finish();
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

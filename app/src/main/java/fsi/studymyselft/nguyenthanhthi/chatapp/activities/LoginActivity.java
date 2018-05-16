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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button buttonLogin;
    private TextView goToRegister;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference rootReference, usersReference;

    private ArrayList<User> userList;

    private final String TAG = "LoginActivity";

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

    private void loginWithEmailPassword(final String email, String password) {
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

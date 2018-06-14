package fsi.studymyselft.nguyenthanhthi.chatapp.activities.detailInfoUser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat.ChatActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

public class DetailInfoUserActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DetailInfoUserActivity";

    private ImageView imgAvatar;
    private ImageButton imgButtonCall, imgButtonSendSms;
    private TextView txtUsername, txtEmail, txtPhoneNumber, txtAddress, txtPosition;

    private User otherUser;

    private DatabaseReference rootReference, usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info_user);

        initData();
        bindViews();
    }

    private void initData() {
        //get information of other User from Intent
        if (getIntent().getExtras() == null) {
            Toast.makeText(getContext(), "Can not get data other user from ChatActivity", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Can not get data other user from ChatActivity");
            navigateToChatActivity();
            return;
        }
        otherUser = (User) getIntent().getSerializableExtra("OtherUser");
        Log.e(TAG, "initData(): " + otherUser.getId() + " - " + otherUser.getEmail() + " - "
                + otherUser.getAvatar());

        getInfoOtherUserFromDB();
    }

    private void getInfoOtherUserFromDB() {
        rootReference = FirebaseDatabase.getInstance().getReference();
        if (rootReference.child(getString(R.string.USERS_DATABASE)) == null) {
            rootReference.setValue(getString(R.string.USERS_DATABASE));
        }
        usersReference = rootReference.child(getString(R.string.USERS_DATABASE));

        rootReference.child(getString(R.string.USERS_DATABASE)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (user.getId().equals(otherUser.getId())) {
                            otherUser = user;
                            otherUser.setEmail(user.getEmail());
                            otherUser.setAvatar(user.getAvatar());
                            Log.e(TAG, otherUser.getId() + " - " + otherUser.getEmail() + " - "
                                    + otherUser.getAvatar());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void bindViews() {
        setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findControl();
        setData();

        imgButtonCall.setOnClickListener(this);
        imgButtonSendSms.setOnClickListener(this);
    }

    private void findControl() {
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        imgButtonCall = (ImageButton) findViewById(R.id.img_button_phone);
        imgButtonSendSms = (ImageButton) findViewById(R.id.img_button_send_sms);
        txtUsername = (TextView) findViewById(R.id.txtUserName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtPosition = (TextView) findViewById(R.id.txtPosition);
    }

    private void setData() {
        if (otherUser == null) return;
        Picasso.with(getContext()).load(otherUser.getAvatar()).resize(100, 100).into(imgAvatar);
        if (otherUser.getName() == null) {
            if (otherUser.getEmail() == null) return;
            int end = otherUser.getEmail().indexOf("@");
            String name = otherUser.getEmail().substring(0, end);
            txtUsername.setText(standardize(name));
        }
        else {
            txtUsername.setText(standardize(otherUser.getName()));
        }
        txtEmail.setText(standardize(otherUser.getEmail()));
        txtPhoneNumber.setText("null");
        txtAddress.setText("null");
        txtPosition.setText("null");
    }

    private String standardize(String s) {
        return s != null ? s : "null";
    }

    private String standardizePhoneNumber(String phoneNumber) {
        return phoneNumber.length() != 0 ? phoneNumber : "18008168";
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_button_phone) {
            callThroughSim();
        }
        else if (v.getId() == R.id.img_button_send_sms) {
            sendSms();
        }
    }

    private void callThroughSim() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + standardizePhoneNumber("")));

        checkPermission("call");
        startActivity(callIntent);
    }

    private void sendSms() {
        Intent sendSmsIntent = new Intent(Intent.ACTION_VIEW);
        sendSmsIntent.setData(Uri.parse("sms:" + standardizePhoneNumber("")));

        checkPermission("send");
        startActivity(sendSmsIntent);
    }

    private void checkPermission(String action) {
        switch (action) {
            case "call":
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //nếu quyền gọi điện thoại chưa được gán
                    //thì show dialog hỏi mở quyền
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE}, 100);
                }
                break;
            case "send":
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    //nếu quyền gọi điện thoại chưa được gán
                    //thì show dialog hỏi mở quyền
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS}, 200);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nếu quyền đã được gán
                    //do nothing
                }
                else {
                    checkPermission("call");
                }
                return;
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nếu quyền đã được gán
                    //do nothing
                }
                else {
                    checkPermission("send");
                }
                return;
        }
    }

    @Override
    public Context getContext() {
        return DetailInfoUserActivity.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateToChatActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * come back Chat Activity
     */
    public void navigateToChatActivity() {
        Log.e(TAG, otherUser.getId() + " - " + otherUser.getEmail() + " - "
                + otherUser.getAvatar());
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("OtherUser", otherUser);
        setResult(10);
        finish();
    }
}

package fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseMainActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat.ChatActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.adapter.ListUserAdapter;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.InternetChecking;

public class ListUserActivity extends BaseMainActivity implements ListUserView {

    private static final String TAG = "ListUserActivity";
    private static final String USERS_DATABASE = "Users";

    private ListView lvUsers;
    private ArrayList<User> users;
    private ListUserAdapter adapter;

    private ProgressDialog progressDialog;
    private Menu menu;

    private String newUserID;

    private FirebaseDatabase database;
    private DatabaseReference rootReference, userReference;
    private FirebaseUser currentUser;

    private User newUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        bindViews();
    }

    @Override
    public void bindViews() {
        showErrorInternetCheckingIfExist(TAG);

        showProgress(getString(R.string.loading), getString(R.string.please_wait));

        showUsersList();

        hideProgress();

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("EMAIL", users.get(position).getEmail());
                intent.putExtra("ID", users.get(position).getId());
                intent.putExtra("AVATAR", users.get(position).getAvatar());
                startActivity(intent);
            }
        });
    }

    @Override
    public Context getContext() {
        return ListUserActivity.this;
    }

    @Override
    public void showUsersList() {
        users = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        if (rootReference.child(USERS_DATABASE) == null) rootReference.setValue(USERS_DATABASE);
        userReference = rootReference.child(USERS_DATABASE);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setTitle("List members - " + currentUser.getEmail());

        auth = FirebaseAuth.getInstance();

        lvUsers = (ListView) findViewById(R.id.lvUsers);

        //update database users if current user have already registered
        updateNewUserToDatabase();

        //get all users from database to list "users"
        pushDataUsersToListUsers();

        adapter = new ListUserAdapter(getContext(), users);
        lvUsers.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void updateNewUserToDatabase() {
        String avatar = "http://pluspng.com/img-png/png-doraemon-doraemon-png-180.png";
        User newUser = new User(currentUser.getUid().toString(), currentUser.getEmail().toString(), avatar);
        userReference.child(currentUser.getUid()).setValue(newUser);
    }

    private void pushDataUsersToListUsers() {
        userReference.orderByChild("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //push data user from database into list users
                    users.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if (user.getId() != currentUser.getUid() && !user.getEmail().equals(currentUser.getEmail())) { //do not let current user chat with yourself
                            users.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
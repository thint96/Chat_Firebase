package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import fsi.studymyselft.nguyenthanhthi.chatapp.adapter.ListUserAdapter;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

public class ListUserActivity extends AppCompatActivity {

    private final String TAG = "ListUserActivity";

    private ListView lvUsers;
    private ArrayList<User> users;
    private ListUserAdapter adapter;

    private String newUserID;

    private FirebaseDatabase database;
    private DatabaseReference rootReference, userReference;
    private FirebaseUser currentUser;

    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        setTitle("ListUserActivity");

        users = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        if (rootReference.child("Users") == null) rootReference.setValue("Users");
        userReference = rootReference.child("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        lvUsers = (ListView) findViewById(R.id.lvUsers);

        //update database users if current user have already registered
        updateNewUserToDatabase();

        //get all users from database to list "users"
        pushDataUsersToListUsers();

        adapter = new ListUserAdapter(ListUserActivity.this, users);
        lvUsers.setAdapter(adapter);

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListUserActivity.this, ChatActivity.class);
                intent.putExtra("EMAIL", users.get(position).getEmail());
                intent.putExtra("ID", users.get(position).getId());
                startActivity(intent);
            }
        });

    }

    private void updateNewUserToDatabase() {
        User newUser = new User(currentUser.getUid().toString(), currentUser.getEmail().toString(), true);
        userReference.child(currentUser.getUid()).setValue(newUser);
    }

    private void pushDataUsersToListUsers() {
        userReference.addValueEventListener(new ValueEventListener() {
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

package fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.MessageRecent;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

public class ListUserActivity extends BaseMainActivity implements ListUserView {

    private static final String TAG = "ListUserActivity";
    private static final String USERS_DATABASE = "Users";

    private ListView lvUsers;
    private ListUserAdapter adapter;

    private String newUserID;
    private User newUser;

    private ArrayList<User> users;
    private ArrayList<MessageRecent> messageRecentList;
    private ArrayList<Item> items;

    private FirebaseDatabase database;
    private DatabaseReference rootReference, userReference, messageRecentReference;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        bindViews();
    }

    @Override
    public void bindViews() {
        super.showErrorInternetCheckingIfExist(TAG);

        setTitle("Danh sách cuộc trò chuyện");

        super.showProgress(getString(R.string.loading), getString(R.string.please_wait));

        showUsersList();

        super.hideProgress();

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
        messageRecentList = new ArrayList<>();
        items = new ArrayList<>();

        lvUsers = (ListView) findViewById(R.id.lvUsers);

        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();

        //set reference for Users database
        if (rootReference.child(USERS_DATABASE) == null) {
            rootReference.setValue(USERS_DATABASE);
        }
        userReference = rootReference.child(USERS_DATABASE);

        //get information of current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //update database users if current user have already registered
        updateNewUserToDatabase();

        //get all users from database to list "users"
        pushDataUsersToListUsers();

        adapter = new ListUserAdapter(getContext(), items);

        lvUsers.setAdapter(adapter);
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
//                            adapter.notifyDataSetChanged();
                        }
                    }

                    //push all recent message to list messageRecentList
                    pushDataMessageRecentToListMessageRecent();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pushDataMessageRecentToListMessageRecent() {
        //set reference for Message Recent database
        if (rootReference.child(getString(R.string.MESSAGE_RECENT_DATABASE)) == null) {
            rootReference.setValue(getString(R.string.MESSAGE_RECENT_DATABASE));
        }
        messageRecentReference = rootReference.child(getString(R.string.MESSAGE_RECENT_DATABASE));

        messageRecentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //push data from database to list message recent
                    messageRecentList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MessageRecent messageRecent = snapshot.getValue(MessageRecent.class);
                        messageRecentList.add(messageRecent);
                    }
                    Log.e(TAG, "total item in list message recent: " + messageRecentList.size());

                    setMessageRecentForUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMessageRecentForUser() {
        for (User otherUser : users) {
            Item item = new Item();
            item.setUser(otherUser);

            for (MessageRecent messageRecent : messageRecentList) {
                String id = messageRecent.getId();
                if (id.contains(otherUser.getId())) {
                    item.setRecentMessage(messageRecent.getMessage());
                    break;
                }
            }

            if (item.getRecentMessage() == null) {
                Message recentMessage = new Message();
                recentMessage.setText("Chưa có cuộc trò chuyện");
                item.setRecentMessage(recentMessage);
            }

            items.add(item);
        }

        int size = items.size();
    }

    public class Item {

        private User user;
        private Message recentMessage;

        public Item() {
        }

        public Item(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Message getRecentMessage() {
            return recentMessage;
        }

        public void setRecentMessage(Message recentMessage) {
            this.recentMessage = recentMessage;
        }
    }
}
package fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import fsi.studymyselft.nguyenthanhthi.chatapp.data.ItemListDialog;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.MessageRecent;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.LocationUpdating;

public class ListUserActivity extends BaseMainActivity {

    private static final String TAG = "ListUserActivity";
    private static final String USERS_DATABASE = "Users";

    private ListView lvUsers;
    private ListUserAdapter adapter;

    private String newUserID;
    private User newUser;

    private ArrayList<User> users;
    private ArrayList<MessageRecent> messageRecentList;
    private ArrayList<ItemListDialog> items;

    private FirebaseUser currentUser;
    private DatabaseReference rootReference, userReference, messageRecentReference;

    private LocationUpdating locationUpdating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        bindViews();

    }

    @Override
    public void bindViews() {
        super.showErrorInternetCheckingIfExist(TAG);

        setTitle(getString(R.string.title_of_list_user_activity));

        showProgress(getString(R.string.loading), getString(R.string.please_wait));

        locationUpdating = new LocationUpdating(getContext());

        showUsersList();

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //go to ChatActivity
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("OtherUser", users.get(position));
                startActivity(intent);
            }
        });
    }

    public void showUsersList() {
        users = new ArrayList<>();
        messageRecentList = new ArrayList<>();
        items = new ArrayList<>();
        lvUsers = (ListView) findViewById(R.id.lvUsers);
        locationUpdating = new LocationUpdating(getContext());

        adapter = new ListUserAdapter(getContext(), items);
        lvUsers.setAdapter(adapter);

        //get information of current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //set reference for Users database
        rootReference = FirebaseDatabase.getInstance().getReference();
        if (rootReference.child(USERS_DATABASE) == null) {
            rootReference.setValue(USERS_DATABASE);
        }
        userReference = rootReference.child(USERS_DATABASE);

        //update database users if current user have already registered
        updateNewUserToDatabase();

        //get all users from database to list "users"
        pushDataUsersToListUsers();

        // Save the ListView state (= includes scroll position) as a Parcelable
        Parcelable state = lvUsers.onSaveInstanceState();


        // Restore previous state (including selected item index and scroll position)
        lvUsers.onRestoreInstanceState(state);
    }

    private void updateNewUserToDatabase() {
        String avatar = "http://pluspng.com/img-png/png-doraemon-doraemon-png-180.png";
        User newUser = new User(currentUser.getUid().toString(), currentUser.getEmail().toString(), avatar);

        //set name for current user
        int end = newUser.getEmail().indexOf("@");
        String name = newUser.getEmail().substring(0, end);
        newUser.setName(name);

        //update position for current user
        String position = locationUpdating.getPositionInOneLine();
        newUser.setPosition(position);

        userReference.child(currentUser.getUid()).setValue(newUser);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 500:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //nếu quyền đã được gán
                    //do nothing
                }
                else {
                    locationUpdating.checkPermission();
                }
                return;
        }
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
                    adapter.notifyDataSetChanged();
                    hideProgress();

                    Log.e(TAG, "End of getting data from database");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMessageRecentForUser() {
        for (User otherUser : users) {
            ItemListDialog item = new ItemListDialog();
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

    @Override
    public Context getContext() {
        return ListUserActivity.this;
    }
}
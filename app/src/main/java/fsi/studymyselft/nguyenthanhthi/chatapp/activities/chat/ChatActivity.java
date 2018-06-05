package fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat.holders.CustomIncomingTextMessageViewHolder;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat.holders.CustomOutcomingTextMessageViewHolder;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Dialog;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.InternetChecking;

public class ChatActivity extends BaseActivity
        implements ChatView, MessageInput.InputListener {

    private static final String TAG = "ChatActivity";

    private MessagesList messagesList; //UI - widget
    private MessageInput messageInput; //UI - widget
    private ProgressDialog progressDialog;
    private Menu menu;

    private Dialog myDialog;
    private User myUser, otherUser;
    private Message newMessage;

    private MessagesListAdapter<Message> messagesAdapter;

    private ImageLoader imageLoader;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference rootReference, dialogsReference, myDialogReference,
            messagesReference, membersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindViews();
    }

    @Override
    public void bindViews() {
        showErrorInternetCheckingIfExist(TAG);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        messageInput = (MessageInput) findViewById(R.id.input);

        myDialog = new Dialog();

        //get information of current user - myUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myUser = new User(currentUser.getUid(), currentUser.getEmail());
        myUser.setOnline(true);


        //get information of other User from Intent
        String otherUserId = getIntent().getStringExtra("ID");
        String otherUserEmail = getIntent().getStringExtra("EMAIL");
        String otherUserAvatar = getIntent().getStringExtra("AVATAR");
        otherUser = new User(otherUserId, otherUserEmail, otherUserAvatar);

        //set name of dialog and show UI
        getSupportActionBar().setTitle(otherUserEmail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //update information users to myDialog
        myDialog.addUserToListUsers(myUser);
        myDialog.addUserToListUsers(otherUser);

        initMessageAdapter();

        showMessagesList();

        //validate and send a new message
        messageInput.setInputListener(this);
    }

    @Override
    public Context getContext() {
        return ChatActivity.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.copy) {
            copyToClipBoard();
            return true;
        }
        else if (item.getItemId() == R.id.delete) {
            deleteMessage();
            return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            navigateToListUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deleteMessage() {

    }

    @Override
    public void copyToClipBoard() {

    }

    @Override
    public void showMessagesList() {
        //get reference of root database
        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();

        //get reference of Dialog Database
        if (rootReference.child("Dialogs") == null) {
            rootReference.setValue("Dialogs");
        }
        dialogsReference = rootReference.child("Dialogs");

        //set reference of my dialog in database and get messages in this dialog
        setReferenceToMyDialog();

        messagesList.setAdapter(messagesAdapter);
    }

    @Override
    public void navigateToListUser() {
        Intent intent = new Intent(getContext(), ListUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setReferenceToMyDialog() {
        final String dialogName = (myUser.getId() + "|" + otherUser.getId());
        final String reverseDialogName = (otherUser.getId() + "|" + myUser.getId());
        myDialog.setName(dialogName);

        dialogsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isMyDialogExist = false;

                if (dataSnapshot.exists()) { //if dialog database is exist
                    for (DataSnapshot dialogSnapshot : dataSnapshot.getChildren()) {
                        Dialog dialog = dialogSnapshot.getValue(Dialog.class);

                        //check existence of my dialog
                        if (dialog.getDialogName().equals(dialogName) || dialog.getDialogName().equals(reverseDialogName)) {
                            if (dialog.getDialogName().equals(reverseDialogName)) {
                                myDialog.setName(reverseDialogName); //rename of my dialog if necessary
                            }
                            myDialog.setId(dialog.getId());
                            isMyDialogExist = true;
                            break;
                        }
                    }
                }

                if (!isMyDialogExist) {
                    //create new node in Dialog Database to save my dialog information
                    String key = dialogsReference.push().getKey();
                    myDialog.setId(key);
                    dialogsReference.child(key).setValue(myDialog);
                }

                myDialogReference = dialogsReference.child(myDialog.getId());

                //get all messages in my dialog database
                getAllMessagesDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllMessagesDialog() {
        if (myDialogReference.child("Messages") == null) {
            myDialogReference.setValue("Messages");
        }
        messagesReference = myDialogReference.child("Messages");

        messagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Total count of messages in my dialog database = " + dataSnapshot.getChildrenCount());
                    Log.d(TAG, "Total count of messages in messageAdapter = " + messagesAdapter.getItemCount());
                    Log.d(TAG, "Total count of messages in my Dialog = " + myDialog.getMessages().size());

                    if (messagesAdapter.getItemCount() != dataSnapshot.getChildrenCount()) {
                        messagesAdapter.delete(myDialog.getMessages());
                        myDialog.removeAllMessagesFromListMessages();
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            Message message = messageSnapshot.getValue(Message.class);

                            //set avatar for other user to show UI
                            if (currentUser.getUid().equals(message.getUser().getId())) {
                                //if this message is belong to my user
                                message.getUser().setAvatar("");
                            } else { //if this message is belong to other user
                                message.getUser().setAvatar(otherUser.getAvatar());
                            }

                            myDialog.addMessageToListMessages(message);
                            messagesAdapter.updateNewMessage(message);
                            messagesAdapter.notifyDataSetChanged();
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
    public void initMessageAdapter() {
        initImageLoader(imageLoader);

        MessageHolders messageHolders = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message);

        messagesAdapter = new MessagesListAdapter<Message>(currentUser.getUid(), messageHolders, imageLoader);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        String messageText = String.valueOf(input);

        if (messagesReference == null || messageText.isEmpty() || messageText.equals("")) {
            Toast.makeText(this, "Update new message to database failure", Toast.LENGTH_SHORT).show();
            return false;
        }

        //push new message to database
        String key = messagesReference.push().getKey();
        newMessage = new Message(key, messageText, myUser);
        messagesReference.child(key).setValue(newMessage);

        return true;
    }
}
package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Dialog;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.holders.CustomIncomingTextMessageViewHolder;
import fsi.studymyselft.nguyenthanhthi.chatapp.holders.CustomOutcomingTextMessageViewHolder;

public class ChatActivity extends AppCompatActivity
        implements MessageInput.InputListener {

    private MessagesList messagesList; //UI - widget
    private MessageInput messageInput; //UI - widget

    private Dialog myDialog;
    private User myUser, otherUser;
    private Message newMessage;

    private MessagesListAdapter<Message> messagesAdapter;

    private ImageLoader imageLoader;

    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference rootReference, dialogsReference, myDialogReference,
            messagesReference, membersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        messageInput = (MessageInput) findViewById(R.id.input);

        myDialog = new Dialog();

        //get information of current user - myUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String myAvatarUrl = "http://vi.fanpop.com/clubs/doraemon/answers/show/484361/post-pic-doraemon-with-nobita";
        myUser = new User(currentUser.getUid(), currentUser.getEmail(), myAvatarUrl);

        //get information of other User from Intent
        String otherUserId = getIntent().getStringExtra("ID");
        String otherUserEmail = getIntent().getStringExtra("EMAIL");
        String otherAvatarUrl = "http://www.socimage.net/user/doraemon_mychildhood/1757399948/1069505800034932735_1757399948";
        otherUser = new User(otherUserId, otherUserEmail, otherAvatarUrl);

        //set name of dialog and show UI
        getSupportActionBar().setTitle(otherUserEmail);

        //update information users to myDialog
        myDialog.addUserToListUsers(myUser);
        myDialog.addUserToListUsers(otherUser);

        initMessageAdapter();

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

        //validate and send message
        messageInput.setInputListener(this);

    }

    private void setReferenceToMyDialog() {
        final String dialogName = (myUser.getId() + "|" + otherUser.getId());
        final String reverseDialogName = (otherUser.getId() + "|" + myUser.getId());
        myDialog.setName(dialogName);

        dialogsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isMyDialogExist = false;
                if (dataSnapshot.exists()) {
                    //if dialog database is exist

                    for (DataSnapshot dialogSnapshot : dataSnapshot.getChildren()) {
                        Dialog dialog = dialogSnapshot.getValue(Dialog.class);

                        //check existence of my dialog
                        if (dialog.getName().equals(dialogName) || dialog.getName().equals(reverseDialogName)) {
                            if (dialog.getName().equals(reverseDialogName)) {
                                myDialog.setName(reverseDialogName); //rename my dialog
                            }
                            isMyDialogExist = true;
                            myDialog.setId(dialog.getId());
                            break;
                        }
                    }
                }

                System.out.println(myDialog.getName());
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
                    messagesAdapter.delete(myDialog.getMessages());
                    myDialog.removeAllMessagesFromListMessages();

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);

                        System.out.println("Count of messages in database = " + dataSnapshot.getChildrenCount());

                        myDialog.addMessageToListMessages(message);

                        messagesAdapter.addToStart(message, true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initMessageAdapter() {
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(ChatActivity.this).load(url).into(imageView);
            }
        };

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
            Toast.makeText(this, "udate new message to database fail", Toast.LENGTH_SHORT).show();
            return false;
        }

        //push new message to database
        String key = messagesReference.push().getKey();
        newMessage = new Message(key, messageText, myUser);
        messagesReference.child(key).setValue(newMessage);

        return true;
    }

}
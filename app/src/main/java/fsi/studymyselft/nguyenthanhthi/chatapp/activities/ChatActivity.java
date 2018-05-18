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
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.holders.CustomIncomingTextMessageViewHolder;
import fsi.studymyselft.nguyenthanhthi.chatapp.holders.CustomOutcomingTextMessageViewHolder;

public class ChatActivity extends AppCompatActivity
        implements MessageInput.InputListener {

    private MessagesList messagesList; //UI - widget
    private MessageInput messageInput; //UI - widget

    private String userSendRef, userReceiveRef;

    private Message newMessage;
    private ArrayList<Message> messagesUserSend, messagesUserReceive, messages;
    private MessagesListAdapter<Message> messagesAdapter;

    private FirebaseDatabase database;
    private DatabaseReference rootReference, messagesReference, messagesUserReceiveReference, messagesUserSendReference;
    private FirebaseUser currentUser; // <=> userSend

    private User myUser, otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesList = findViewById(R.id.messagesList);
        messageInput = findViewById(R.id.input);

        messagesUserSend = new ArrayList<>();
        messagesUserReceive = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        if (rootReference.child("Messages") == null) {
            rootReference.setValue("Messages");
        }
        messagesReference = rootReference.child("Messages");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myUser = new User(currentUser.getUid(), currentUser.getEmail());

        //get information of user receive message
        String userReceiveId = getIntent().getStringExtra("ID").toString();
        String userReceiveEmail = getIntent().getStringExtra("EMAIL").toString();
        otherUser = new User(userReceiveId, userReceiveEmail);

        getSupportActionBar().setTitle(userReceiveEmail);

        //set child node of "Messages" node is email and cut tail "@..."
        //with messages of User Receive
        int indexEnd = userReceiveEmail.indexOf("@");
        userReceiveRef = userReceiveEmail.substring(0, indexEnd);
        if (messagesReference.child(userReceiveRef) == null) {
            messagesReference.setValue(userReceiveRef);
        }
        messagesUserReceiveReference = messagesReference.child(userReceiveRef);

        //with messages of User Send
        indexEnd = myUser.getEmail().indexOf("@");
        String userSendRef = myUser.getEmail().substring(0, indexEnd);
        if (messagesReference.child(userSendRef) == null) {
            messagesReference.setValue(userSendRef);
        }
        messagesUserSendReference = messagesReference.child(userSendRef);

//        initMessageAdapter();

        //validate and send message
        messageInput.setInputListener(this);

        //get all messages from database to list "Messages"
//        pushDataMessagesToListMessages();

    }

//    private void pushDataMessagesToListMessages() {
//        messagesUserSendReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    messagesUserReceive.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Message message = snapshot.getValue(Message.class);
//                        if (message.getUser().getName() == userSendRef) {
//                            messagesUserReceive.add(message);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        messagesUserReceiveReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    messagesAdapter.delete(messagesUserSend);
//                    messagesUserSend.clear();
//                    int i = 0;
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        Message message = data.getValue(Message.class);
//                        messagesUserSend.add(message);
//
//                        Toast.makeText(ChatActivity.this, ++i + " - " + message.getText(), Toast.LENGTH_SHORT).show();
//                        messagesAdapter.addToStart(message, true);
//                    }
//                }
//
//                messagesList.setAdapter(messagesAdapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    private void initMessageAdapter() {
//        MessageHolders messageHolders = new MessageHolders()
//                .setIncomingTextConfig(
//                        CustomIncomingTextMessageViewHolder.class,
//                        R.layout.item_custom_incoming_text_message)
//                .setOutcomingTextConfig(
//                        CustomOutcomingTextMessageViewHolder.class,
//                        R.layout.item_custom_outcoming_text_message);
//
//        messagesAdapter = new MessagesListAdapter<Message>(currentUser.getUid(), messageHolders, null);
//    }
//
    @Override
    public boolean onSubmit(CharSequence input) {
//        String messageText = String.valueOf(input);
//        if (messagesUserReceiveReference == null || messageText.isEmpty() || messageText.equals("")) {
//            Toast.makeText(this, "udate new message to database fail", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        //push new message to database
//        String key = messagesUserReceiveReference.push().getKey();
//        newMessage = new Message(key, messageText, userSend);
//        messagesUserReceiveReference.child(key).setValue(newMessage);
        return true;
    }

}

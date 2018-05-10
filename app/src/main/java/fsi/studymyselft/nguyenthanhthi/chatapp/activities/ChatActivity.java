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

    private MessagesList messagesList; //UI
    private MessageInput messageInput;

    private Message newMessage;
    private ArrayList<Message> messages;
    private MessagesListAdapter<Message> messagesAdapter;

    private FirebaseDatabase database;
    private DatabaseReference rootReference, messagesReference, messagesUserReceiveReference;
    private FirebaseUser currentUser;

    private User userSend, userReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesList = findViewById(R.id.messagesList);
        messageInput = findViewById(R.id.input);

        messages = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        if (rootReference.child("Messages") == null) {
            rootReference.setValue("Messages");
        }
        messagesReference = rootReference.child("Messages");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userSend = new User(currentUser.getUid(), currentUser.getEmail());

        //get information of user receive message
        String userReceiveId = getIntent().getStringExtra("ID");
        String userReceiveEmail = getIntent().getStringExtra("EMAIL");
        userReceive = new User(userReceiveId, userReceiveEmail);

        if (messagesReference.child(userReceiveId) == null) {
            messagesReference.setValue(userReceiveId);
        }
        messagesUserReceiveReference = messagesReference.child(userReceiveId);

        initMessageAdapter();

        //validate and send message
        messageInput.setInputListener(this);

        //get all messages from database to list "Messages"
        pushDataMessagesToListMessages();

    }

    private void pushDataMessagesToListMessages() {
        messagesUserReceiveReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messages.clear();
                    int i = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Message message = data.getValue(Message.class);
                        messages.add(message);

//                        Toast.makeText(ChatActivity.this, ++i + "" + message.getContent(), Toast.LENGTH_SHORT).show();
                        messagesAdapter.addToStart(message, true);
                    }
                }

                messagesList.setAdapter(messagesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initMessageAdapter() {
        MessageHolders messageHolders = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message);

        messagesAdapter = new MessagesListAdapter<Message>(currentUser.getUid(), messageHolders, null);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        String messageText = String.valueOf(input);
        if (messagesUserReceiveReference == null || messageText.isEmpty() || messageText.equals("")) {
            Toast.makeText(this, "udate new message to database fail", Toast.LENGTH_SHORT).show();
            return false;
        }

        //push new message to database
        String key = messagesUserReceiveReference.push().getKey();
        newMessage = new Message(key, messageText, userSend);
        messagesUserReceiveReference.child(key).setValue(newMessage);
        return true;
    }

}

package fsi.studymyselft.nguyenthanhthi.chatapp.activities.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseMainActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Dialog;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

public class DialogsActivity extends BaseMainActivity implements DialogsView {

    private static final String TAG = "DialogsActivity";

    private DialogsList dialogsList; //UI - widget
    private DialogsListAdapter<Dialog> dialogsAdapter;
    private ImageLoader imageLoader;

    private ArrayList<Dialog> dialogs;

    private FirebaseDatabase database;
    private DatabaseReference rootReference, dialogsReference;
    private FirebaseUser currentUser;

    private User newUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        bindViews();
    }

    @Override
    public void bindViews() {
        showErrorInternetCheckingIfExist(TAG);

        dialogsList = (DialogsList) findViewById(R.id.dialogsList);

        showListDialogs();
    }

    @Override
    public Context getContext() {
        return DialogsActivity.this;
    }

    @Override
    public void showListDialogs() {
        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();

        //get reference of Dialog Database
        if (rootReference.child("Dialogs") == null) {
            rootReference.setValue("Dialogs");
        }
        dialogsReference = rootReference.child("Dialogs");

        pushDataDialogsFromDatabaseToListDialogs();

        initDialogsAdapter();

        dialogsList.setAdapter(dialogsAdapter);
    }

    @Override
    public void initDialogsAdapter() {
        initImageLoader(imageLoader);

        dialogsAdapter = new DialogsListAdapter<Dialog>(imageLoader);
        dialogsAdapter.setItems(dialogs);
    }

    private void pushDataDialogsFromDatabaseToListDialogs() {
        dialogsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Dialog dialog = snapshot.getValue(Dialog.class);
                        dialogs.add(dialog);
                    }

                    Log.d(TAG, "Total dialogs in array list dialogs = " + dialogs.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

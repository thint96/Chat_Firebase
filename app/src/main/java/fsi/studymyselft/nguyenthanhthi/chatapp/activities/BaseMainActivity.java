package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login.LoginActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

/**
 * Created by thanhthi on 03/06/2018.
 */

public abstract class BaseMainActivity extends BaseActivity
        implements MenuItem.OnMenuItemClickListener {

    private static final String TAG = "BaseMainActivity";

    private FrameLayout myContentLayout;    //This is the frame layout to keep your content view
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Menu drawerMenu;

    private ImageView imgAvatar;
    private TextView txtUserName, txtEmail;
    private User myUser;

    private FirebaseUser currentUser;
    private DatabaseReference rootReference, usersReference;
    private FirebaseAuth.AuthStateListener authStateListener;

    protected void onCreateNavigationDrawer() {
        super.setContentView(R.layout.layout_base_main);

        Log.d(TAG, "onCreateNavigationDrawer()");

        myContentLayout = (FrameLayout) findViewById(R.id.my_content_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // The base layout that contains your navigation drawer

        //start setting header of navigation drawer
        View header = navigationView.getHeaderView(0);
        final ImageView imgAvatar = header.findViewById(R.id.img_avatar);
        final TextView txtUserName = header.findViewById(R.id.txtUserName);
        final TextView txtEmail = header.findViewById(R.id.txtEmail);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myUser = new User(currentUser.getUid(), currentUser.getEmail());

        rootReference = FirebaseDatabase.getInstance().getReference();
        if (rootReference.child(getString(R.string.USERS_DATABASE)) == null) {
            rootReference.setValue(getString(R.string.USERS_DATABASE));
        }
        usersReference = rootReference.child(getString(R.string.USERS_DATABASE));

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        Log.e(TAG, "user element: " + user.getId() + " - " + user.getEmail() + " - " + user.getAvatar());
                        if (user.getId().equals(currentUser.getUid())) {
                            myUser.setAvatar(user.getAvatar());
                            Log.e(TAG, myUser.getId() + " - " + myUser.getEmail() + " - " + myUser.getAvatar());

                            //set name for my user
                            String email = myUser.getEmail();
                            Log.e(TAG, "email: " + email);
                            int indexEnd = email.indexOf("@");
                            Log.e(TAG, "index end = " + indexEnd);
                            txtUserName.setText(email.substring(0, indexEnd));
                            txtEmail.setText(email);

                            //set email for my user
                            txtEmail.setText(email);

                            //set image for avatar of my user
                            Log.e(TAG, "Avatar url of my user: " + myUser.getAvatar());
                            Picasso.with(getContext())
                                    .load(myUser.getAvatar())
                                    .resize(100, 100)
                                    .into(imgAvatar);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //end setting header of navigation drawer

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {
            @Override
            public void onDrawerClosed(View v) {
                getSupportActionBar().show();
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                getSupportActionBar().hide();
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerMenu = navigationView.getMenu();
        for (int i = 0; i < drawerMenu.size(); i++) {
            drawerMenu.getItem(i).setOnMenuItemClickListener(this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /* Override all setContentView methods to put the content view to the FrameLayout view_stub
     * so that, we can make other activity implementations looks like normal activity subclasses.
     */
    @Override
    public void setContentView(View view) {
        if (myContentLayout != null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            myContentLayout.addView(view, layoutParams);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        onCreateNavigationDrawer();
        if (myContentLayout != null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = layoutInflater.inflate(layoutResID, myContentLayout, false);
            myContentLayout.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (myContentLayout != null) {
            myContentLayout.addView(view, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns true,
        // then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        item.setCheckable(true); // set item as selected to persist highlight
        drawerLayout.closeDrawers(); // close drawer when item is tapped

        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(getContext(), ListUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.nav_setting:
                Toast.makeText(getContext(), "go to setting screen", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_trash:
                Toast.makeText(getContext(), "go to trash screen", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                logout();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //end of adding navigation drawer

    public void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (authStateListener != null) {
                            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
                        }
                        usersReference.child(currentUser.getUid()).child("online").setValue(false);
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        finish();
                    }
                });
    }
}
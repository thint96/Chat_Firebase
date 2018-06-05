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
import android.support.v7.widget.Toolbar;
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
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.authen.login.LoginActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;

/**
 * Created by thanhthi on 03/06/2018.
 */

public abstract class BaseMainActivity extends BaseActivity
        implements BaseMainView, MenuItem.OnMenuItemClickListener {

    private static final String TAG = "BaseMainActivity";

    private FrameLayout myContentLayout;    //This is the frame layout to keep your content view
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private Menu drawerMenu;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    protected void onCreateNavigationDrawer() {
        super.setContentView(R.layout.layout_base_main);

        Log.d(TAG, "onCreateNavigationDrawer()");

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        myContentLayout = (FrameLayout) findViewById(R.id.my_content_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // The base layout that contains your navigation drawer

        View header = navigationView.getHeaderView(0);
        ImageView imgAvatar = header.findViewById(R.id.img_avatar);
        TextView txtUserName = header.findViewById(R.id.txtUserName);
        TextView txtEmail = header.findViewById(R.id.txtEmail);

        String email = currentUser.getEmail();
        int indexEnd = email.indexOf("@");
        txtUserName.setText(email.substring(0, indexEnd));
        txtEmail.setText(email);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View v){
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
        }
        else {
            super.onBackPressed();
        }
    }

    //end of adding navigation drawer

    @Override
    public void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        finish();
                    }
                });
    }
}
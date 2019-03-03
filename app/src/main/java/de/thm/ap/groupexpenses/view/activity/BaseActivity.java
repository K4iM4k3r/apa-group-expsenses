package de.thm.ap.groupexpenses.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.UserLiveData;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener, View.OnClickListener{
    private final String TAG = this.getClass().getName();
    private FrameLayout view_stub; //This is the framelayout to keep the content view
    private View headerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView name;
    private UserLiveData userLiveData;
    private CircleImageView picture;
    protected FirebaseAuth auth;
    protected FirebaseFirestore db;
    public ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.app_base_layout);// The base layout that contains your navigation drawer.
        view_stub = findViewById(R.id.view_stub);
        // The new navigation view from Android Design Library. Can inflate menu resources. Easy
        NavigationView navigation_view = findViewById(R.id.navigation_view);
        headerView = navigation_view.getHeaderView(0);
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = headerView.findViewById(R.id.header_name);
        picture = headerView.findViewById(R.id.header_pic);

        // Add listener
        Menu drawerMenu = navigation_view.getMenu();
        for(int i = 0; i < drawerMenu.size(); i++) {
            drawerMenu.getItem(i).setOnMenuItemClickListener(this);
        }
        headerView.setOnClickListener(this);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() != null) {

            userLiveData = DatabaseHandler.qetUserLiveData(auth.getCurrentUser().getUid());
            userLiveData.observe(this, user -> {
                if(user != null){
                    App.CurrentUser = user;
                    name.setText(user.getNickname());
                    checkFriendsList();
                }
            });
        }

        checkLoginState();
    }

    @SuppressWarnings("unused")
    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (view_stub != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = inflater.inflate(layoutResID, view_stub, false);
            view_stub.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (view_stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view_stub.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view_stub != null) {
            view_stub.addView(view, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLoginState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userLiveData.removeObservers(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginState();
        userLiveData.observe(this, user -> {
            if(user != null){
                App.CurrentUser = user;
                name.setText(user.getNickname());
                checkFriendsList();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == headerView){
                startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.menu_item_logout:
                auth.signOut();
                File pic = new File(getExternalFilesDir(null), "profilePic.jpg");
                //noinspection ResultOfMethodCallIgnored
                pic.delete();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.menu_item_main:
                startActivity(new Intent(this, EventActivity.class));
                return true;
            case R.id.menu_item_friends:
                startActivity(new Intent(this, FriendsActivity.class));
                return true;
            default:
                return false;
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void checkLoginState(){
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null || !currentUser.isEmailVerified()){
            Log.i(TAG, "no user logged in");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else{
            Log.i(TAG, "User is logged in: " + userLiveData.getValue());
            File pic = new File(getExternalFilesDir(null), "profilePic.jpg");
            if(pic.exists()){
                picture.setImageURI(Uri.fromFile(pic));
            }
        }
    }

    public void goToFriendsList(View view) {
        startActivity(new Intent(this, FriendsActivity.class));
        finish();
    }

    private void checkFriendsList(){
        if(App.CurrentUser.getFriendsIds() != null){
            if(App.CurrentUser.getFriendsIds().isEmpty()){
                findViewById(R.id.notification_friends).setVisibility(View.VISIBLE);
            }
            else {
                findViewById(R.id.notification_friends).setVisibility(View.GONE);
            }
        }
    }
}
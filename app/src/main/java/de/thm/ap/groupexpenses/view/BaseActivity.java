package de.thm.ap.groupexpenses.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.R;


public class BaseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener, View.OnClickListener{
    private FrameLayout view_stub; //This is the framelayout to keep the content view
    private NavigationView navigation_view; // The new navigation view from Android Design Library. Can inflate menu resources. Easy
    private View headerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Menu drawerMenu;
    private TextView name;
    private CircleImageView picture;
    protected FirebaseAuth auth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.app_base_layout);// The base layout that contains your navigation drawer.
        view_stub = findViewById(R.id.view_stub);
        navigation_view = findViewById(R.id.navigation_view);
        headerView = navigation_view.getHeaderView(0);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = headerView.findViewById(R.id.header_name);
        picture = headerView.findViewById(R.id.header_pic);

        // Add listener
        drawerMenu = navigation_view.getMenu();
        for(int i = 0; i < drawerMenu.size(); i++) {
            drawerMenu.getItem(i).setOnMenuItemClickListener(this);
        }
        headerView.setOnClickListener(this);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();
    }

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
    protected void onResume() {
        super.onResume();
        checkLoginState();
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
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.menu_item_main:
                startActivity(new Intent(this, EventActivity.class));
                return true;
            default:
                return false;
        }
    }

    public void checkLoginState(){
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.getCurrentUser();
        if(currentUser == null || !currentUser.isEmailVerified()){
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            File pic = new File(getExternalFilesDir(null), "profilePic.jpg");
            if(pic.exists()){
                picture.setImageURI(Uri.fromFile(pic));
            }
            name.setText(currentUser.getDisplayName());
        }
    }
}
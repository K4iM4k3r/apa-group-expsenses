package de.thm.ap.groupexpenses.view.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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
import de.thm.ap.groupexpenses.receivers.NotificationReceiver;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener, View.OnClickListener{
    private NotificationManagerCompat notificationManager;
    private String testPayment = "A debt has been paid";
    private String testEvent = "A Event has been created";
    private final String TAG = this.getClass().getName();
    private FrameLayout view_stub; //This is the framelayout to keep the content view
    private View headerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView name;
    private UserLiveData userLiveData;
    private CircleImageView picture;
    private DrawerLayout mDrawerLayout;
    protected FirebaseAuth auth;
    protected FirebaseFirestore db;
    public ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.app_base_layout);// The base layout that contains your navigation drawer.
        notificationManager = NotificationManagerCompat.from(this);
        view_stub = findViewById(R.id.view_stub);
        // The new navigation view from Android Design Library. Can inflate menu resources. Easy
        NavigationView navigation_view = findViewById(R.id.navigation_view);
        headerView = navigation_view.getHeaderView(0);
        mDrawerLayout = findViewById(R.id.drawer_layout);
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

    /**
     * Sends payment notifications
     * @param v
     */
    public void sendOnPaymentChannel(View v) {
        Intent activityIntent = new Intent(this, BaseActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("paymentKey", testPayment);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification payNotification = new NotificationCompat.Builder(this, App.PaymentID )
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle("Geldsammler Payment")
                .setContentText(testPayment)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true) // dismiss Notification if tapped
                .addAction(R.mipmap.ic_launcher, "paymentKey", actionIntent)
                .build();
        notificationManager.notify(1, payNotification);
    }

    /**
     * Sends event invite notifications
     * @param v
     */
    public void sendOnNewEventChannel(View v) {
        Intent activityIntent = new Intent(this, BaseActivity.class);
        PendingIntent contentIntent2 = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification eventNotification = new NotificationCompat.Builder(this, App.newEventID )
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle("Geldsammler Event invite")
                .setContentText(testPayment)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent2)
                .build();
        notificationManager.notify(2, eventNotification);

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
            View stubView = null;
            if (inflater != null) {
                stubView = inflater.inflate(layoutResID, view_stub, false);
            }
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
            mDrawerLayout.closeDrawer(GravityCompat.START, false);
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START, false);
        switch (item.getItemId()) {
//            case R.id.menu_item_profile:
//                startActivity(new Intent(this, ProfileActivity.class));
//                return true;
            case R.id.menu_item_logout:
                auth.signOut();
                File pic = new File(getExternalFilesDir(null), "profilePic.jpg");
                //noinspection ResultOfMethodCallIgnored
                pic.delete();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.menu_item_event:
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
        if(App.CurrentUser.getFriendsIds() == null || App.CurrentUser.getFriendsIds().isEmpty()){
            findViewById(R.id.notification_friends).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.notification_friends).setVisibility(View.GONE);
        }
    }
}
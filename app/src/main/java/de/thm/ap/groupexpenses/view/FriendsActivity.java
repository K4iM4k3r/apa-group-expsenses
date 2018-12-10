package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.User;

public class FriendsActivity extends AppCompatActivity {


    ListView friendListView;

    List<User> friends;
    ArrayAdapter<User> friendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.FriendsActivityTitle));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        friendListView = findViewById(R.id.friends_list);
        friendListView.setEmptyView(findViewById(R.id.friends_list_empty));

        friends = getFriends();

        Collections.sort(friends); //alphabetical sort

        friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, friends);
        friendListView.setAdapter(friendsAdapter);
    }


    // todo import friends
    private List<User> getFriends(){
        return Arrays.asList(App.TestValues.USERS);
    }


}

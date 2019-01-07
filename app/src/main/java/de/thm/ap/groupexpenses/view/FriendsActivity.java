
package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.User;

public class FriendsActivity extends BaseActivity {


    ListView friendListView;

    List<User> friends;
    ArrayAdapter<User> friendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.FriendsActivityTitle));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.dialog_friend_title);

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            input.setHint(getString(R.string.hint_nickname));
            input.setPadding(32,32,32,32);
            builder.setView(input);
            builder.setTitle(R.string.dialog_friends_message);
            builder.setPositiveButton(R.string.dialog_friends_positiveBtn, (dialog, which) -> {
                final String userInput = input.getText().toString();

                if(TextUtils.isEmpty(userInput)) {
                    Snackbar.make(view, getString(R.string.error_user_not_found), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.dialog_friend_retry),  l -> fab.callOnClick()).show();
                }
                else {
                    DatabaseHandler.isNicknameExist(userInput, exists -> {
                        if (!exists) {
                            Snackbar.make(view, getString(R.string.error_user_not_found), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.dialog_friend_retry),  l -> fab.callOnClick()).show();
                        } else {
                            DatabaseHandler.queryUserByNickname(userInput, friend -> {
                                App.CurrentUser.addFriend(friend.getUid());
                                DatabaseHandler.updateUser(App.CurrentUser);
                                loadFriends();
                            });
                        }
                    });
                }
            });
            builder.show();

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        friendListView = findViewById(R.id.friends_list);
        friendListView.setEmptyView(findViewById(R.id.friends_list_empty));

        loadFriends();

    }

    private void loadFriends(){
        DatabaseHandler.getAllFriendsOfUser(auth.getUid(), friends ->{
            this.friends = friends;
            Collections.sort(friends); //alphabetical sort

            friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, friends);
            friendListView.setAdapter(friendsAdapter);
        });

    }
}

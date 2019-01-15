
package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.adapter.UserArrayAdapter;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.User;

public class FriendsActivity extends BaseActivity {
    ListView friendListView;
    List<User> friends;
    UserArrayAdapter friendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        });

        friendListView = findViewById(R.id.friends_list);
        friendListView.setEmptyView(findViewById(R.id.friends_list_empty));
        friendListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        friendListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                mode.setTitle(friendListView.getCheckedItemCount() + " " + getString(R.string.CABTitle));

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.action_remove_friend:
                        deleteSelectedItems(mode);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_friends, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });
        loadFriends();

    }

    private void deleteSelectedItems(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SparseBooleanArray ids = friendListView.getCheckedItemPositions();
        builder.setTitle(R.string.dialog_friend_delete);
        builder.setMessage(R.string.dialog_friend_delete_message);
        builder.setNeutralButton(R.string.close,null);
        builder.setPositiveButton(R.string.dialog_friend_delete_ok, (dialog, which) -> {

            for (int i = 0; i < friendsAdapter.getCount(); i++) {
//                Log.i(Constants.RecAct, " i:" + i + "; " + ids.get(i));
                User selectedUser = friendsAdapter.getItem(i);
                if (ids.get(i) && selectedUser != null) {
                    App.CurrentUser.removeFriend(selectedUser.getUid());
                }
            }
            DatabaseHandler.updateUserWithFeedback(App.CurrentUser, success -> {
                Snackbar.make(friendListView, getString(R.string.success_remove_friend), Snackbar.LENGTH_LONG).show();
                loadFriends();
                },
                failure -> Snackbar.make(friendListView, getString(R.string.error_remove_friend), Snackbar.LENGTH_LONG).show());
            mode.finish();

        });
        builder.show();
    }

    private void loadFriends(){
        DatabaseHandler.getAllFriendsOfUser(auth.getUid(), friends ->{
            this.friends = friends;
            Collections.sort(friends); //alphabetical sort

            friendsAdapter = new UserArrayAdapter(this, friends);
            friendListView.setAdapter(friendsAdapter);
        });

    }
}

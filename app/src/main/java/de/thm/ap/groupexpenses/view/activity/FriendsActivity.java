
package de.thm.ap.groupexpenses.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.services.NotificationService;
import de.thm.ap.groupexpenses.view.dialog.ProfileInfoDialog;

public class FriendsActivity extends BaseActivity {
    private ListView friendListView;
    private UserArrayAdapter friendsAdapter;
//    private TextView friendsListInfo;
    private UserListLiveData listLiveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.FriendsActivityTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        friendsListInfo = findViewById(R.id.friends_list_info);

        FloatingActionButton fab = findViewById(R.id.friends_activity_add_friends_btn);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.dialog_friend_title);

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            input.setHint(getString(R.string.hint_nickname));
            input.setPadding(32, 32, 32, 32);
            builder.setView(input);
            builder.setTitle(R.string.dialog_friends_message);
            builder.setPositiveButton(R.string.dialog_friends_positiveBtn, (dialog, which) -> {
                final String userInput = input.getText().toString();

                if (TextUtils.isEmpty(userInput)) {
                    Snackbar.make(view, getString(R.string.error_user_not_found), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.dialog_friend_retry), l -> fab.callOnClick()).show();
                } else {
                    DatabaseHandler.isNicknameExisting(userInput, exists -> {
                        if (!exists) {
                            Snackbar.make(view, getString(R.string.error_user_not_found), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.dialog_friend_retry), l -> fab.callOnClick()).show();
                        } else {
                            // tell the NotificationService that we added the friend
                            NotificationService.isCaller = true;
                            DatabaseHandler.queryUserByNickname(userInput, friend -> DatabaseHandler.makeFriendship(App.CurrentUser, friend));
                        }
                    });
                }
            });
            builder.show();
        });

        friendListView = findViewById(R.id.friends_list);
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

        friendListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = ((User)friendListView.getItemAtPosition(position));
            new ProfileInfoDialog(selectedUser, this);
        });

        super.showProgressDialog();
        listLiveData = DatabaseHandler.getAllFriendsOfUser(auth.getUid());
        listLiveData.observe(this, this::loadFriends);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listLiveData.observe(this, this::loadFriends);
    }

    private void deleteSelectedItems(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SparseBooleanArray ids = friendListView.getCheckedItemPositions();
        builder.setTitle(R.string.dialog_friend_delete);
        builder.setMessage(R.string.dialog_friend_delete_message);
        builder.setNeutralButton(R.string.close, null);
        builder.setPositiveButton(R.string.dialog_friend_delete_ok, (dialog, which) -> {

            for (int i = 0; i < friendsAdapter.getCount(); i++) {
                User selectedUser = friendsAdapter.getItem(i);
                if (ids.get(i) && selectedUser != null) {
                    DatabaseHandler.destroyFriendship(App.CurrentUser, selectedUser);
                    super.showProgressDialog();
                }
            }
            DatabaseHandler.updateUserWithFeedback(App.CurrentUser,
                    success -> Snackbar.make(friendListView, getString(R.string.success_remove_friend), Snackbar.LENGTH_LONG).show(),
                    failure -> Snackbar.make(friendListView, getString(R.string.error_remove_friend), Snackbar.LENGTH_LONG).show());
            mode.finish();

        });
        builder.show();
    }

    private void loadFriends(List<User> friends) {
        super.hideProgressDialog();
        if(friends.isEmpty()){
            friendListView.setVisibility(View.GONE);
        }
        else {
            friendListView.setVisibility(View.VISIBLE);
            Collections.sort(friends, Collections.reverseOrder()); //alphabetical sort
            friendsAdapter = new UserArrayAdapter(this, friends);
            friendListView.setAdapter(friendsAdapter);
        }
    }

    private class UserArrayAdapter extends ArrayAdapter<User> {
        private final static int VIEW_RESOURCE = R.layout.user_list_item;

        private UserArrayAdapter(Context ctx, List<User> users) {
            super(ctx, VIEW_RESOURCE, users);
        }

        @NonNull
        @Override
        public View getView(int pos, View view, @NonNull ViewGroup parent) {

            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = Objects.requireNonNull(vi).inflate(VIEW_RESOURCE, null);
            }

            User user = getItem(pos);
            if (user != null) {
                CircleImageView picture = view.findViewById(R.id.user_pic);
                if (user.getProfilePic() != null) {
                    DatabaseHandler.getUserProfilePic(parent.getContext(), user.getUid(), opPictureUri -> opPictureUri.ifPresent(picture::setImageURI));
                }
                TextView nickname = view.findViewById(R.id.list_item_nickname);
                nickname.setText(user.getNickname());
            }

            return view;
        }
    }
}

package de.thm.ap.groupexpenses.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.EventFormActivity;

public class UserListFragmentDialog extends DialogFragment {

    private View view;
    private ListView userListView;
    private Button addBtn, doneBtn;
    private UserArrayAdapter userArrayAdapter;
    private List<User> friendsList;
    private ArrayList<User> addableUsers, addableUsersSelected, usersDeleted;
    private Event selectedEvent;
    private List<User> selectedUsers;
    private User creator;
    private String TAG;
    private boolean isCreator, hasPositions;
    private static int edit_state;
    private static int previous_edit_state;
    private static final int EDIT_STATE_INSPECT_USERS = 1;
    private static final int EDIT_STATE_ADD_USERS = 2;
    private static final int EDIT_STATE_DELETE_USERS = 3;

    public void build(List<User> selectedUsers, List<User> friendsList) {
        this.selectedUsers = selectedUsers;
        this.friendsList = friendsList;
    }

    public void build(Event selectedEvent, List<User> selectedUsers, List<User> friendsList) {
        build(selectedUsers, friendsList);
        this.selectedEvent = selectedEvent;
        if (!selectedEvent.getPositions().isEmpty()) hasPositions = true;
        if (App.CurrentUser.getUid().equals(selectedEvent.getCreatorId())) isCreator = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_list, container, false);
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        TextView headerTextView = view.findViewById(R.id.fragment_user_list_users_textView);
        EditText userSearchEditText = view.findViewById(R.id.fragment_user_list_search_editText);
        ImageView closeIconImageView = view.findViewById(R.id.fragment_user_list_close_imageView);
        userListView = view.findViewById(R.id.fragment_user_list_listView);
        addBtn = view.findViewById(R.id.fragment_user_list_add_btn);
        doneBtn = view.findViewById(R.id.fragment_user_list_done_btn);
        TAG = getTag();
        edit_state = 0;
        previous_edit_state = 0;

        if (selectedUsers == null)
            selectedUsers = new ArrayList<>();

        if (friendsList == null)
            friendsList = new ArrayList<>();

        if (TAG.equals("edit_event")) {
            setEditState(EDIT_STATE_INSPECT_USERS);

            if (isCreator) addBtn.setText(R.string.add_remove);
            else {
                addBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.GONE);
            }

            if (hasPositions) addBtn.setText(R.string.event_form_add_members);

            userArrayAdapter = new UserArrayAdapter(getActivity(), selectedUsers);
        } else {
            headerTextView.setText(R.string.event_form_add_members);
            addBtn.setVisibility(View.GONE);
            userArrayAdapter = new UserArrayAdapter(getActivity(), friendsList);
        }
        userListView.setAdapter(userArrayAdapter);

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) userListView.getItemAtPosition(position);
            switch (TAG) {
                case "create_event":
                    if (!removeUserById(selectedUser.getUid(), selectedUsers)) {
                        selectedUsers.add(selectedUser);
                    }
                    userArrayAdapter.notifyDataSetChanged();
                    break;

                case "edit_event":
                    switch (edit_state) {
                        case EDIT_STATE_INSPECT_USERS:
                            // do nothing, maybe see profile here ?!
                            break;

                        case EDIT_STATE_ADD_USERS:
                            break;

                        case EDIT_STATE_DELETE_USERS:
                            break;
                    }
                    break;
            }
        });

        userSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                userArrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        closeIconImageView.setOnClickListener(v -> {
            getDialog().dismiss();
        });

        addBtn.setOnClickListener(v -> {
            switch (TAG) {
                case "edit_event":
                    switch (edit_state) {
                        case EDIT_STATE_INSPECT_USERS: // add btn was pressed in inspect state
                            if (!hasPositions) { // you can delete users when no pos in event
                                setEditState(EDIT_STATE_DELETE_USERS);
                                // remove creator from list and save it locally to add it back later
                                for (int idx = 0; idx < selectedUsers.size(); ++idx) {
                                    if (selectedUsers.get(idx).getUid().equals(App.CurrentUser.getUid())) {
                                        creator = selectedUsers.get(idx);
                                        selectedUsers.remove(idx);
                                    }
                                }
                                userArrayAdapter.notifyDataSetChanged();
                                addBtn.setText(R.string.event_form_add_members);
                                doneBtn.setText(R.string.ok);
                                if (usersDeleted == null) usersDeleted = new ArrayList<>();
                                break;
                            }
                            // don't break
                        case EDIT_STATE_DELETE_USERS:
                            setEditState(EDIT_STATE_ADD_USERS);
                            headerTextView.setText(R.string.event_form_add_members);
                            addBtn.setText(R.string.ok);
                            doneBtn.setText(R.string.back);
                            addableUsers = new ArrayList<>();
                            if (addableUsersSelected == null)
                                addableUsersSelected = new ArrayList<>();

                            for (int idx = 0; idx < friendsList.size(); ++idx) {
                                User currentUser = friendsList.get(idx);
                                if (!findUserById(currentUser.getUid(), selectedUsers))
                                    addableUsers.add(currentUser);
                            }
                            userArrayAdapter = new UserArrayAdapter(getActivity(), addableUsers);
                            userListView.setAdapter(userArrayAdapter);
                            userArrayAdapter.notifyDataSetChanged();
                            userListView.setOnItemClickListener((parent, view, position, id) -> {
                                User selectedUser = (User) userListView.getItemAtPosition(position);
                                if (!removeUserById(selectedUser.getUid(), addableUsersSelected))
                                    addableUsersSelected.add(selectedUser);
                                userArrayAdapter.notifyDataSetChanged();
                            });
                            break;

                        case EDIT_STATE_ADD_USERS: // add btn was pressed in edit state
                            if(!hasPositions)setEditState(EDIT_STATE_DELETE_USERS);
                            else setEditState(EDIT_STATE_INSPECT_USERS);
                            for (int idx = 0; idx < addableUsersSelected.size(); ++idx) {
                                boolean userFound = false;
                                for (int idx2 = selectedUsers.size() - 1; idx2 >= 0; --idx2) {
                                    if (selectedUsers.get(idx2).getUid().equals(addableUsersSelected.get(idx).getUid())) {
                                        userFound = true;
                                        break;
                                    }
                                }
                                if (!userFound) selectedUsers.add(addableUsersSelected.get(idx));
                            }

                            userArrayAdapter = new UserArrayAdapter(getActivity(), selectedUsers);
                            userListView.setAdapter(userArrayAdapter);
                            doneBtn.setText(R.string.ok);
                            addBtn.setText(R.string.event_form_add_members);
                            break;
                    }
                    break;
            }
        });

        doneBtn.setOnClickListener(v -> {
            switch (TAG) {
                case "edit_event":
                    switch (edit_state) {
                        case EDIT_STATE_ADD_USERS:
                            if (!hasPositions) {
                                setEditState(EDIT_STATE_DELETE_USERS);
                                headerTextView.setText(R.string.event_form_users);
                                addBtn.setText(R.string.event_form_add_members);
                                doneBtn.setText(R.string.done);
                                userArrayAdapter = new UserArrayAdapter(getActivity(), selectedUsers);
                                userListView.setAdapter(userArrayAdapter);
                                break;
                            }
                            // don't break
                        case EDIT_STATE_DELETE_USERS:
                            setEditState(EDIT_STATE_INSPECT_USERS);
                            headerTextView.setText(R.string.event_form_users);
                            doneBtn.setText(R.string.done);
                            if (!hasPositions) {
                                selectedUsers.add(creator);
                                addBtn.setText(R.string.add_remove);
                            } else {
                                addBtn.setText(R.string.event_form_add_members);
                            }
                            userArrayAdapter.notifyDataSetChanged();
                            break;
                    }
                    break;
                case "create_event":
                    ((EventFormActivity) getActivity()).setEventMembers(selectedUsers);
                    getDialog().dismiss();
                    break;
            }
        });

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (isCreator) {
            if (usersDeleted != null && addableUsersSelected != null) {
                if (addableUsersSelected.size() > 0 && usersDeleted.size() > 0) {
                    showConfirmDialog(addableUsersSelected.size(), usersDeleted.size());
                } else if (addableUsersSelected.size() > 0) {
                    showConfirmDialog(addableUsersSelected.size(), 0);
                }
            } else if (usersDeleted != null) {
                if (usersDeleted.size() > 0)
                    showConfirmDialog(0, usersDeleted.size());
            }
        }
    }

    private void showConfirmDialog(int addedSize, int deletedSize) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.dialog_confirm_member_changes, null);
        final AlertDialog confirmDialogBuilder = new AlertDialog.Builder(getContext()).create();
        TextView add_header = promptView.findViewById(R.id.dialog_user_changes_add_header_textView);
        TextView remove_header = promptView.findViewById(R.id.dialog_user_changes_remove_header_textView);
        TextView add_users = promptView.findViewById(R.id.dialog_user_changes_add_users_textView);
        TextView remove_users = promptView.findViewById(R.id.dialog_user_changes_remove_users_textView);
        Button confirmBtn = promptView.findViewById(R.id.dialog_user_changes_confirm_btn);
        Button cancelBtn = promptView.findViewById(R.id.dialog_user_changes_cancel_btn);

        String addString = getResources().getString(R.string.add_with_num, addedSize);
        add_header.setText(addString);

        String rmvString = getResources().getString(R.string.remove_with_num, deletedSize);
        remove_header.setText(rmvString);

        if (addedSize > 0) {
            add_users.setTextColor(Color.parseColor("#3a90e0"));
            add_users.setText(App.listToString(addableUsersSelected));
        }

        if (deletedSize > 0) {
            remove_users.setTextColor(Color.parseColor("#3a90e0"));
            remove_users.setText(App.listToString(usersDeleted));
        }

        cancelBtn.setOnClickListener(v -> {
            if (deletedSize > 0)
                selectedUsers.addAll(usersDeleted);
            if (addedSize > 0) {
                for (int idx = 0; idx < addableUsersSelected.size(); ++idx)
                    removeUserById(addableUsersSelected.get(idx).getUid(), selectedUsers);
            }
            confirmDialogBuilder.dismiss();
        });

        confirmBtn.setOnClickListener(v -> {
            String[] addableUsersSelectedUids = new String[addableUsersSelected.size()];
            for (int idx = 0; idx < addableUsersSelected.size(); ++idx)
                addableUsersSelectedUids[idx] = addableUsersSelected.get(idx).getUid();
            selectedEvent.addMembers(addableUsersSelectedUids);
            DatabaseHandler.updateEvent(selectedEvent);
            confirmDialogBuilder.dismiss();
        });

        confirmDialogBuilder.setView(promptView);
        confirmDialogBuilder.show();
    }

    private class UserArrayAdapter extends ArrayAdapter<User> {
        private Context mContext;
        private List<User> usersList;
        private Filter filter;

        private UserArrayAdapter(@NonNull Context context, List<User> list) {
            super(context, 0, list);
            mContext = context;
            usersList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;

            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.fragment_user_list_row, parent,
                        false);

            User currentUser = usersList.get(position);
            TextView name = listItem.findViewById(R.id.fragment_user_list_row_name);
            name.setText(currentUser.toString());
            name.setTextColor(Color.parseColor("#3a90e0"));
            ImageView image;
            String userId = currentUser.getUid();
            switch (TAG) {
                case "create_event":
                    image = listItem.findViewById(R.id.fragment_user_list_row_image_tick);
                    if (findUserById(userId, selectedUsers))
                        image.setVisibility(View.VISIBLE);
                    else
                        image.setVisibility(View.GONE);
                    break;

                case "edit_event":
                    switch (edit_state) {
                        case EDIT_STATE_INSPECT_USERS:
                            if (currentUser.getUid().equals(App.CurrentUser.getUid())) {
                                name.setText(R.string.yourself);
                            }
                            if (previous_edit_state != 0) {
                                image = listItem.findViewById(R.id.fragment_user_list_row_image_delete);
                                image.setVisibility(View.GONE);
                            }
                            // maybe view profile here?!
                            break;

                        case EDIT_STATE_ADD_USERS:
                            image = listItem.findViewById(R.id.fragment_user_list_row_image_tick);
                            if (findUserById(userId, addableUsersSelected))
                                image.setVisibility(View.VISIBLE);
                            else
                                image.setVisibility(View.GONE);
                            break;

                        case EDIT_STATE_DELETE_USERS:
                            image = listItem.findViewById(R.id.fragment_user_list_row_image_delete);
                            image.setVisibility(View.VISIBLE);
                            if (addableUsersSelected != null) {
                                for (int idx = 0; idx < addableUsersSelected.size(); ++idx) {
                                    if (userId == addableUsersSelected.get(idx).getUid()) {
                                        name.setText(currentUser.toString() + " (NEU)");
                                        name.setTextColor(Color.parseColor("#2ba050"));
                                        break;
                                    }
                                }
                            }
                            image.setOnClickListener(v -> {
                                boolean newUser = false;
                                if (!removeUserById(userId, selectedUsers))
                                    throw new IllegalAccessError("User '" + currentUser +
                                            "' not found, cannot be deleted!");

                                // remove (- if exists) from addable list if user was just added in this dialog
                                if (addableUsersSelected != null)
                                    newUser = removeUserById(userId, addableUsersSelected);

                                // add to users removed list if user was not just added in this dialog
                                if (!newUser)
                                    usersDeleted.add(currentUser);

                                notifyDataSetChanged();
                            });
                            break;
                    }

                    break;
            }
            return listItem;
        }

        @Override
        public Filter getFilter() {
            if (filter == null)
                filter = new AppFilter<>(usersList);
            return filter;
        }

        private class AppFilter<T> extends Filter {
            private ArrayList<T> sourceObjects;

            private AppFilter(List<T> objects) {
                sourceObjects = new ArrayList<>();
                synchronized (this) {
                    sourceObjects.addAll(objects);
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence chars) {
                String filterSeq = chars.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (filterSeq.length() > 0) {
                    ArrayList<T> filter = new ArrayList<>();
                    for (T object : sourceObjects) {
                        // the filtering itself:
                        if (object.toString().toLowerCase().contains(filterSeq))
                            filter.add(object);
                    }
                    result.count = filter.size();
                    result.values = filter;
                } else {
                    // add all objects
                    synchronized (this) {
                        result.values = sourceObjects;
                        result.count = sourceObjects.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // NOTE: this function is *always* called from the UI thread.
                ArrayList<T> filtered = (ArrayList<T>) results.values;
                notifyDataSetChanged();
                clear();
                for (int i = 0, l = filtered.size(); i < l; i++)
                    add((User) filtered.get(i));

                notifyDataSetInvalidated();
            }

        }
    }

    private void setEditState(int state) {
        previous_edit_state = edit_state;
        edit_state = state;
    }

    private boolean removeUserById(String id, List<User> list) {
        for (int idx = 0; idx < list.size(); ++idx) {
            if (list.get(idx).getUid().equals(id)) {
                list.remove(idx);
                return true;
            }
        }
        return false;
    }

    private boolean findUserById(String id, List<User> list) {
        for (int idx = 0; idx < list.size(); ++idx) {
            if (list.get(idx).getUid().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
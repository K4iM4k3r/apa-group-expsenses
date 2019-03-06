package de.thm.ap.groupexpenses.view.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.activity.EventFormActivity;
import de.thm.ap.groupexpenses.view.dialog.InviteDialog;
import de.thm.ap.groupexpenses.view.dialog.ProfileInfoDialog;

public class UserListDialogFragment extends DialogFragment {

    private View view;
    private ListView userListView;
    private Button add_btn, invite_btn;
    private UserArrayAdapter userArrayAdapter;
    private List<User> friendsList;
    private ArrayList<User> addableUsers, addableUsersSelected;
    private Event selectedEvent;
    private List<User> selectedUsers;
    private String TAG;
    private boolean isCreator;
    private static int edit_state;
    private static final int EDIT_STATE_INSPECT_USERS = 1;
    private static final int EDIT_STATE_ADD_USERS = 2;

    public void build(List<User> selectedUsers, List<User> friendsList) {
        this.selectedUsers = selectedUsers;
        this.friendsList = friendsList;
    }

    public void build(Event selectedEvent, List<User> selectedUsers, List<User> friendsList) {
        build(selectedUsers, friendsList);
        this.selectedEvent = selectedEvent;
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
        EditText userSearchEditText = view.findViewById(R.id.fragment_user_list_search_editText);
        ImageView closeIconImageView = view.findViewById(R.id.fragment_user_list_close_imageView);
        TextView headerTextView = view.findViewById(R.id.fragment_user_list_users_textView);
        userListView = view.findViewById(R.id.fragment_user_list_listView);
        add_btn = view.findViewById(R.id.fragment_user_list_add_btn);
        invite_btn = view.findViewById(R.id.fragment_user_list_done_btn);
        TAG = getTag();
        edit_state = 0;

        if (selectedUsers == null)
            selectedUsers = new ArrayList<>();

        if (friendsList == null)
            friendsList = new ArrayList<>();

        switch (TAG) {
            case "create_event":
                headerTextView.setText(R.string.event_form_add_friend);
                invite_btn.setVisibility(View.GONE);
                userArrayAdapter = new UserArrayAdapter(getActivity(), friendsList);
                break;

            case "edit_event":
                setEditState(EDIT_STATE_INSPECT_USERS);

                if (isCreator) {
                    switch (selectedEvent.getLifecycleState()) {
                        case ONGOING:
                        case LIVE:
                            add_btn.setText(R.string.event_form_add_friend);
                            invite_btn.setText(R.string.invite_link_share);
                            break;
                        case LOCKED:
                        case CLOSED:
                        case ERROR:
                            add_btn.setVisibility(View.GONE);
                            invite_btn.setVisibility(View.GONE);
                        default:
                            add_btn.setVisibility(View.GONE);
                            invite_btn.setVisibility(View.GONE);
                    }
                } else {
                    add_btn.setVisibility(View.GONE);
                    invite_btn.setVisibility(View.GONE);
                }
                userArrayAdapter = new UserArrayAdapter(getActivity(), selectedUsers);
                break;
        }
        userListView.setAdapter(userArrayAdapter);
        userListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
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
                            DatabaseHandler.queryUser(selectedUser.getUid(), user ->
                                    new ProfileInfoDialog(user, getContext()));
                            break;
                        case EDIT_STATE_ADD_USERS:
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

        add_btn.setOnClickListener(v -> {
            switch (TAG) {
                case "create_event":
                    ((EventFormActivity) getActivity()).setEventMembers(selectedUsers);
                    getDialog().dismiss();
                    break;
                case "edit_event":
                    switch (edit_state) {
                        case EDIT_STATE_INSPECT_USERS: // add btn was pressed in inspect state
                            setEditState(EDIT_STATE_ADD_USERS);
                            headerTextView.setText(R.string.event_form_add_friend);
                            add_btn.setText(R.string.ok);
                            invite_btn.setVisibility(View.GONE);
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
                            setEditState(EDIT_STATE_INSPECT_USERS);
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
                            // add all selected users to event
                            String[] addableUsersSelectedUids = new String[addableUsersSelected.size()];
                            for (int idx = 0; idx < addableUsersSelected.size(); ++idx)
                                addableUsersSelectedUids[idx] = addableUsersSelected.get(idx).getUid();
                            selectedEvent.addMembers(addableUsersSelectedUids);
                            DatabaseHandler.updateEvent(selectedEvent);
                            getDialog().dismiss();
                            break;
                    }
                    break;
            }
        });

        invite_btn.setOnClickListener((View v) -> {
            switch (edit_state) {
                case EDIT_STATE_ADD_USERS:
                    // this cannot be reached
                    break;
                case EDIT_STATE_INSPECT_USERS:
                    // send invite btn clicked
                    getDialog().dismiss();
                    new InviteDialog(getContext(), selectedEvent);
                    break;
                default:
                    getDialog().dismiss();
            }
        });
        closeIconImageView.setOnClickListener(v -> getDialog().dismiss());

        return view;
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
            String currentUserString = currentUser.toString();
            final int MAX_USER_NAME_LENGTH = 30;
            if (currentUserString.length() > MAX_USER_NAME_LENGTH) {
                currentUserString = currentUserString.substring(0, MAX_USER_NAME_LENGTH) + "...";
            }
            name.setText(currentUserString);
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
                                name.setText(R.string.you);
                            }
                            break;
                        case EDIT_STATE_ADD_USERS:
                            image = listItem.findViewById(R.id.fragment_user_list_row_image_tick);
                            if (findUserById(userId, addableUsersSelected))
                                image.setVisibility(View.VISIBLE);
                            else
                                image.setVisibility(View.GONE);
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
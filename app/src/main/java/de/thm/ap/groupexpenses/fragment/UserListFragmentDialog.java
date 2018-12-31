package de.thm.ap.groupexpenses.fragment;

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
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.EventFormActivity;

public class UserListFragmentDialog extends DialogFragment {

    private View view;
    private TextView headerTextView;
    private ListView userListView;
    private Button addBtn, doneBtn;
    private UserArrayAdapter userArrayAdapter;
    private ArrayList<User> usersInContactList, addableUsers, addableUsersSelected;
    private List<User> selectedUsers;
    private String TAG;
    private static boolean isCreator;
    private static int edit_state;
    private static int previous_edit_state;
    private static final int EDIT_STATE_INSPECT_USERS = 1;
    private static final int EDIT_STATE_ADD_USERS = 2;
    private static final int EDIT_STATE_DELETE_USERS = 3;

    public static UserListFragmentDialog newInstance(List<User> selectedUsers) {
        UserListFragmentDialog f = new UserListFragmentDialog();
        Bundle args = new Bundle();
        args.putSerializable("selectedUsers", (ArrayList<User>) selectedUsers);
        f.setArguments(args);
        return f;
    }

    public static UserListFragmentDialog newInstance(List<User> selectedUsers, User creator) {
        UserListFragmentDialog f = newInstance(selectedUsers);
        if(App.CurrentUser.getId() == creator.getId()) isCreator = true;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_user_list, container,false);
        }else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        EditText userSearchEditText = view.findViewById(R.id.fragment_user_list_search_editText);
        userListView = view.findViewById(R.id.fragment_user_list_listView);
        addBtn = view.findViewById(R.id.fragment_user_list_add_btn);
        doneBtn = view.findViewById(R.id.fragment_user_list_done_btn);
        TAG = getTag();
        edit_state = 0;
        previous_edit_state = 0;

        selectedUsers = (List<User>) getArguments().getSerializable("selectedUsers");
        if(selectedUsers == null)
            selectedUsers = new ArrayList<>();

        usersInContactList = new ArrayList<>();

        usersInContactList.add(new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de"));
        usersInContactList.add(new User(2, "Hendrik", "Kegel", "oof"));
        usersInContactList.add(new User(3, "Kai", "Schäfer", "oof2"));
        usersInContactList.add(new User(4, "David", "Omran", "oof3"));
        usersInContactList.add(new User(5, "Ulf", "Smolka", "ka"));
        usersInContactList.add(new User(6, "Dominik", "Herz", "kjlkalsd"));
        usersInContactList.add(new User(7, "Aris", "Christidis", "lolo"));
        usersInContactList.add(new User(8, "KQC", "NA", "xD"));
        usersInContactList.add(new User(9, "Adam", "Bdam", "dontEvenknow"));
        usersInContactList.add(new User(10, "Max", "Muster", "maybe@fdm"));
        usersInContactList.add(new User(11, "Rainer", "Rein", "lalalala"));
        usersInContactList.add(new User(12, "Reimann", "Rolf", "2345"));
        usersInContactList.add(new User(13, "Nikolaus", "Santa", "geshcnk@gmx.de"));
        usersInContactList.add(new User(14, "Rentier", "Rot", "ren@s.c"));
        usersInContactList.add(new User(15, "Grinch", "Grinch", "kb"));
        usersInContactList.add(new User(16, "Rübe", "Nase", "hund@gmx.com"));

        if(TAG.equals("edit_event")){
            setEditState(EDIT_STATE_INSPECT_USERS);

            if(isCreator) addBtn.setText(R.string.add_remove);
            else addBtn.setVisibility(View.GONE);

            headerTextView = view.findViewById(R.id.fragment_user_list_users_textView);
            headerTextView.setVisibility(View.VISIBLE);
            userArrayAdapter = new UserArrayAdapter(getActivity(), (ArrayList<User>) selectedUsers);
        } else {
            userArrayAdapter = new UserArrayAdapter(getActivity(), usersInContactList);
        }
        userListView.setAdapter(userArrayAdapter);

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) userListView.getItemAtPosition(position);
            switch (TAG){
                case "create_event":
                    if(!removeUserById(selectedUser.getId(), selectedUsers)){
                        selectedUsers.add(selectedUser);
                    }
                    userArrayAdapter.notifyDataSetChanged();
                    break;

                case "edit_event":
                    switch (edit_state){
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
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                userArrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        addBtn.setOnClickListener(v -> {
            switch (TAG){
                case "create_event":
                    ((EventFormActivity)getActivity()).setEventMembers(selectedUsers);
                    getDialog().dismiss();
                    break;

                case "edit_event":
                    switch (edit_state){
                        case EDIT_STATE_INSPECT_USERS: // add btn was pressed in inspect state
                            setEditState(EDIT_STATE_DELETE_USERS);
                            userArrayAdapter.notifyDataSetChanged();
                            addBtn.setText(R.string.event_form_add_members);
                            doneBtn.setText(R.string.confirm);
                            break;

                        case EDIT_STATE_ADD_USERS: // add btn was pressed in edit state
                            setEditState(EDIT_STATE_DELETE_USERS);
                            for(int idx = 0; idx < addableUsersSelected.size(); ++idx){
                                boolean userFound = false;
                                for(int idx2 = selectedUsers.size() - 1; idx2 >= 0; --idx2){
                                    if(selectedUsers.get(idx2).getId() == addableUsersSelected.get(idx).getId()){
                                        userFound = true;
                                        break;
                                    }
                                }
                                if(!userFound) selectedUsers.add(addableUsersSelected.get(idx));
                            }

                            userArrayAdapter = new UserArrayAdapter(getActivity(), (ArrayList<User>)selectedUsers);
                            userListView.setAdapter(userArrayAdapter);
                            doneBtn.setText(R.string.confirm);
                            addBtn.setText(R.string.event_form_add_members);
                            break;

                        case EDIT_STATE_DELETE_USERS:
                            setEditState(EDIT_STATE_ADD_USERS);
                            headerTextView.setText(R.string.event_form_add_members);
                            addBtn.setText(R.string.add);
                            doneBtn.setText(R.string.cancel);
                            addableUsers = new ArrayList<>();
                            if(addableUsersSelected == null) addableUsersSelected = new ArrayList<>();

                            for(int idx = 0; idx < usersInContactList.size(); ++idx){
                                User currentUser = usersInContactList.get(idx);
                                if(!findUserById(currentUser.getId(), selectedUsers))
                                    addableUsers.add(currentUser);
                            }
                            userArrayAdapter = new UserArrayAdapter(getActivity(), addableUsers);
                            userListView.setAdapter(userArrayAdapter);
                            userArrayAdapter.notifyDataSetChanged();
                            userListView.setOnItemClickListener((parent, view, position, id) -> {
                                User selectedUser = (User) userListView.getItemAtPosition(position);
                                if(!removeUserById(selectedUser.getId(), addableUsersSelected))
                                    addableUsersSelected.add(selectedUser);
                                userArrayAdapter.notifyDataSetChanged();
                            });
                            break;
                    }
                    break;
            }
        });

        doneBtn.setOnClickListener(v -> {
            switch(edit_state){
                case EDIT_STATE_INSPECT_USERS:
                    getDialog().dismiss();
                    break;

                case EDIT_STATE_DELETE_USERS:
                    setEditState(EDIT_STATE_INSPECT_USERS);
                    addBtn.setText(R.string.add_remove);
                    doneBtn.setText(R.string.done);
                    userArrayAdapter.notifyDataSetChanged();
                    break;

                case EDIT_STATE_ADD_USERS:
                    setEditState(EDIT_STATE_DELETE_USERS);
                    addBtn.setText(R.string.event_form_add_members);
                    doneBtn.setText(R.string.done);
                    userArrayAdapter = new UserArrayAdapter(getActivity(), (ArrayList<User>) selectedUsers);
                    userListView.setAdapter(userArrayAdapter);
                    break;
            }
        });

        return view;
    }

    private class UserArrayAdapter extends ArrayAdapter<User> {
        private Context mContext;
        private List<User> usersList;
        private Filter filter;

        private UserArrayAdapter(@NonNull Context context, ArrayList<User> list) {
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
            int userId = currentUser.getId();
            switch(TAG){
                case "create_event":
                    image = listItem.findViewById(R.id.fragment_user_list_row_image_tick);
                    if(findUserById(userId, selectedUsers))
                        image.setVisibility(View.VISIBLE);
                    else
                        image.setVisibility(View.GONE);
                    break;

                case "edit_event":
                    switch(edit_state){
                        case EDIT_STATE_INSPECT_USERS:
                            if(previous_edit_state != 0){
                                image = listItem.findViewById(R.id.fragment_user_list_row_image_delete);
                                image.setVisibility(View.GONE);
                            }
                            // maybe view profile here?!
                            break;

                        case EDIT_STATE_ADD_USERS:
                            image = listItem.findViewById(R.id.fragment_user_list_row_image_tick);
                            if(findUserById(userId, addableUsersSelected))
                                image.setVisibility(View.VISIBLE);
                            else
                                image.setVisibility(View.GONE);
                            break;

                        case EDIT_STATE_DELETE_USERS:
                            image = listItem.findViewById(R.id.fragment_user_list_row_image_delete);
                            image.setVisibility(View.VISIBLE);
                            if(addableUsersSelected != null){
                                for(int idx = 0; idx < addableUsersSelected.size(); ++idx){
                                    if(userId == addableUsersSelected.get(idx).getId()){
                                        name.setText(currentUser.toString() + " (NEU)");
                                        name.setTextColor(Color.parseColor("#2ba050"));
                                        break;
                                    }
                                }
                            }
                            image.setOnClickListener(v -> {
                                if(!removeUserById(userId, selectedUsers))
                                    throw new IllegalAccessError("User '" + currentUser +
                                            "' not found, cannot be deleted!");

                                // remove (- if exists) from addable list if user was just added in this dialog
                                if(addableUsersSelected != null)
                                    removeUserById(userId, addableUsersSelected);

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

    private void setEditState(int state){
        previous_edit_state = edit_state;
        edit_state = state;
    }

    private boolean removeUserById(int id, List<User> list){
        for(int idx = 0; idx < list.size(); ++idx){
            if(id == list.get(idx).getId()){
                list.remove(idx);
                return true;
            }
        }
        return false;
    }

    private boolean findUserById(int id, List<User> list){
        for(int idx = 0; idx < list.size(); ++idx){
            if(id == list.get(idx).getId()){
                return true;
            }
        }
        return false;
    }
}
package de.thm.ap.groupexpenses.fragment;

import android.app.DialogFragment;
import android.content.Context;
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

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.EventFormActivity;

public class UserListFragmentDialog extends DialogFragment {

    private View view;
    private EditText userSearch;
    private ListView userListView;
    private Button addBtn;
    private UserArrayAdapter userArrayAdapter;
    ArrayList<User> usersInContactList;
    ArrayList<User> selectedUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_user_list, container,false);
        }else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        userSearch = view.findViewById(R.id.fragment_user_list_search_editText);
        userListView = view.findViewById(R.id.fragment_user_list_listView);
        addBtn = view.findViewById(R.id.fragment_user_list_btn);

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

        userArrayAdapter = new UserArrayAdapter(getActivity(), usersInContactList);
        userListView.setAdapter(userArrayAdapter);

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) userListView.getItemAtPosition(position);

            if(selectedUsers.contains(selectedUser))
                selectedUsers.remove(selectedUser);
            else
                selectedUsers.add(selectedUser);

            userArrayAdapter.notifyDataSetChanged();
        });

        userSearch.addTextChangedListener(new TextWatcher() {
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
            ((EventFormActivity)getActivity()).setEventMembers(selectedUsers);
            getDialog().dismiss();
        });

        return view;
    }


    private class UserArrayAdapter extends ArrayAdapter<User> {
        private Context mContext;
        private List<User> usersList;
        private Filter filter;

        public UserArrayAdapter(@NonNull Context context, ArrayList<User> list) {
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

            ImageView image = listItem.findViewById(R.id.fragment_user_list_row_image_tick);

            if(selectedUsers.contains(currentUser))
                image.setVisibility(View.VISIBLE);
            else
                image.setVisibility(View.INVISIBLE);

            TextView name = listItem.findViewById(R.id.fragment_user_list_row_name);
            name.setText(currentUser.toString());

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

            public AppFilter(List<T> objects) {
                sourceObjects = new ArrayList<T>();
                synchronized (this) {
                    sourceObjects.addAll(objects);
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence chars) {
                String filterSeq = chars.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (filterSeq != null && filterSeq.length() > 0) {
                    ArrayList<T> filter = new ArrayList<T>();
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
}

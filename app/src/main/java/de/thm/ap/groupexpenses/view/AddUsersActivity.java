package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.User;

public class AddUsersActivity extends AppCompatActivity {

    private EditText userPickEditText;
    private ListView eventUserList;
    ArrayList<User> usersInContactList;
    ArrayList<User> selectedUsers;
    private UserArrayAdapter userArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        userPickEditText = findViewById(R.id.add_user_editText);
        eventUserList = findViewById(R.id.add_user_user_list);

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

        userArrayAdapter = new UserArrayAdapter(this, usersInContactList);
        eventUserList.setAdapter(userArrayAdapter);


        eventUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = (User) eventUserList.getItemAtPosition(position);

                if(selectedUsers.contains(selectedUser))
                    selectedUsers.remove(selectedUser);
                else
                    selectedUsers.add(selectedUser);
            }
        });

        userPickEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                userArrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_users_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_users_finish_btn) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("selectedUsers", selectedUsers);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout, parent,
                        false);

            User currentUser = usersList.get(position);

            ImageView image = listItem.findViewById(R.id.add_user_imageView);

            if(selectedUsers.contains(currentUser))
                image.setVisibility(View.VISIBLE);
            else
                image.setVisibility(View.INVISIBLE);

            TextView name = listItem.findViewById(R.id.add_user_textView);
            name.setText(currentUser.toString());

            return listItem;
        }

        @Override
        public Filter getFilter() {
            if (filter == null)
                filter = new AppFilter<User>(usersList);
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

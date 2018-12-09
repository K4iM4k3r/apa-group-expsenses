package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.User;

public class AddUsersActivity extends AppCompatActivity {

    private EditText userPickEditText;
    private ListView eventUserList;
    ArrayList<User> usersInContactList;
    ArrayList<User> selectedUsers;
    private ArrayAdapter<User> usersAdapter;

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


        usersAdapter = new ArrayAdapter(this, R.layout.list_item_layout, usersInContactList);
        eventUserList.setAdapter(usersAdapter);


        eventUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                User selectedUser = (User)eventUserList.getItemAtPosition(position);
                usersAdapter.remove(selectedUser);
                usersAdapter.notifyDataSetChanged();
                selectedUsers.add(selectedUser);
            }
        });

        userPickEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                usersAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });




    }
}

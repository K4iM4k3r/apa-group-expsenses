package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.fragment.UserListFragmentDialog;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;

public class EventFormActivity extends BaseActivity {

    private EditText eventNameEditText, eventDateEditText, eventInfoEditText;
    private TextView eventUsersTextView;
    private ArrayList<User> eventUsersList;
    private Button addMembersBtn;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private boolean fromDateSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);
        getSupportActionBar().setTitle(R.string.event_form_create_event);
        eventNameEditText = findViewById(R.id.event_form_name_edit);
        eventDateEditText = findViewById(R.id.event_form_date_edit);
        eventInfoEditText = findViewById(R.id.event_form_info_edit);
        addMembersBtn = findViewById(R.id.event_form_add_members_btn);
        eventUsersTextView = findViewById(R.id.event_form_users_textView);

        addMembersBtn.setOnClickListener(v -> {
            DatabaseHandler.getAllFriendsOfUser(auth.getCurrentUser().getUid(), result -> {
                List<User> friendsList = result;
                UserListFragmentDialog dialog = UserListFragmentDialog.newInstance(eventUsersList, friendsList);
                dialog.show(getFragmentManager(), "create_event");
            });

        });

        eventDateEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus && !fromDateSet) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EventFormActivity.this,
                        R.style.Theme_AppCompat_DayNight_Dialog,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            } else if(fromDateSet)
                fromDateSet = false;
        });

        dateSetListener = (view, year, month, day) -> {
            month = month + 1;
            String date = day + "." + month + "." + year;
            eventDateEditText.setText(date);
            fromDateSet = true;
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_form_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.event_form_create_event_btn) {
            if(eventNameEditText.getText().toString().isEmpty()){
                eventNameEditText.setError(getString(R.string.error_field_required));
                eventNameEditText.requestFocus();
            } else if(eventDateEditText.getText().toString().isEmpty()){
                eventDateEditText.setError(getString(R.string.error_field_required));
                eventDateEditText.requestFocus();
            } else if(!isValidDate(eventDateEditText.getText().toString())){
                eventDateEditText.setError(getString(R.string.error_invalid_date));
                eventDateEditText.requestFocus();
            }  else if(eventUsersList == null || eventUsersList.isEmpty()){
                eventUsersTextView.setText(getString(R.string.error_users_required));
                addMembersBtn.setError("");
                addMembersBtn.requestFocus();
            } else {
                // save event strings here

                if(eventUsersList == null) eventUsersList = new ArrayList<>();

                User creator = App.CurrentUser;
                String eventName = eventNameEditText.getText().toString().trim();
                eventName = eventName.substring(0,1).toUpperCase() + eventName.substring(1);

                ArrayList<String> eventUserListStrings = new ArrayList<>();
                for(int idx = 0; idx < eventUsersList.size(); ++idx){
                    eventUserListStrings.add(eventUsersList.get(idx).getUid());
                }

                Event event = new Event(creator.getUid(),
                        eventName,
                        eventDateEditText.getText().toString(),
                        eventInfoEditText.getText().toString(),
                        eventUserListStrings
                        );
                DatabaseHandler.createEvent(event);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static boolean isValidDate(String inputDate) {
        final String MODULE_NAME_PATTERN = "^([1-9]|0[1-9]|[12][0-9]|3[01])[-\\.]([1-9]|0[1-9]|1[012])[-\\.]\\d{4}$";
        Pattern pattern = Pattern.compile(MODULE_NAME_PATTERN);
        Matcher matcher = pattern.matcher(inputDate);
        return matcher.matches();
    }


    public void setEventMembers(List<User> userList){
        eventUsersList = (ArrayList<User>) userList;
        eventUsersTextView.setText(App.listToString(eventUsersList));
    }

}
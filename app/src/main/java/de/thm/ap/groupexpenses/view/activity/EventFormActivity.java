package de.thm.ap.groupexpenses.view.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.fragment.UserListDialogFragment;

import static de.thm.ap.groupexpenses.App.CurrentUser;
import static de.thm.ap.groupexpenses.App.getDateFromLong;
import static de.thm.ap.groupexpenses.App.listToString;

public class EventFormActivity extends BaseActivity {

    private EditText eventNameEditText, eventBeginDateEditText, eventEndDateEditText, eventInfoEditText;
    private TextView eventUsersTextView;
    private ArrayList<User> addedMembers;
    private int selectedDeadlineItem;
    private Date end_date, start_date;
    private DatePickerDialog.OnDateSetListener beginDateSetListener, endDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.event_form_create_event);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Spinner deadline_spinner = findViewById(R.id.event_form_deadline_spinner);
        eventNameEditText = findViewById(R.id.event_form_name_edit);
        eventBeginDateEditText = findViewById(R.id.event_form_date_begin_edit);
        eventEndDateEditText = findViewById(R.id.event_form_date_end_edit);
        eventInfoEditText = findViewById(R.id.event_form_info_edit);
        Button addMembersBtn = findViewById(R.id.event_form_add_members_btn);
        eventUsersTextView = findViewById(R.id.event_form_users_textView);
        ImageView edit_start_date_btn = findViewById(R.id.event_form_date_begin_edit_btn);
        ImageView edit_end_date_btn = findViewById(R.id.event_form_date_end_edit_btn);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> pay_stretch_spinner_adapter = ArrayAdapter
                .createFromResource(this, R.array.pay_stretch_weeks,
                        android.R.layout.simple_spinner_item);
        pay_stretch_spinner_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deadline_spinner.setAdapter(pay_stretch_spinner_adapter);
        selectedDeadlineItem = 1;
        deadline_spinner
                .setSelection(selectedDeadlineItem); // set 2 weeks pay stretch per default

        addMembersBtn.setOnClickListener(v -> {
            DatabaseHandler.getAllFriendsOfUser(auth.getCurrentUser().getUid(), result -> {
                List<User> friendsList = result;
                UserListDialogFragment dialog = new UserListDialogFragment();
                dialog.build(addedMembers, friendsList);
                dialog.show(getFragmentManager(), "create_event");
            });

        });

        eventBeginDateEditText.setOnFocusChangeListener((view, hasFocus) ->
                eventBeginDateEditText.setEnabled(false));

        edit_start_date_btn.setOnClickListener(v -> {
            int year, month, day;
            if (start_date != null) {
                year = end_date.getYear() + 1900;
                month = end_date.getMonth();
                day = end_date.getDate();
            } else {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog dialog = new DatePickerDialog(
                    EventFormActivity.this,
                    R.style.Theme_AppCompat_DayNight_Dialog,
                    beginDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();
        });

        eventEndDateEditText.setOnFocusChangeListener((view, hasFocus) ->
                eventEndDateEditText.setEnabled(false));

        edit_end_date_btn.setOnClickListener(v -> {
            int year, month, day;
            if (end_date != null) {
                year = end_date.getYear() + 1900;
                month = end_date.getMonth();
                day = end_date.getDate();
            } else {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog dialog = new DatePickerDialog(
                    EventFormActivity.this,
                    R.style.Theme_AppCompat_DayNight_Dialog,
                    endDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();
        });

        deadline_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeadlineItem = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setDefaultDates();

        beginDateSetListener = (view, year, month, day) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0, 0);
            start_date = c.getTime();
            String date = getDateFromLong(start_date.getTime());
            eventBeginDateEditText.setText(date);

            if (start_date.after(end_date)) {
                c.set(year, month, day, 23, 59, 59);
                end_date = c.getTime();
                eventEndDateEditText.setText(date);
            }
        };

        endDateSetListener = (view, year, month, day) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 23, 59, 59);
            end_date = c.getTime();
            String date = getDateFromLong(end_date.getTime());
            eventEndDateEditText.setText(date);

            if (start_date.after(end_date)) {
                eventEndDateEditText.setError(getString(R.string.error_end_date_before_start_date));
                return;
            }

            eventEndDateEditText.setError(null);
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_form_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_form_create_event_btn:
                createEvent();
                break;
            default:
                throw new IllegalArgumentException(item + "does is invalid.");
        }
        return super.onOptionsItemSelected(item);
    }

    private void createEvent() {

        if (!isValidInput()) return;

        // get event name + upper case first letter
        String eventName = eventNameEditText.getText().toString().trim();
        eventName = eventName.substring(0, 1).toUpperCase() + eventName.substring(1);

        // check if the event has members
        if (addedMembers == null) addedMembers = new ArrayList<>();

        // calculate deadline day
        int timeSpanInWeeks = selectedDeadlineItem + 1;
        int timeSpanInDays = timeSpanInWeeks * 7;
        long timeSpanInMillis = TimeUnit.DAYS.toMillis(timeSpanInDays);

        Long deadlineday = end_date.getTime() + timeSpanInMillis;

        Event event = new Event(
                CurrentUser.getUid(),                            // creatorId
                eventName,                                           // name
                start_date.getTime(),                                // date_begin
                end_date.getTime(),                                  // date_end
                deadlineday,                                         // deadline_day
                eventInfoEditText.getText().toString(),              // info
                addedMembers.stream()                                // members
                        .map(User::getUid)
                        .collect(Collectors.toList())
        );

        DatabaseHandler.createEvent(event);
        finish();
    }

    private boolean isValidInput() {
        if (eventNameEditText.getText().toString().isEmpty()) {
            eventNameEditText.setError(getString(R.string.error_field_required));
            eventNameEditText.requestFocus();
            return false;
        }

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.set(year, month, day, 0, 0, 0);
        // subtract some hours so you can chose the actual day as well
        c.add(Calendar.HOUR, -12);

        if (start_date.before(c.getTime())) {
            eventBeginDateEditText.setError(getString(R.string.error_event_start_in_past));
            eventBeginDateEditText.requestFocus();
            return false;
        }

        if (start_date.after(end_date)) {
            eventEndDateEditText.setError(getString(R.string.error_end_date_before_start_date));
            eventEndDateEditText.requestFocus();
            return false;
        }
        return true;
    }

    public void setEventMembers(List<User> userList) {
        addedMembers = (ArrayList<User>) userList;
        eventUsersTextView.setText(listToString(addedMembers));
    }

    private void setDefaultDates() {

        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(year, month, day, 0, 0, 0);
        start_date = c.getTime();
        eventBeginDateEditText.setText(getDateFromLong(start_date.getTime()));

        c.set(year, month, day, 23, 59, 59);
        end_date = c.getTime();
        eventEndDateEditText.setText(getDateFromLong(end_date.getTime()));
    }

}
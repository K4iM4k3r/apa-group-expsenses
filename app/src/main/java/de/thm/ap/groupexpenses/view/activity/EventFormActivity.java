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
import android.widget.Switch;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.view.fragment.UserListDialogFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;

public class EventFormActivity extends BaseActivity {

    private EditText eventNameEditText, eventBeginDateEditText, eventEndDateEditText, eventInfoEditText;
    private TextView eventUsersTextView;
    private ArrayList<User> eventUsersList;
    private Button addMembersBtn;
    private Switch one_day_event_switch;
    private Spinner pay_stretch_spinner;
    int pay_stretch_item_selected_index;
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
        one_day_event_switch = findViewById(R.id.event_form_one_day_event_switch);
        pay_stretch_spinner = findViewById(R.id.event_form_pay_stretch_spinner);
        eventNameEditText = findViewById(R.id.event_form_name_edit);
        eventBeginDateEditText = findViewById(R.id.event_form_date_begin_edit);
        eventEndDateEditText = findViewById(R.id.event_form_date_end_edit);
        eventInfoEditText = findViewById(R.id.event_form_info_edit);
        addMembersBtn = findViewById(R.id.event_form_add_members_btn);
        eventUsersTextView = findViewById(R.id.event_form_users_textView);
        ImageView edit_start_date_btn = findViewById(R.id.event_form_date_begin_edit_btn);
        ImageView edit_end_date_btn = findViewById(R.id.event_form_date_end_edit_btn);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> pay_stretch_spinner_adapter = ArrayAdapter
                .createFromResource(this, R.array.pay_stretch_weeks,
                        android.R.layout.simple_spinner_item);
        pay_stretch_spinner_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pay_stretch_spinner.setAdapter(pay_stretch_spinner_adapter);
        pay_stretch_item_selected_index = 1;
        pay_stretch_spinner
                .setSelection(pay_stretch_item_selected_index); // set 2 weeks pay stretch per default

        addMembersBtn.setOnClickListener(v -> {
            DatabaseHandler.getAllFriendsOfUser(auth.getCurrentUser().getUid(), result -> {
                List<User> friendsList = result;
                UserListDialogFragment dialog = new UserListDialogFragment();
                dialog.build(eventUsersList, friendsList);
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
            if (!one_day_event_switch.isChecked()) {
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
            }
        });

        one_day_event_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventEndDateEditText.setText(getString(R.string.event_form_one_day));
                eventEndDateEditText.setEnabled(false);
                end_date = null;
            } else {
                eventEndDateEditText.setText("");
            }
        });

        pay_stretch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pay_stretch_item_selected_index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        beginDateSetListener = (view, year, month, day) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0, 0);
            start_date = c.getTime();
            Format format = new SimpleDateFormat("dd.MM.yyyy");
            String date_string = format.format(start_date);
            eventBeginDateEditText.setText(date_string);

            if (end_date == null){
                c.set(year, month, day, 23, 59, 59);
                end_date = c.getTime();
                date_string = format.format(end_date);
                eventEndDateEditText.setText(date_string);
            }
        };

        endDateSetListener = (view, year, month, day) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 23, 59, 59);
            end_date = c.getTime();
            Format format = new SimpleDateFormat("dd.MM.yyyy");
            String date_string = format.format(end_date);
            eventEndDateEditText.setText(date_string);
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

        boolean isValidInput = true;

        if (id == R.id.event_form_create_event_btn) {
            if (eventNameEditText.getText().toString().isEmpty()) {
                eventNameEditText.setError(getString(R.string.error_field_required));
                eventNameEditText.requestFocus();
                isValidInput = false;
            } else if (start_date == null) {
                eventBeginDateEditText.setError(getString(R.string.error_field_required));
                eventBeginDateEditText.setEnabled(true);
                isValidInput = false;
            } else {
                if (!one_day_event_switch.isChecked()) {
                    // event is not an one day event
                    if (end_date == null) {
                        eventEndDateEditText.setError(getString(R.string.error_field_required));
                        eventEndDateEditText.setEnabled(true);
                        isValidInput = false;
                    } else if (!isStartDateBeforeEndDate()) {
                        eventEndDateEditText.setError(getString(R.string.error_end_date_before_start_date));
                        eventEndDateEditText.setEnabled(true);
                        isValidInput = false;
                    }
                }
            }

            if (isValidInput) {
                // save event strings here
                if (eventUsersList == null) eventUsersList = new ArrayList<>();
                User creator = App.CurrentUser;
                String eventName = eventNameEditText.getText().toString().trim();
                eventName = eventName.substring(0, 1).toUpperCase() + eventName.substring(1);

                ArrayList<String> eventUserListStrings = new ArrayList<>();
                for (int idx = 0; idx < eventUsersList.size(); ++idx) {
                    eventUserListStrings.add(eventUsersList.get(idx).getUid());
                }

                int weeks = pay_stretch_item_selected_index+1;
                int days = weeks * 7;
                long millis = TimeUnit.DAYS.toMillis(days);
                Long deadlineday = end_date.getTime() + millis;

                Event event = new Event(
                        creator.getUid(),                                    // creatorId
                        eventName,                                           // name
                        start_date.getTime(),                                // date_begin
                        end_date == null ? null : end_date.getTime(),        // date_end
                        deadlineday,                                         // deadline_day
                        eventInfoEditText.getText().toString(),              // info
                        eventUserListStrings                                 // members
                );

                DatabaseHandler.createEvent(event);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isStartDateBeforeEndDate() {
        return !start_date.after(end_date);
    }

    public void setEventMembers(List<User> userList) {
        eventUsersList = (ArrayList<User>) userList;
        eventUsersTextView.setText(App.listToString(eventUsersList));
    }

}
package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.User;

public class EventFormActivity extends AppCompatActivity {

    private EditText eventNameEditText, eventDateEditText, eventInfoEditText;
    private Button addMembersBtn;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ArrayAdapter<String> membersAdapter;

    private static final int CONTACT_PICK_SUCCESS = 27478;

    private ArrayList<User> selectedUsers;
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


        // usersInContactList.add etc.


        addMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(
                        EventFormActivity.this, AddUsersActivity.class),
                        CONTACT_PICK_SUCCESS);
            }
        });

        eventDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
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
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                eventDateEditText.setText(date);
                fromDateSet = true;
            }
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
            } else if(eventInfoEditText.getText().toString().isEmpty()){
                eventInfoEditText.setError(getString(R.string.error_field_required));
                eventInfoEditText.requestFocus();
            } else if(eventDateEditText.getText().toString().isEmpty()){
                eventDateEditText.setError(getString(R.string.error_field_required));
                eventDateEditText.requestFocus();
            } else if(!isValidDate(eventDateEditText.getText().toString())){
                eventDateEditText.setError(getString(R.string.error_invalid_date));
            } else {
                // save event strings here

                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_PICK_SUCCESS) {

        }
    }

    private static boolean isValidDate(String inputDate) {
        final String MODULE_NAME_PATTERN = "^([1-9]|0[1-9]|[12][0-9]|3[01])[-\\.]([1-9]|0[1-9]|1[012])[-\\.]\\d{4}$";
        Pattern pattern = Pattern.compile(MODULE_NAME_PATTERN);
        Matcher matcher = pattern.matcher(inputDate);
        return matcher.matches();
    }
}
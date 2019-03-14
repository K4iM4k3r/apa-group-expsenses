package de.thm.ap.groupexpenses.view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.services.NotificationService;

public class PositionFormActivity extends BaseActivity {

    private EditText positionNameEditText, positionInfoEditText, positionValEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_form);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.position_form_create_position);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        positionNameEditText = findViewById(R.id.position_form_name_edit);
        positionInfoEditText = findViewById(R.id.position_form_info_edit);
        positionValEditText = findViewById(R.id.position_form_value_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.position_form_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.position_form_create_position_btn) {
            boolean isValidInput = true;
            if (positionNameEditText.getText().toString().isEmpty()) {
                isValidInput = false;
                positionNameEditText.setError(getString(R.string.error_field_required));
                positionNameEditText.requestFocus();
            } else if (positionValEditText.getText().toString().isEmpty()) {
                isValidInput = false;
                positionValEditText.setError(getString(R.string.error_field_required));
                positionValEditText.requestFocus();
            } else {
                float value = Float.parseFloat(positionValEditText.getText().toString());
                if(value < 0.f){
                    positionValEditText.setError(getString(R.string.error_smaller_than_zero));
                    positionValEditText.requestFocus();
                    isValidInput = false;
                } else if(value > 10000.f){
                    positionValEditText.setError(getString(R.string.error_bigger_than_10000));
                    positionValEditText.requestFocus();
                    isValidInput = false;
                }
            }
            if(isValidInput){
                // update position name, info and value here
                User creator = App.CurrentUser;
                String positionName = positionNameEditText.getText().toString().trim();
                positionName = positionName.substring(0, 1).toUpperCase() + positionName.substring(1);
                Position position = new Position(
                        creator.getUid(),
                        positionName,
                        Float.parseFloat(positionValEditText.getText().toString()),
                        positionInfoEditText.getText().toString()
                );

                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    throw new IllegalStateException("Related Event to created Position '" +
                            positionName + "' not found!");
                } else {
                    String relatedEventEid = extras.getString("relatedEventEid");
                    // tell the NotificationService that we created the position
                    NotificationService.isCaller = true;
                    DatabaseHandler.queryEvent(relatedEventEid, relatedEvent -> {
                        relatedEvent.addPosition(position);
                        DatabaseHandler.updateEvent(relatedEvent);
                        finish();
                    });
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}


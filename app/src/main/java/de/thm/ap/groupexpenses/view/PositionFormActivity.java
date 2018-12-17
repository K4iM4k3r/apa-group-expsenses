package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class PositionFormActivity extends AppCompatActivity {

    private EditText positionNameEditText, positionValEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_form);
        getSupportActionBar().setTitle(R.string.position_form_create_position);
        positionNameEditText = findViewById(R.id.position_form_name_edit);
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
            if(positionNameEditText.getText().toString().isEmpty()){
                positionNameEditText.setError(getString(R.string.error_field_required));
                positionNameEditText.requestFocus();
            } else if(positionValEditText.getText().toString().isEmpty()){
                positionValEditText.setError(getString(R.string.error_field_required));
                positionValEditText.requestFocus();
            } else {
                // save position name sting and value here
                User creator = App.CurrentUser;
                String positionName = positionNameEditText.getText().toString().trim();
                positionName = positionName.substring(0,1).toUpperCase() + positionName.substring(1);

                Position position = new Position(creator, positionName,
                        Float.parseFloat(positionValEditText.getText().toString())
                );

                Intent returnIntent = new Intent();
                returnIntent.putExtra("createdPosition", position);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

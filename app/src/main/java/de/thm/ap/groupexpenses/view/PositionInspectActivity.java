package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.fragment.ObjectListFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class PositionInspectActivity extends BaseActivity implements ObjectListFragment.ItemClickListener{

    private Event selectedEvent;
    private Position selectedPosition;
    private List<Object> userList;
    private TextView positionNameTextView;
    private ObjectListFragment objectListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_inspect);
        getSupportActionBar().setTitle(R.string.position_inspect_position_info);
        positionNameTextView = findViewById(R.id.position_inspect_pos_name);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                // cancel Activity
                finish();
            } else {
                selectedEvent = (Event) extras.getSerializable("event");
                selectedPosition = (Position) extras.getSerializable("position");
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("event");
            selectedPosition = (Position) savedInstanceState.getSerializable("position");
        }

        if(selectedEvent != null && selectedPosition != null){
            userList = (List<Object>)(List<?>) selectedEvent.getMembers();
            positionNameTextView.setText(selectedPosition.getTopic());
            objectListFragment = (ObjectListFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.position_inspect_fragment);
            // IMPORTANT: add event at the end of list to be used and subsequently deleted
            // in ObjectListFragment
            if(!userList.isEmpty())
                userList.add(selectedPosition);
            objectListFragment.createFragmentObjects(userList, "PositionInspect");
        } else
            finish();

        Button pay_btn = findViewById(R.id.position_inspect_pay_btn);
        pay_btn.setOnClickListener(v -> {
            // pay position
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.position_inspect_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.position_inspect_edit_position:

                break;

            case R.id.position_inspect_delete_position:

                break;

            case R.id.position_inspect_show_history:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentObjectClick(Object object) {

    }
}

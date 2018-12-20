package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.fragment.ObjectListFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;

public class PositionActivity extends BaseActivity implements ObjectListFragment.ItemClickListener{

    private Event selectedEvent;
    private List<Object> positionList;
    private ObjectListFragment objectListFragment;
    private static final int POSITION_CREATE_SUCCESS = 11215;
    private static final int POSITION_INSPECT_SUCCESS = 42562;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        getSupportActionBar().setTitle(R.string.position_inspect_positions);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                // cancel Activity
                finish();
            } else {
                selectedEvent = (Event) extras.getSerializable("event");
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("event");
        }

        if(selectedEvent != null){
            positionList = (List<Object>)(List<?>) selectedEvent.getPositions();
            objectListFragment = (ObjectListFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.position_fragment);
            // IMPORTANT: add event at the end of list to be used and subsequently deleted
            // in ObjectListFragment
            if(!positionList.isEmpty())
                positionList.add(selectedEvent);
            objectListFragment.createFragmentObjects(positionList, "Position");
        } else
            finish();

        FloatingActionButton createPositionBtn = findViewById(R.id.create_position_btn);
        createPositionBtn.setOnClickListener(v -> startActivityForResult(new Intent(PositionActivity.this,
                PositionFormActivity.class), POSITION_CREATE_SUCCESS)
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.position_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.position_menu_done:
                // return Event to EventActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("inspectedEvent", selectedEvent);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;

            case R.id.position_menu_info:
                // display event info, date and members

                AlertDialog.Builder builder = new AlertDialog.Builder( PositionActivity.this);
                builder.setTitle(R.string.event_form_info);
                builder.setMessage(
                        Html.fromHtml(selectedEvent.getInfo() + "<br><br>" + "<b>"
                                + getString(R.string.event_form_begin) + ": </b><br>" + selectedEvent.getDate()
                                + "<br><br><b>"
                                + getString(R.string.event_form_users) + "("
                                + selectedEvent.getMembers().size() + "): " + "</b><br>" +
                        App.listToHTMLString(selectedEvent.getMembers()))
                );
                builder.show();
                break;
        }

        if (id == R.id.position_menu_done) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode) {
                case POSITION_CREATE_SUCCESS:
                    Position position  = (Position) data.getExtras().getSerializable("createdPosition");
                    positionList.add(position);
                    positionList.add(selectedEvent);
                    objectListFragment.updateFragmentObjects(positionList, "Position");
                    break;
            }
        }
    }

    @Override
    public void onFragmentObjectClick(Object object) {
        Intent intent = new Intent(PositionActivity.this, PositionInspectActivity.class);
        intent.putExtra("event", selectedEvent);
        intent.putExtra("position", (Position)object);

        startActivityForResult(intent, POSITION_INSPECT_SUCCESS);
    }
}

package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.App;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.fragment.ObjectListFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class EventActivity extends BaseActivity implements ObjectListFragment.ItemClickListener{

    private List<Event> events;
    private ObjectListFragment objectListFragment;

    private static final int EVENT_CREATE_SUCCESS = 19438;
    private static final int EVENT_INSPECT_SUCCESS = 26374;

    private static final String TAG = "EventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);

        TextView eventsLoadingTextView = findViewById(R.id.events_loading_textView);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null || !currentUser.isEmailVerified()){
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            setCurrentUser(currentUser);
            DatabaseHandler.getAllUserEvents(currentUser.getUid(), result -> {
                eventsLoadingTextView.setVisibility(View.GONE);
                events = result;
                if(events == null) events = new ArrayList<>();
                objectListFragment = (ObjectListFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.event_fragment);
                objectListFragment.createFragmentObjects(result, "Event");

            });

        }


        FloatingActionButton createEventBtn = findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(v -> startActivityForResult(
                new Intent(EventActivity.this, EventFormActivity.class),
                EVENT_CREATE_SUCCESS)
        );

        auth = FirebaseAuth.getInstance();
    }

    private void setCurrentUser(FirebaseUser currentUser) {
        DatabaseHandler.queryUser(currentUser.getUid(), result -> {
            App.CurrentUser = result;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Event event;
            switch(requestCode) {
                case EVENT_CREATE_SUCCESS:
                    event = (Event) data.getExtras().getSerializable("createdEvent");
                    events.add(event);
                    DatabaseHandler.createEvent(event);
                    objectListFragment.updateFragmentObjects(events, null, "Event");
                    break;

                case EVENT_INSPECT_SUCCESS: // get inspected Event and update it (its positions)
                    event = (Event) data.getExtras().getSerializable("inspectedEvent");
                    boolean eventFound = false;

                    for(int idx = 0; idx < events.size(); ++idx) {
                        if (event.getEid().equals((events.get(idx)).getEid())) {
                            events.set(idx, event);
                            eventFound = true;
                            break;
                        }
                    }

                    if(!eventFound)
                        throw new IllegalStateException("Inspected Event not found!");
                    else {
                        objectListFragment.updateFragmentObjects(events, null, "Event");
                    }

                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onFragmentObjectClick(Object event) {
        Intent intent = new Intent(EventActivity.this, PositionActivity.class);
        intent.putExtra("event", (Event)event);
        startActivityForResult(intent, EVENT_INSPECT_SUCCESS);
    }
}

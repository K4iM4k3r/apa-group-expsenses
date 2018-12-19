package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.App;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.fragment.ObjectListFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class EventActivity extends BaseActivity implements ObjectListFragment.ItemClickListener{

    private List<Object> events;
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

        events = new ArrayList<>();

        ArrayList<User> userList = new ArrayList<>();
        User myUser = new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de");
        User myUser2 = new User(2, "Hendrik", "Kegel", "oof");
        User myUser3 = new User(3, "Kai", "Schäfer", "oof2");
        User myUser4 = new User(4, "David", "Omran", "oof3");
        User myUser5 = new User(5, "Ulf", "Smolka", "ka");
        App.CurrentUser = myUser;
        userList.add(myUser);
        userList.add(myUser2);
        userList.add(myUser3);
        userList.add(myUser4);
        userList.add(myUser5);
        //userList.add(new User(6, "Dominik", "Herz", "kjlkalsd"));
        //userList.add(new User(7, "Aris", "Christidis", "lolo"));
        //userList.add(new User(8, "KQC", "NA", "xD"));
        //userList.add(new User(9, "Adam", "Bdam", "dontEvenknow"));
        //userList.add(new User(10, "Max", "Muster", "maybe@fdm"));
        //userList.add(new User(11, "Rainer", "Rein", "lalalala"));

        Event testEvent = new Event(
                new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de"),
                "TestEvent1",
                "13.08.2019",
                "Eventinfo",
                userList
        );

        Event testEvent2 = new Event(
                new User(1, "Hendrik", "Kegel", "dontknow"),
                "TestEvent2",
                "01.12.2033",
                "Eventinfo blblbablablabla",
                userList
        );

        testEvent.addPosition(new Position(myUser, "TestPosition", 50.f));
        testEvent.addPosition(new Position(myUser, "TestPosition2", 20.f));

        testEvent2.addPosition(new Position(myUser2, "TestPosition3", 100.f));
        testEvent2.addPosition(new Position(myUser2, "TestPosition4", 100.f));

        events.add(testEvent);
        events.add(testEvent2);


        objectListFragment = (ObjectListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.event_fragment);
        objectListFragment.createFragmentObjects(events, "Event");

        FloatingActionButton createEventBtn = findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(v -> startActivityForResult(
                new Intent(EventActivity.this, EventFormActivity.class),
                EVENT_CREATE_SUCCESS)
        );

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!events.isEmpty()){
            // do nothing?
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Event event;
            switch(requestCode) {
                case EVENT_CREATE_SUCCESS:
                    event = (Event) data.getExtras().getSerializable("createdEvent");
                    events.add(event);
                    objectListFragment.updateFragmentObjects(events, "Event");
                    break;

                case EVENT_INSPECT_SUCCESS: // get inspected Event and update it (its positions)
                    event = (Event) data.getExtras().getSerializable("inspectedEvent");
                    boolean eventFound = false;

                    for(int idx = 0; idx < events.size(); ++idx) {
                        if (event.getId() == ((Event) events.get(idx)).getId()) {
                            events.set(idx, event);
                            eventFound = true;
                            break;
                        }
                    }

                    if(!eventFound)
                        throw new IllegalStateException("Inspected Event not found!");
                    else
                        objectListFragment.updateFragmentObjects(events, "Event");
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null || !currentUser.isEmailVerified()){
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onFragmentObjectClick(Object event) {
        Intent intent = new Intent(EventActivity.this, PositionActivity.class);
        intent.putExtra("event", (Event)event);

        startActivityForResult(intent, EVENT_INSPECT_SUCCESS);
    }
}

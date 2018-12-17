package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;

public class EventActivity extends BaseActivity {

    private TextView noEvents;
    private ListView eventList;
    private ArrayList<Event> events;
    private EventArrayAdapter eventAdapter;
    private View headerView;

    private static final int EVENT_CREATE_SUCCESS = 19438;
    private static final int POSITION_CREATE_SUCCESS = 26374;

    private static final String TAG = "EventActivity";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);

        CallLogFragment fragmentTest2 = (CallLogFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentTest);
        fragmentTest2.setFragmentText("avs");


        //FragmentTest fragmentTest = (FragmentTest)getSupportFragmentManager().findFragmentById(R.id.fragmentTest);
        //fragmentTest.setFragmentText("TestText");

        noEvents = findViewById(R.id.no_events);
        eventList = findViewById(R.id.event_list);
        headerView = getLayoutInflater().inflate(R.layout.event_list_header, null);

        eventList.addHeaderView(headerView);

        events = new ArrayList<>();

        ArrayList<User> userList = new ArrayList<>();
        User myUser = new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de");
        User myUser2 = new User(2, "Hendrik", "Kegel", "oof");
        App.CurrentUser = myUser;
        userList.add(myUser);
        userList.add(myUser2);
        userList.add(new User(3, "Kai", "Schäfer", "oof2"));
        userList.add(new User(4, "David", "Omran", "oof3"));
        userList.add(new User(5, "Ulf", "Smolka", "ka"));
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

        testEvent.addPosition(new Position(myUser, "TestPosition", 30));
        testEvent.addPosition(new Position(myUser, "TestPosition2", 30));
        //testEvent.addPosition(new Position(myUser, "TestPosition3", -98));

        testEvent2.addPosition(new Position(myUser2, "TestPosition4", 30));
        //testEvent2.addPosition(new Position(myUser, "TestPosition5", -17));
        //testEvent2.addPosition(new Position(myUser, "TestPosition6", 128));

        events.add(testEvent);
        events.add(testEvent2);

        eventAdapter = new EventArrayAdapter(this, events);
        eventList.setAdapter(eventAdapter);

        FloatingActionButton createEventBtn = findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(EventActivity.this,
                        EventFormActivity.class), EVENT_CREATE_SUCCESS);
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (eventList.getHeaderViewsCount() == 1 && position == 0) {
                    // click on balance summary field (on top of list)
                    return;
                }

                Event selectedEvent = (Event) eventList.getItemAtPosition(position);

                Intent intent = new Intent(EventActivity.this, PositionActivity.class);
                intent.putExtra("event", selectedEvent);

                startActivityForResult(intent, POSITION_CREATE_SUCCESS);
            }
        });
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!events.isEmpty()){
            noEvents.setVisibility(View.GONE);
            // calculate event balance here
            float event_balance = Stats.getBalance(events);

            ((TextView)headerView.findViewById(R.id.event_balance_summary_val))
                    .setText(new DecimalFormat("0.00").format(event_balance) + " " + getString(R.string.euro));

            if(event_balance < 0){
                headerView.findViewById(R.id.event_list_header_layout)
                        .setBackgroundColor(Color.parseColor("#ef4545"));   // red
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode) {
                case EVENT_CREATE_SUCCESS:
                    Event event  = (Event) data.getExtras().getSerializable("createdEvent");
                    events.add(event);
                    eventAdapter.clear();
                    eventAdapter.addAll(events);
                    break;
            }
        }
    }



    private class EventArrayAdapter extends ArrayAdapter<Event> {
        private Context mContext;

        public EventArrayAdapter(@NonNull Context context, ArrayList<Event> list) {
            super(context, 0, list);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;

            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.event_list_item, parent,
                        false);

            Event e = getItem(position);

            TextView name = listItem.findViewById(R.id.name2);
            name.setText(e.getName());

            TextView creatorAndDate =  listItem.findViewById(R.id.creatorAndDate);
            creatorAndDate.setText("Ersteller: " + e.getCreator());

            TextView balance =  listItem.findViewById(R.id.balance2);

            float balance_f = Stats.getEventBalance(e);

            balance.setText(new DecimalFormat("0.00").format(balance_f) + " €");

            return listItem;
        }

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


}

package de.thm.ap.groupexpenses.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;

public class EventActivity extends AppCompatActivity {

    private TextView eventSummary, totalBalance;
    private ListView eventList;
    private ArrayList<Event> events;
    private EventArrayAdapter eventAdapter;

    private static final int EVENT_CREATE_SUCCESS = 19438;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventSummary = findViewById(R.id.event_summary);
        totalBalance = findViewById(R.id.balance_total);
        eventList = findViewById(R.id.event_list);

        events = new ArrayList<>();

        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de"));
        userList.add(new User(2, "Hendrik", "Kegel", "oof"));
        userList.add(new User(3, "Kai", "Schäfer", "oof2"));
        userList.add(new User(4, "David", "Omran", "oof3"));
        userList.add(new User(5, "Ulf", "Smolka", "ka"));
        userList.add(new User(6, "Dominik", "Herz", "kjlkalsd"));
        userList.add(new User(7, "Aris", "Christidis", "lolo"));
        userList.add(new User(8, "KQC", "NA", "xD"));
        userList.add(new User(9, "Adam", "Bdam", "dontEvenknow"));
        userList.add(new User(10, "Max", "Muster", "maybe@fdm"));
        userList.add(new User(11, "Rainer", "Rein", "lalalala"));

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
                Event selectedEvent = (Event) eventList.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder( EventActivity.this);
                builder.setTitle(R.string.event_form_info);
                builder.setMessage(
                        Html.fromHtml(selectedEvent.getInfo() + "<br><br>" +
                        "<b>" + getString(R.string.event_form_users) + ": " + "</b>" +
                        App.listToHTMLString(selectedEvent.getUsers()))
                );
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!events.isEmpty()){
            eventSummary.setText(getString(R.string.total_balance) + ":");
            eventSummary.setTextSize(30.f);
            eventSummary.setTextColor(Color.RED);
            // calculate balance here and show it in 'totalBalance.setText'
            totalBalance.setVisibility(View.VISIBLE);
            totalBalance.setText("-20€");
            totalBalance.setTextSize(30.f);
            totalBalance.setTextColor(Color.RED);
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
        if (requestCode == EVENT_CREATE_SUCCESS) {
            Event event  = (Event) data.getExtras().getSerializable("createdEvent");
            events.add(event);
            eventAdapter.clear();
            eventAdapter.addAll(events);
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

            TextView name = listItem.findViewById(R.id.name);
            name.setText(e.getName());

            TextView creatorAndDate =  listItem.findViewById(R.id.creatorAndDate);
            creatorAndDate.setText("Erstellt von " + e.getCreator() + "  |  Start: " + e.getDate());

            TextView balance =  listItem.findViewById(R.id.balance);
            balance.setText("-10€");

            return listItem;
        }

    }
}

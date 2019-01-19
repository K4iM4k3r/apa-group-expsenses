package de.thm.ap.groupexpenses.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.App;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;

public class EventActivity extends BaseActivity implements PositionEventListFragment.ItemClickListener {

    private List<Event> events;
    private PositionEventListFragment positionEventListFragment;
    private EventListLiveData listLiveData;

    private static final String TAG = "EventActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
//        Toolbar toolbar = findViewById(R.id.event_toolbar);
//        setSupportActionBar(toolbar);

        TextView eventsLoadingTextView = findViewById(R.id.events_loading_textView);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            DatabaseHandler.queryUser(currentUser.getUid(), result -> {
                App.CurrentUser = result;
                listLiveData = DatabaseHandler.getEventListLiveData(currentUser.getUid());
                listLiveData.observe(this, eventList -> {
                    eventsLoadingTextView.setVisibility(View.GONE);
                    events = eventList;
                    if (events == null) events = new ArrayList<>();
                    positionEventListFragment = (PositionEventListFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.event_fragment);
                    positionEventListFragment.updateList(eventList, null);
                });
            });

        }
        FloatingActionButton createEventBtn = findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(v -> startActivity(
                new Intent(EventActivity.this, EventFormActivity.class)));
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onFragmentObjectClick(Object event) {
        if (event == null) return;

        Intent intent = new Intent(EventActivity.this, PositionActivity.class);
        intent.putExtra("eventEid", ((Event) event).getEid());
        startActivity(intent);
    }
}

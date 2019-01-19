package de.thm.ap.groupexpenses.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.view.dialog.CashCheckDialog;
import de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment;
import de.thm.ap.groupexpenses.view.fragment.UserListDialogFragment;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.dialog.EventInfoDialog;
import de.thm.ap.groupexpenses.view.dialog.PositionInfoDialog;

public class PositionActivity extends BaseActivity implements PositionEventListFragment.ItemClickListener {

    private Event selectedEvent;
    private List<User> eventMembers;
    private PositionEventListFragment positionEventListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.position_inspect_positions);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String selectedEventEid = null;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                // cancel Activity
                finish();
            } else {
                selectedEventEid = extras.getString("eventEid");
            }
        } else {
            selectedEventEid = savedInstanceState.getString("eventEid");
        }
        if (selectedEventEid != null) {
            EventLiveData eventLiveData = DatabaseHandler.getEventLiveData(selectedEventEid);
            eventLiveData.observe(this, event -> {
                selectedEvent = event;
                positionEventListFragment = (PositionEventListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.position_fragment);
                positionEventListFragment.updateList(event.getPositions(), event);
            });

            UserListLiveData userListLiveData = DatabaseHandler.getAllMembersOfEvent(selectedEventEid);
            userListLiveData.observe(this, userList -> {
                eventMembers = userList;
            });
        } else {
            finish();
        }
        FloatingActionButton createPositionBtn = findViewById(R.id.create_position_btn);
        createPositionBtn.setOnClickListener(v -> {
            if (selectedEvent.getMembers() == null || selectedEvent.getMembers().size() == 1) {
                Toast error_no_members_toast = Toast.makeText(this, R.string.error_no_members,
                        Toast.LENGTH_LONG);
                error_no_members_toast.show();
            } else {
                Intent intent = new Intent(PositionActivity.this, PositionFormActivity.class);
                intent.putExtra("relatedEventEid", selectedEvent.getEid());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.position_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.position_menu_inspect_users:
                // display event user list
                DatabaseHandler.getAllFriendsOfUser(auth.getCurrentUser().getUid(), friendsList -> {
                    UserListDialogFragment dialog = new UserListDialogFragment();
                    dialog.build(selectedEvent, eventMembers, friendsList);
                    dialog.show(getFragmentManager(), "edit_event");
                });
                break;

            case R.id.position_menu_info:
                // display event info
                if (App.CurrentUser.getUid().equals(selectedEvent.getCreatorId())) {
                    new EventInfoDialog(selectedEvent, null, this);
                } else {
                    DatabaseHandler.queryUser(selectedEvent.getCreatorId(), eventCreator -> {
                        if (eventCreator == null) {
                            new EventInfoDialog(selectedEvent, getString(R.string.deleted_user), this);
                        } else {
                            new EventInfoDialog(selectedEvent, eventCreator.getNickname(),
                                    this);
                        }
                    });
                }
                break;

            case R.id.position_menu_cash_check:
                // display event cash check
                new CashCheckDialog(this, selectedEvent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentObjectClick(Object object) {
        if (object == null) return;

        // show a custom alert dialog with position information
        Position selectedPosition = (Position) object;
        if (App.CurrentUser.getUid().equals(selectedPosition.getCreatorId())) {
            new PositionInfoDialog(selectedPosition, selectedEvent, null, this);
        } else {
            DatabaseHandler.queryUser(selectedPosition.getCreatorId(), positionCreator -> {
                if (positionCreator == null) {
                    new PositionInfoDialog(selectedPosition, selectedEvent, getString(R.string.deleted_user),
                            this);
                } else {
                    new PositionInfoDialog(selectedPosition, selectedEvent, positionCreator.getNickname(),
                            this);
                }
            });
        }
    }
}

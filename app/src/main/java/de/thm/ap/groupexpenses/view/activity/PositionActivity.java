package de.thm.ap.groupexpenses.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.dialog.EventInfoDialog;
import de.thm.ap.groupexpenses.view.dialog.PositionInfoDialog;
import de.thm.ap.groupexpenses.view.fragment.CashFragment;
import de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment;
import de.thm.ap.groupexpenses.view.fragment.UserListDialogFragment;

import static de.thm.ap.groupexpenses.model.Event.LifecycleState.CLOSED;

public class PositionActivity extends BaseActivity implements PositionEventListFragment.ItemClickListener {

    private Event selectedEvent;
    private List<User> eventMembers;
    private ViewPager mViewPager;

    FloatingActionButton lFab;
    FloatingActionButton rFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
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
                if (actionBar != null && event != null) {
                    actionBar.setTitle(event.getName());
                    View actionBarView = findTextViewWithText(
                            getWindow().getDecorView(), actionBar.getTitle().toString());
                    if (actionBarView != null) {
                        actionBarView.setOnClickListener(v -> {
                            // show event info
                            if (App.CurrentUser.getUid().equals(selectedEvent.getCreatorId())) {
                                new EventInfoDialog(selectedEvent, null, App.CurrentUser.getUid(),
                                        this);
                            } else {
                                DatabaseHandler.queryUser(selectedEvent.getCreatorId(), eventCreator -> {
                                    if (eventCreator == null) {
                                        new EventInfoDialog(selectedEvent, getString(R.string.deleted_user), null,
                                                this);
                                    } else {
                                        new EventInfoDialog(selectedEvent, eventCreator.getNickname(),
                                                eventCreator.getUid(), this);
                                    }
                                });
                            }
                        });
                    }

                }
            });

            UserListLiveData userListLiveData = DatabaseHandler.getAllMembersOfEvent(selectedEventEid);
            userListLiveData.observe(this, userList -> eventMembers = userList);
        } else {
            finish();
        }

        lFab = findViewById(R.id.leftFab);
        rFab = findViewById(R.id.rightFab);

        CollectionPagerAdapter mCollectionPagerAdapter = new CollectionPagerAdapter(
                getSupportFragmentManager(), 2, selectedEventEid);
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mCollectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_expenses)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_balance)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void chooseFabs(){
        boolean isEventCreator = selectedEvent.getCreatorId().equals(App.CurrentUser.getUid());
        Event.LifecycleState lifecycleState = selectedEvent.getLifecycleState();

        lFab.setVisibility(View.GONE);
        rFab.setVisibility(View.GONE);


        if (isEventCreator && selectedEvent.isClosable() && lifecycleState!=CLOSED){
           // lFab shown + delete event
            lFab.setVisibility(View.VISIBLE);
            lFab.setImageResource(R.drawable.ic_delete_white_24dp);

        }
        else if (!isEventCreator && selectedEvent.isEven(App.CurrentUser.getUid()) && lifecycleState!=CLOSED){
            // lFab shown + leave event
            lFab.setVisibility(View.VISIBLE);
            lFab.setImageResource(R.drawable.ic_exit_white_24dp);
        }

        if (isEventCreator && lifecycleState==CLOSED){
            // rFab shown + delete
            rFab.setVisibility(View.VISIBLE);
            rFab.setImageResource(R.drawable.ic_delete_white_24dp);

        }
        else if (!isEventCreator && lifecycleState==CLOSED){
            // rFab shown + leave
            rFab.setVisibility(View.VISIBLE);
            rFab.setImageResource(R.drawable.ic_exit_white_24dp);
        }
        else {
            // rFab + addPosition
            rFab.setVisibility(View.VISIBLE);
            rFab.setImageResource(R.drawable.ic_add_white_24dp);
            rFab.setOnClickListener(v -> {
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
    }

    /*
    Search for the event title in all views of PositionActivity in order to find the action bar view!
    This might cause a bug when a position in this event is called exactly like the event title!
     */
    @Nullable
    public static TextView findTextViewWithText(@Nullable View toCheck, String toFind) {
        if (toCheck instanceof TextView) {
            String foundText = ((TextView) toCheck).getText().toString();
            if (foundText.equals(toFind)) {
                return (TextView) toCheck;
            }
        } else if (toCheck instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) toCheck).getChildCount(); i++) {
                TextView found = findTextViewWithText(((ViewGroup) toCheck).getChildAt(i), toFind);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
            case R.id.position_menu_add_invite_users:
                // display event user list
                DatabaseHandler.getAllFriendsOfUser(Objects.requireNonNull(auth.getCurrentUser()).getUid(), friendsList -> {
                    UserListDialogFragment dialog = new UserListDialogFragment();
                    dialog.build(selectedEvent, eventMembers, friendsList);
                    dialog.show(getFragmentManager(), "edit_event");
                });
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
            new PositionInfoDialog(selectedPosition, selectedEvent, null,
                    App.CurrentUser.getUid(), this);
        } else {
            DatabaseHandler.queryUser(selectedPosition.getCreatorId(), positionCreator -> {
                if (positionCreator == null) {
                    new PositionInfoDialog(selectedPosition, selectedEvent, getString(R.string.deleted_user),
                            null, this);
                } else {
                    new PositionInfoDialog(selectedPosition, selectedEvent, positionCreator.getNickname(),
                            positionCreator.getUid(), this);
                }
            });
        }
    }

    public void setUsersPaid(List<User> usersPaidList, Position position) {
        for (User user : usersPaidList) {
            position.removeDebtor(user.getUid());
        }
        selectedEvent.updatePosition(position);
        DatabaseHandler.updateEvent(selectedEvent);
    }

    private class CollectionPagerAdapter extends FragmentPagerAdapter {
        private int numberPages;
        private String eid;

        CollectionPagerAdapter(FragmentManager fm, int numberPages, String eid) {
            super(fm);
            this.numberPages = numberPages;
            this.eid = eid;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            Bundle args = new Bundle();
            switch (i) {
                case 0:
                    fragment = new PositionEventListFragment<>();
                    args.putString(CashFragment.SELECTED_EID, eid);
                    break;
                case 1:
                    fragment = new CashFragment();
                    args.putString(CashFragment.SELECTED_EID, eid);

                    break;
                default:
                    fragment = new DemoObjectFragment();
                    args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
                    break;
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return this.numberPages;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    public static class DemoObjectFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_1, container, false);
            Bundle args = getArguments();
            if (args != null) {
                ((TextView) rootView.findViewById(R.id.textView)).setText(
                        Integer.toString(args.getInt(ARG_OBJECT)));
            }
            return rootView;
        }
    }
}

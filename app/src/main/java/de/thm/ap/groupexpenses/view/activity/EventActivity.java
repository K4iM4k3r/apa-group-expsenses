package de.thm.ap.groupexpenses.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.view.fragment.CashFragment;
import de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment;

import static de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment.USERID;

public class EventActivity extends BaseActivity implements PositionEventListFragment.ItemClickListener {

    private EventListLiveData listLiveData;
    private ViewPager mViewPager;
    private static final String TAG = "EventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView eventsLoadingTextView = findViewById(R.id.events_loading_textView);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            DatabaseHandler.queryUser(currentUser.getUid(), result -> {
                App.CurrentUser = result;
                listLiveData = DatabaseHandler.getEventListLiveData(currentUser.getUid());
                listLiveData.observe(this, eventList -> eventsLoadingTextView.setVisibility(View.GONE));
            });

        }
        FloatingActionButton createEventBtn = findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(v -> startActivity(
                new Intent(EventActivity.this, EventFormActivity.class)));
        auth = FirebaseAuth.getInstance();

        CollectionPagerAdapter mCollectionPagerAdapter = new CollectionPagerAdapter(
                getSupportFragmentManager(), 2, auth.getUid());
        mViewPager = findViewById(R.id.pagerEventList);
        mViewPager.setAdapter(mCollectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layoutEventList);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_events)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_cash)));
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

    private class CollectionPagerAdapter extends FragmentPagerAdapter {
        private int numberPages;
        private String uid;

        CollectionPagerAdapter(FragmentManager fm, int numberPages, String uid) {
            super(fm);
            this.numberPages = numberPages;
            this.uid = uid;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            Bundle args = new Bundle();
            switch (i){
                case 0:
                    fragment = new PositionEventListFragment<>();
                    args.putString(USERID, uid);
                    break;
                case 1:
                    fragment = new CashFragment();
                    args.putString(USERID, uid);
                    break;
                default:
                    fragment = new PositionActivity.DemoObjectFragment();
                    args.putInt(PositionActivity.DemoObjectFragment.ARG_OBJECT, i + 1);
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
}

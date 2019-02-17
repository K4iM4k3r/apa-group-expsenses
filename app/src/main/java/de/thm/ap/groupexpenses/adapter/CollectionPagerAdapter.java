package de.thm.ap.groupexpenses.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.thm.ap.groupexpenses.view.activity.PositionActivity;
import de.thm.ap.groupexpenses.view.activity.TabTestActivity;
import de.thm.ap.groupexpenses.view.fragment.CashFragment;
import de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment;

public class CollectionPagerAdapter extends FragmentPagerAdapter {
    private int numberPages;
    private String eid;

    public CollectionPagerAdapter(FragmentManager fm, int numberPages, String eid) {
        super(fm);
        this.numberPages = numberPages;
        this.eid = eid;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch (i){
            case 0:
                fragment = new CashFragment();
                break;
            case 1:
                fragment = new PositionEventListFragment();
                break;
            default:
                fragment = new PositionActivity.DemoObjectFragment();
                break;
        }
//            fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(TabTestActivity.DemoObjectFragment.ARG_OBJECT, i + 1);
        args.putString(CashFragment.SELECTED_EID, eid);
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
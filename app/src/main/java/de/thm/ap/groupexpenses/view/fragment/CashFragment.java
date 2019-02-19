package de.thm.ap.groupexpenses.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.model.Stats;

import static de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment.USERID;

public class CashFragment extends Fragment {
    public static final String SELECTED_EID = "seid";
    Map<String, Float> cash_check_map;
    ArrayList<UserValue> userValueList;
    private UserValueArrayAdapter userValueArrayAdapter;
    private ListView cash_check_list;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_cash_check, container, false);
        cash_check_list = rootView.findViewById(R.id.dialog_cash_check_list);
        Bundle args = getArguments();

        if (args != null) {
            String eid = args.getString(SELECTED_EID);
            String uid = args.getString(USERID);
            if (eid != null){
                EventLiveData eventLiveData;
                eventLiveData = DatabaseHandler.getEventLiveData(eid);
                eventLiveData.observe(this, event -> {
                    if (event != null) {
                        userValueList = new ArrayList<>();
                        cash_check_map = event.getBalanceTable(App.CurrentUser.getUid());
                        buildCashView();
                    }
                });
            }
            else if (uid != null){
                EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(uid);
                listLiveData.observe(this, eventList -> {

                    if (eventList != null) {
                        userValueList = new ArrayList<>();
                        cash_check_map = Stats.getGlobalBalanceTable(App.CurrentUser,eventList);
                        buildCashView();
                    }
                });
            }
        }
        return rootView;
    }
    private class UserValue {
        private String name;
        private float value;

        UserValue(String name, float value) {
            this.name = name;
            this.value = value;
        }
    }

    private class UserValueArrayAdapter extends ArrayAdapter<UserValue> {
        private Context mContext;
        private List<UserValue> usersValueList;

        private UserValueArrayAdapter(@NonNull Context context, List<UserValue> list) {
            super(context, 0, list);
            mContext = context;
            usersValueList = list;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;

            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.dialog_cash_check_list_row,
                        parent, false);

            UserValue currentUserValue = usersValueList.get(position);

            ImageView forward_arrow = listItem.findViewById(R.id.dialog_cash_check_arrow_forward);
            ImageView back_arrow = listItem.findViewById(R.id.dialog_cash_check_arrow_back);
            if (currentUserValue.value < 0) { // App.CurrentUser owes money
                back_arrow.setVisibility(View.VISIBLE);
                forward_arrow.setVisibility(View.GONE);
                back_arrow.setOnClickListener(v -> {
                    // pay ALL depts to user here (multiple positions)
                    // TODO: David pay system
                    float val = currentUserValue.value * (-1);

                    // TODO: After successful payment -> delete user from ALL positions he just payed for
                });
            } else {    // App.CurrentUser gets money
                forward_arrow.setVisibility(View.VISIBLE);
                back_arrow.setVisibility(View.GONE);
                forward_arrow.setOnClickListener(v -> {
                    // TODO: remind user for payment here via e-mail
                    float val = currentUserValue.value;

                    //example
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("message/rfc822");
//                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{});
//                    i.putExtra(Intent.EXTRA_SUBJECT, "You have to pay money");
//                    i.putExtra(Intent.EXTRA_TEXT   , "body of email");
//                    try {
//                        startActivity(Intent.createChooser(i, "Send mail..."));
//                    } catch (android.content.ActivityNotFoundException ex) {
//                        Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                    }
                });
            }


            TextView userValueName = listItem.findViewById(R.id.dialog_cash_check_name);
            TextView userValueBalance = listItem.findViewById(R.id.dialog_cash_check_balance);

            userValueName.setText(currentUserValue.name);
            userValueBalance.setText(new DecimalFormat("0.00€").format(currentUserValue.value));

            return listItem;
        }
    }

    private void buildCashView(){
        List<String> keyList = new ArrayList<>(cash_check_map.keySet());
        int idx = 0;
        while (idx < keyList.size()) {
            userValueList.add(null);
            idx++;
        }
        for (idx = 0; idx < keyList.size(); ++idx) {
            final String key = keyList.get(idx);
            DatabaseHandler.queryUser(key, user -> {
                for (int idx2 = 0; idx2 < userValueList.size(); ++idx2) {
                    if (userValueList.get(idx2) == null) {
                        if (user != null) {
                            userValueList.set(idx2, new UserValue(user.getNickname(), cash_check_map.get(key)));
                        } else {
                            userValueList.set(idx2, new UserValue(getString(R.string.unknown),
                                    cash_check_map.get(key)));
                        }
                        break;
                    }
                }
                if (!userValueList.contains(null)) {
                    // create and set adapter
                    userValueArrayAdapter = new UserValueArrayAdapter(Objects.requireNonNull(getContext()), userValueList);
                    cash_check_list.setAdapter(userValueArrayAdapter);
                }
            });
        }
    }
}

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
import de.thm.ap.groupexpenses.livedata.EventLiveData;

public class CashFragment extends Fragment {
    private static final String SELECTED_EID = "seid";
    Map<String, Float> cash_check_map;
    ArrayList<UserValue> userValueList;
    private UserValueArrayAdapter userValueArrayAdapter;
    private TextView event_name;
    private ListView cash_check_list;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_cash_check, container, false);
        event_name = rootView.findViewById(R.id.dialog_cash_check_name);
        cash_check_list = rootView.findViewById(R.id.dialog_cash_check_list);
        Bundle args = getArguments();

        EventLiveData eventLiveData;
        if (args != null) {
            eventLiveData = DatabaseHandler.getEventLiveData(args.getString(SELECTED_EID));
            eventLiveData.observe(this, event -> {
                if (event != null) {
                    event_name.setText(event.getName());
                    userValueList = new ArrayList<>();
                    cash_check_map = event.getBalanceTable(App.CurrentUser.getUid());
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
            });
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
            } else {    // App.CurrentUser gets money
                forward_arrow.setVisibility(View.VISIBLE);
                back_arrow.setVisibility(View.GONE);
            }


            TextView userValueName = listItem.findViewById(R.id.dialog_cash_check_name);
            TextView userValueBalance = listItem.findViewById(R.id.dialog_cash_check_balance);

            userValueName.setText(currentUserValue.name);
            userValueBalance.setText(new DecimalFormat("0.00€").format(currentUserValue.value));

            return listItem;
        }
    }
}

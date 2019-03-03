package de.thm.ap.groupexpenses.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.view.activity.PayActivity;
import de.thm.ap.groupexpenses.view.dialog.ProfileInfoDialog;

import static de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment.USERID;

public class CashFragment extends Fragment {
    public static final String SELECTED_EID = "seid";
    private Map<String, Float> cash_check_map;
    private ArrayList<UserValue> userValueList;
    private UserValueArrayAdapter userValueArrayAdapter;
    private ListView cash_check_list;
    private Event event;
    private List<Event> eventList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_cash_check, container, false);
        cash_check_list = rootView.findViewById(R.id.dialog_cash_check_list);
        TextView header_val = rootView.findViewById(R.id.dialog_cash_check_header_val);
        ImageView help_btn = rootView.findViewById(R.id.dialog_cash_check_help_btn);
        LinearLayout help_layout = rootView.findViewById(R.id.dialog_cash_check_help_layout);
        Bundle args = getArguments();

        help_btn.setOnClickListener(v -> {
            if (help_layout.getVisibility() == View.GONE) {
                help_layout.setVisibility(View.VISIBLE);
            } else {
                help_layout.setVisibility(View.GONE);
            }

        });

        if (args != null) {
            String eid = args.getString(SELECTED_EID);
            String uid = args.getString(USERID);
            if (eid != null) {
                EventLiveData eventLiveData;
                eventLiveData = DatabaseHandler.getEventLiveData(eid);
                eventLiveData.observe(this, event -> {
                    if (event != null) {
                        this.event = event;
                        float balance = Stats.getEventBalance(event);
                        header_val.setText(new DecimalFormat("0.00").format(balance) + "€");
                        if (balance < 0) {
                            header_val.setTextColor(Color
                                    .parseColor("#ef4545"));    // red
                        } else {
                            header_val.setTextColor(Color
                                    .parseColor("#2ba050"));    // green
                        }
                        userValueList = new ArrayList<>();
                        cash_check_map = event.getBalanceTable(App.CurrentUser.getUid());
                        buildCashView();
                    }
                });
            } else if (uid != null) {
                EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(uid);
                listLiveData.observe(this, eventList -> {
                    if (eventList != null) {
                        this.eventList = eventList;
                        TextView header_text = rootView.findViewById(R.id.dialog_cash_check_header_text);
                        header_text.setText(getString(R.string.total_balance));
                        float balance = Stats.getBalance(eventList);
                        header_val.setText(new DecimalFormat("0.00").format(balance) + "€");
                        if (balance < 0)
                            header_val.setTextColor(Color.parseColor("#ef4545"));    // red
                        else
                            header_val.setTextColor(Color.parseColor("#2ba050"));    // green
                        userValueList = new ArrayList<>();
                        cash_check_map = Stats.getGlobalBalanceTable(App.CurrentUser, eventList);
                        buildCashView();
                    }
                });
            }
        }
        return rootView;
    }

    private class UserValue {
        private String uid, name, email;
        private float value;

        UserValue(String uid, String name, String email, float value) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.value = value;
        }
    }

    private class UserValueArrayAdapter extends ArrayAdapter<UserValue> {
        private Context mContext;
        private List<UserValue> usersValueList;
        private final int MAX_NAME_LENGTH = 15;

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

            ImageView arrow_imageView = listItem.findViewById(R.id.dialog_cash_check_arrow);
            LinearLayout value_layout = listItem.findViewById(R.id.dialog_cash_check_value_layout);
            TextView userValueName = listItem.findViewById(R.id.dialog_cash_check_name);
            TextView userValueName2 = listItem.findViewById(R.id.dialog_cash_check_name2);

            if (currentUserValue.value < 0) { // App.CurrentUser owes money
                Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_forward_red_24dp);
                arrow_imageView.setImageDrawable(arrow);
                userValueName.setText(R.string.yourself);
                if (currentUserValue.name.length() > MAX_NAME_LENGTH) {
                    userValueName2.setText(currentUserValue.name.substring(0, MAX_NAME_LENGTH)
                            + "...");
                } else {
                    userValueName2.setText(currentUserValue.name);
                }
                value_layout.setOnClickListener(v -> {
                    // pay ALL debts to user here (multiple positions)
                    float totalDebt = currentUserValue.value * (-1);
                    String amountAsString = new DecimalFormat("0.00").format(totalDebt);
                    Intent payIntent = new Intent(getContext(), PayActivity.class);
                    payIntent.putExtra("amount", amountAsString);
                    startActivity(payIntent);

                    // TODO: After successful payment -> add user to has paid list in ALL positions he just payed for
                });
                userValueName.setOnClickListener(v -> {
                    DatabaseHandler.queryUser(App.CurrentUser.getUid(), user -> {
                        new ProfileInfoDialog(user, mContext);
                    });
                });
                userValueName2.setOnClickListener(v -> {
                    DatabaseHandler.queryUser(currentUserValue.uid, user -> {
                        new ProfileInfoDialog(user, mContext);
                    });
                });
            } else {    // App.CurrentUser gets money
                Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_forward_green_24dp);
                arrow_imageView.setImageDrawable(arrow);
                userValueName2.setText(R.string.you);
                if (currentUserValue.name.length() > MAX_NAME_LENGTH) {
                    userValueName.setText(currentUserValue.name.substring(0, MAX_NAME_LENGTH)
                            + "...");
                } else {
                    userValueName.setText(currentUserValue.name);
                }
                value_layout.setOnClickListener(v -> {
                    String[] email_address = {currentUserValue.email};
                    String email_subject = getString(R.string.reminder);
                    String eventListString = "";
                    if (event != null) {
                        email_subject += " " + getString(R.string.tab_expenses) + " " + event.getName();
                        eventListString = event.getName() + "\n";
                        event = null;
                    } else if (eventList != null) {
                        eventList = Stats.getOpenEvents(App.CurrentUser.getUid(), currentUserValue.uid, eventList);
                        email_subject += " " + getString(R.string.tab_events);
                        for (Event e : eventList) {
                            eventListString += e.getName() + "\n";
                        }
                    }
                    String email_body = getString(R.string.reminder_mail_body,
                            currentUserValue.name,                // debtor name
                            eventListString,                                  // event list
                            new DecimalFormat("0.00")
                                    .format(currentUserValue.value),          // dept value
                            App.CurrentUser.getFirstName()
                                    + " " + App.CurrentUser.getLastName());   // creditor name

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, email_address);
                    intent.putExtra(Intent.EXTRA_SUBJECT, email_subject);
                    intent.putExtra(Intent.EXTRA_TEXT, email_body);
                    try {
                        startActivity(Intent.createChooser(intent, getString(R.string.send_reminder_mail,
                                currentUserValue.name)));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "There are no email clients installed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                userValueName.setOnClickListener(v -> {
                    DatabaseHandler.queryUser(currentUserValue.uid, user -> {
                        new ProfileInfoDialog(user, mContext);
                    });
                });
                userValueName2.setOnClickListener(v -> {
                    DatabaseHandler.queryUser(App.CurrentUser.getUid(), user -> {
                        new ProfileInfoDialog(user, mContext);
                    });
                });
            }

            TextView userValueBalance = listItem.findViewById(R.id.dialog_cash_check_balance);

            userValueBalance.setText(new DecimalFormat("0.00€").format(currentUserValue.value));

            return listItem;
        }
    }

    Comparator<UserValue> DEBT_SORT = (userValue1, userValue2) -> {
        if (userValue1.value < userValue2.value) {
            return -1;
        } else {
            return 1;
        }
    };

    private void buildCashView() {
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
                            userValueList.set(idx2, new UserValue(
                                    user.getUid(),
                                    user.getNickname(),
                                    user.getEmail(),
                                    cash_check_map.get(key)));
                        } else {
                            userValueList.set(idx2, new UserValue(
                                    getString(R.string.unknown),
                                    getString(R.string.unknown),
                                    getString(R.string.unknown),
                                    cash_check_map.get(key)));
                        }
                        break;
                    }
                }
                if (!userValueList.contains(null)) {
                    // create and set adapter
                    Collections.sort(userValueList, DEBT_SORT);
                    userValueArrayAdapter = new UserValueArrayAdapter(Objects.requireNonNull(getContext()), userValueList);
                    cash_check_list.setAdapter(userValueArrayAdapter);
                }
            });
        }
    }
}

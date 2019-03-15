package de.thm.ap.groupexpenses.view.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.services.NotificationService;
import de.thm.ap.groupexpenses.view.activity.PayActivity;
import de.thm.ap.groupexpenses.view.dialog.ProfileInfoDialog;

import static de.thm.ap.groupexpenses.view.fragment.PositionEventListFragment.USER_ID;

public class CashFragment extends Fragment {
    public static final String SELECTED_EID = "seid";
    private Map<String, Float> cash_check_map;
    private ArrayList<UserValue> userValueList;
    private UserValueArrayAdapter userValueArrayAdapter;
    private ListView cash_check_list;
    private Event event;
    private List<Event> eventList;
    private final int PAY_REQUEST_CODE = 49824;


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
            String uid = args.getString(USER_ID);
            if (eid != null) {
                EventLiveData eventLiveData;
                eventLiveData = DatabaseHandler.getEventLiveData(eid);
                eventLiveData.observe(this, event -> {
                    if (event != null) {
                        this.event = event;
                        float balance = Stats.getEventBalance(event);
                        String header_val_text = new DecimalFormat("0.00").format(balance) + "€";
                        header_val.setText(header_val_text);
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
                DatabaseHandler.queryUser(uid, result -> {
                    App.CurrentUser = result;
                    EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(uid);
                    listLiveData.observe(this, eventList -> {
                        if (eventList != null) {
                            this.eventList = eventList;
                            TextView header_text = rootView.findViewById(R.id.dialog_cash_check_header_text);
                            header_text.setText(getString(R.string.total_balance));
                            float balance = Stats.getBalance(eventList);
                            String header_val_text = new DecimalFormat("0.00").format(balance) + "€";
                            header_val.setText(header_val_text);
                            if (balance < 0)
                                header_val.setTextColor(Color.parseColor("#ef4545"));    // red
                            else
                                header_val.setTextColor(Color.parseColor("#2ba050"));    // green
                            userValueList = new ArrayList<>();
                            cash_check_map = Stats.getGlobalBalanceTable(App.CurrentUser, eventList);
                            buildCashView();
                        }
                    });
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
                userValueName.setText(R.string.you);
                if (currentUserValue.name.length() > MAX_NAME_LENGTH) {
                    String userValueName2Text = currentUserValue.name.substring(0, MAX_NAME_LENGTH) + "...";
                    userValueName2.setText(userValueName2Text);
                } else {
                    userValueName2.setText(currentUserValue.name);
                }
                value_layout.setOnClickListener(v -> {
                    if (event != null) {
                        switch (event.getLifecycleState()) {
                            case UPCOMING:
                            case CLOSED:
                            case LIVE:
                            case ERROR:
                                // not possible to pay right now
                                Toast.makeText(getContext(), getString(R.string.error_wrong_time_for_payment),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case LOCKED:
                                // possible to pay in LOCKED state!
                                // now send reminder mail or do cash transaction
                                fulfillPaymentConfirmDialog(currentUserValue);
                                break;
                            default:
                        }
                    } else if (eventList != null) {
                        boolean allEventsAreLocked = true;
                        for (Event e : Stats.getOpenEvents(currentUserValue.uid, App.CurrentUser.getUid(), eventList)) {
                            if (e.getLifecycleState() != Event.LifecycleState.LOCKED) {
                                allEventsAreLocked = false;
                                break;
                            }
                        }
                        if (allEventsAreLocked) {
                            // possible to pay, since ALL events are in LOCKED state!
                            // now send reminder mail or do cash transaction
                            fulfillPaymentConfirmDialog(currentUserValue);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.error_not_all_events_locked),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
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
                userValueName2.setText(R.string.yourself);
                if (currentUserValue.name.length() > MAX_NAME_LENGTH) {
                    userValueName.setText(currentUserValue.name.substring(0, MAX_NAME_LENGTH)
                            + "...");
                } else {
                    userValueName.setText(currentUserValue.name);
                }
                value_layout.setOnClickListener(v -> {
                    if (event != null) {
                        switch (event.getLifecycleState()) {
                            case UPCOMING:
                            case CLOSED:
                            case LIVE:
                            case ERROR:
                                // not possible to pay right now
                                Toast.makeText(getContext(), getString(R.string.error_wrong_time_for_payment),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case LOCKED:
                                // possible to pay in LOCKED state!
                                // reminder mail or do cash transaction
                                showCashOrReminderDialog(currentUserValue);
                                break;
                            default:
                        }
                    } else if (eventList != null) {
                        boolean allEventsAreLocked = true;
                        for (Event e : Stats.getOpenEvents(App.CurrentUser.getUid(), currentUserValue.uid, eventList)) {
                            if (e.getLifecycleState() != Event.LifecycleState.LOCKED) {
                                allEventsAreLocked = false;
                                break;
                            }
                        }
                        if (allEventsAreLocked) {
                            // possible to pay in LOCKED state!
                            // reminder mail or do cash transaction
                            showCashOrReminderDialog(currentUserValue);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.error_not_all_events_locked),
                                    Toast.LENGTH_LONG).show();
                        }
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
        } else if (userValue1.value > userValue2.value) {
            return 1;
        } else return 0;
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // payment successful
                String debtor_uid = data.getStringExtra("debtor_uid");
                if (event != null) {   // user is paying for one event
                    for (Position p : event.getPositions()) {
                        releaseAllDebtsBetweenUsers(p, App.CurrentUser.getUid(), debtor_uid);
                    }
                    // tell the NotificationService that we did the payment
                    NotificationService.isCaller = true;
                    DatabaseHandler.updateEvent(event);
                    Toast.makeText(getContext(), getString(R.string.done_paypal_payment), Toast.LENGTH_SHORT).show();
                } else if (eventList != null) {    // user is paying for all events
                    for (Event e : Stats.getOpenEvents(App.CurrentUser.getUid(), debtor_uid, eventList)) {
                        for (Position p : e.getPositions()) {
                            releaseAllDebtsBetweenUsers(p, App.CurrentUser.getUid(), debtor_uid);
                        }
                        // tell the NotificationService that we did the payment
                        NotificationService.isCaller = true;
                        DatabaseHandler.updateEvent(e);
                    }
                    Toast.makeText(getContext(), getString(R.string.done_paypal_payment), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // payment NOT successful
                Toast.makeText(getContext(), getString(R.string.error_paypal_payment), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void buildCashView() {
        if (cash_check_map.isEmpty()) {
            // create and set adapter
            if (userValueArrayAdapter == null) {
                Collections.sort(userValueList, DEBT_SORT);
                userValueArrayAdapter = new UserValueArrayAdapter(Objects.requireNonNull(getContext()), userValueList);
                cash_check_list.setAdapter(userValueArrayAdapter);
            } else {
                userValueArrayAdapter.clear();
                userValueArrayAdapter.addAll(userValueList);
                userValueArrayAdapter.notifyDataSetChanged();

            }
        } else {
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
                        if (userValueArrayAdapter == null) {
                            Collections.sort(userValueList, DEBT_SORT);
                            userValueArrayAdapter = new UserValueArrayAdapter(Objects.requireNonNull(getContext()), userValueList);
                            cash_check_list.setAdapter(userValueArrayAdapter);
                        } else {
                            userValueArrayAdapter.clear();
                            userValueArrayAdapter.addAll(userValueList);
                            userValueArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }

    }

    private void showCashOrReminderDialog(UserValue currentUserValue) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.dialog_choose_2_options, null);
        final android.app.AlertDialog confirmDialogBuilder = new android.app.AlertDialog.Builder(getContext()).create();
        Button cash_pay_btn = promptView.findViewById(R.id.dialog_chose_2_options_option1_btn);
        Button remind_btn = promptView.findViewById(R.id.dialog_chose_2_options_option2_btn);

        remind_btn.setOnClickListener(v -> {
            // remind user of payment per mail
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

        cash_pay_btn.setOnClickListener(v -> {
            // user is paying per cash
            TextView confirm_text = promptView.findViewById(R.id.dialog_chose_2_options_text);
            confirm_text.setText(getString(R.string.confirm_cash_payment, currentUserValue.name,
                    new DecimalFormat("0.00").format(currentUserValue.value)));
            confirm_text.setVisibility(View.VISIBLE);

            cash_pay_btn.setText(getString(R.string.confirm));
            remind_btn.setText(getString(R.string.cancel));

            cash_pay_btn.setOnClickListener(v2 -> {
                if (event != null) {   // user is paying for one event
                    for (Position p : event.getPositions()) {
                        releaseAllDebtsBetweenUsers(p, App.CurrentUser.getUid(), currentUserValue.uid);
                    }
                    DatabaseHandler.updateEvent(event);
                    Toast.makeText(getContext(), getString(R.string.done_cash_payment), Toast.LENGTH_SHORT).show();
                } else if (eventList != null) {    // user is paying for all events
                    for (Event e : Stats.getOpenEvents(App.CurrentUser.getUid(), currentUserValue.uid, eventList)) {
                        for (Position p : e.getPositions()) {
                            releaseAllDebtsBetweenUsers(p, App.CurrentUser.getUid(), currentUserValue.uid);
                        }
                        DatabaseHandler.updateEvent(e);
                    }
                    Toast.makeText(getContext(), getString(R.string.done_cash_payment), Toast.LENGTH_SHORT).show();
                }
                confirmDialogBuilder.dismiss();
            });

            remind_btn.setOnClickListener(v2 -> {
                confirmDialogBuilder.dismiss();
            });
        });

        confirmDialogBuilder.setView(promptView);
        confirmDialogBuilder.show();
    }

    private void fulfillPaymentConfirmDialog(UserValue currentUserValue) {
        // pay ALL debts to user here (multiple positions)
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.dialog_choose_2_options, null);
        final android.app.AlertDialog confirmDialogBuilder = new android.app.AlertDialog.Builder(getContext()).create();
        Button confirm_pay_btn = promptView.findViewById(R.id.dialog_chose_2_options_option1_btn);
        Button cancel_pay_btn = promptView.findViewById(R.id.dialog_chose_2_options_option2_btn);
        TextView confirm_text = promptView.findViewById(R.id.dialog_chose_2_options_text);
        confirm_pay_btn.setText(getString(R.string.confirm));
        cancel_pay_btn.setText(getString(R.string.cancel));

        float amount = currentUserValue.value * (-1);
        String amountAsString = new DecimalFormat("0.00").format(amount);

        confirm_text.setText(getString(R.string.fulfill_payment_confirm_msg,
                currentUserValue.name, amountAsString));
        confirm_text.setVisibility(View.VISIBLE);

        confirm_pay_btn.setOnClickListener(v -> {
            // fulfill payment
            Intent payIntent = new Intent(getContext(), PayActivity.class);
            payIntent.putExtra("amount", amountAsString);
            payIntent.putExtra("debtor_uid", currentUserValue.uid);
            startActivityForResult(payIntent, PAY_REQUEST_CODE);
            confirmDialogBuilder.dismiss();
        });

        cancel_pay_btn.setOnClickListener(v -> {
            // cancel payment
            confirmDialogBuilder.dismiss();
        });

        confirmDialogBuilder.setView(promptView);
        confirmDialogBuilder.show();
    }

    /*
    releases all debts for one position between two users
     */
    private void releaseAllDebtsBetweenUsers(Position p, String user_one, String user_two) {
        if (p.getCreatorId().equals(user_one)) {
            if (!p.isExcludedFromPayments(user_two)) {
                p.removeDebtor(user_two);
            }
        } else if (p.getCreatorId().equals(user_two)) {
            if (!p.isExcludedFromPayments(user_one)) {
                p.removeDebtor(user_one);
            }
        }
    }
}

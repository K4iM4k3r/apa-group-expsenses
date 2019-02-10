package de.thm.ap.groupexpenses.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Stats;

public class CashCheckDialog {
    Map<String, Float> cash_check_map;
    ArrayList<UserValue> userValueList;
    private UserValueArrayAdapter userValueArrayAdapter;
    private AlertDialog.Builder cashCheckDialog;
    private AlertDialog dialog;
    private View view;
    private TextView event_name;
    private ListView cash_check_list;
    private Context context;

    @SuppressLint("ClickableViewAccessibility")
    public CashCheckDialog(Context context, Event event) {
        this.context = context;
        cashCheckDialog = new AlertDialog.Builder(context);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_cash_check, null);
        event_name = view.findViewById(R.id.dialog_cash_check_name);
        cash_check_list = view.findViewById(R.id.dialog_cash_check_list);

        event_name.setText(event.getName());

        // close btn clicked
        event_name.setOnTouchListener(new RightDrawableOnTouchListener(event_name) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                dialog.dismiss();
                return true;
            }
        });

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
                            userValueList.set(idx2, new UserValue(context.getString(R.string.unknown),
                                    cash_check_map.get(key)));
                        }
                        break;
                    }
                }

                if (!userValueList.contains(null)) {
                    // create and set adapter
                    userValueArrayAdapter = new UserValueArrayAdapter(context, userValueList);
                    cash_check_list.setAdapter(userValueArrayAdapter);
                    // create dialog
                    cashCheckDialog.setView(view);
                    dialog = cashCheckDialog.create();
                    dialog.show();
                }
            });
        }
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
                });
            }


            TextView userValueName = listItem.findViewById(R.id.dialog_cash_check_name);
            TextView userValueBalance = listItem.findViewById(R.id.dialog_cash_check_balance);

            userValueName.setText(currentUserValue.name);
            userValueBalance.setText(new DecimalFormat("0.00").format(currentUserValue.value)
                    + " €");

            return listItem;
        }
    }
}
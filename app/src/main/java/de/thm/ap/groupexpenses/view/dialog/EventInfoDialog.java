package de.thm.ap.groupexpenses.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Stats;

public class EventInfoDialog {
    private AlertDialog.Builder eventDialog;
    private AlertDialog dialog;
    private Event event;
    private View view;
    private TextView eventName, eventInfo, eventDepts;
    private TextView dept_val;
    private String creatorNickname;
    private Context context;

    public EventInfoDialog(Event object, String creatorNickname, Context context) {
        this.context = context;
        eventDialog = new AlertDialog.Builder(context);
        event = object;
        this.creatorNickname = creatorNickname;
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_event_view, null);
        dept_val = view.findViewById(R.id.event_dialog_dept_val);
        createDialog();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void createDialog() {
        eventName = view.findViewById(R.id.event_dialog_name);
        eventDepts = view.findViewById(R.id.event_dialog_your_depts);
        TextView positionCreatorAndDate = view.findViewById(R.id.event_dialog_creator_and_date);
        eventInfo = view.findViewById(R.id.event_dialog_info);

        eventName.setText(event.getName());

        String creator;

        if (creatorNickname == null) creator = context.getString(R.string.you);
        else creator = creatorNickname;

        String creatorAndDate = context.getResources().getString(R.string.creator_and_date_event, creator, event.getDate());
        Spannable creatorAndDateDefaultVal = new SpannableString(creatorAndDate);
        creatorAndDateDefaultVal.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                13, 13 + creator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        positionCreatorAndDate.setText(creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE);

        float balance = Stats.getEventBalance(event);
        if (balance >= 0) {
            eventDepts.setText(R.string.your_dept_claim_event);
            dept_val.setTextColor(Color.parseColor("#2ba050")); // green
        }
        dept_val.setText(new DecimalFormat("0.00")
                .format(balance) + " " + context.getString(R.string.euro));
        String eventInfoString = event.getInfo();
        if (!eventInfoString.isEmpty()) eventInfo.setText(eventInfoString);

        // close btn clicked
        eventName.setOnTouchListener(new RightDrawableOnTouchListener(eventName) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                dialog.dismiss();
                return true;
            }
        });
        eventDialog.setView(view);
        dialog = eventDialog.create();
        dialog.show();
    }
}
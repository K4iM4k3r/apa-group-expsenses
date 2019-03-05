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

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;

import static de.thm.ap.groupexpenses.App.getDateFromLong;

public class EventInfoDialog {
    private AlertDialog.Builder eventDialog;
    private AlertDialog dialog;
    private Event event;
    private View view;
    private String creatorNickname, creatorUid;
    private Context context;

    public EventInfoDialog(Event object, String creatorNickname, String creatorUid, Context context) {
        this.context = context;
        eventDialog = new AlertDialog.Builder(context);
        event = object;
        this.creatorNickname = creatorNickname;
        this.creatorUid = creatorUid;
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_event_view, null);

        createDialog();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void createDialog() {
        TextView eventName = view.findViewById(R.id.event_dialog_name);
        TextView positionCreatorAndDate = view.findViewById(R.id.event_dialog_creator_and_date);
        TextView eventInfo = view.findViewById(R.id.event_dialog_info);
        TextView your_expenses = view.findViewById(R.id.event_dialog_expenses_you_val);
        TextView expenses_ratio = view.findViewById(R.id.event_dialog_expenses_participation);

        eventName.setText(event.getName());

        String creator;

        if (creatorNickname == null) creator = context.getString(R.string.you);
        else creator = creatorNickname;

        String creatorAndDate = context.getResources().getString(R.string.creator_and_date_event, creator, getDateFromLong(event.getDate_begin()));
        Spannable creatorAndDateDefaultVal = new SpannableString(creatorAndDate);
        creatorAndDateDefaultVal.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                13, 13 + creator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        positionCreatorAndDate.setText(creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE);
        positionCreatorAndDate.setOnClickListener(v -> {
            if (creatorUid != null) {
                DatabaseHandler.queryUser(creatorUid, user -> {
                    new ProfileInfoDialog(user, context);
                });
            }
        });
        float my_expenses = 0;
        float expenses_others = 0;
        for (Position p : event.getPositions()) {
            if (p.getCreatorId().equals(App.CurrentUser.getUid())) {
                my_expenses += p.getValue();
            } else {
                expenses_others += p.getValue();
            }
        }
        float ratio = (my_expenses / (expenses_others + my_expenses)) * 100;
        String event_expenses_ratio = context.getString(R.string.event_expenses_ratio,
                new DecimalFormat("0").format(ratio));
        expenses_ratio.setText(event_expenses_ratio);
        your_expenses.setText(new DecimalFormat("0.00").format(my_expenses)+"€");
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
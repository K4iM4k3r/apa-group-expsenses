package de.thm.ap.groupexpenses.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    TextView your_expenses, expenses_ratio, your_expenses_text, eventInfo, positionCreatorAndDate;
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
        positionCreatorAndDate = view.findViewById(R.id.event_dialog_creator_and_date);
        eventInfo = view.findViewById(R.id.event_dialog_info);
        your_expenses_text = view.findViewById(R.id.event_dialog_expenses_you_text);
        your_expenses = view.findViewById(R.id.event_dialog_expenses_you_val);
        expenses_ratio = view.findViewById(R.id.event_dialog_expenses_participation);

        eventName.setText(event.getName());

        String creator;

        if (creatorNickname == null) {  // user is creator of event
            creator = context.getString(R.string.yourself);
            eventInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_edit_grey_24dp, 0);
            eventInfo.setCompoundDrawablePadding(-20);

            // info edit btn clicked
            eventInfo.setOnTouchListener(new RightDrawableOnTouchListener(eventInfo) {
                @Override
                public boolean onDrawableTouch(final MotionEvent motionEvent) {
                    infoEditBtnClicked(motionEvent);
                    return false;
                }
            });
        } else creator = creatorNickname;

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
        your_expenses.setText(new DecimalFormat("0.00").format(my_expenses) + "€");
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

    private void infoEditBtnClicked(MotionEvent motionEvent) {
        EditText quickEditField = view.findViewById(R.id.event_dialog_quick_edit_field);
        Button saveBtn = view.findViewById(R.id.event_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.event_dialog_cancel_btn);
        TextView eventInfo = view.findViewById(R.id.event_dialog_info);

        // make visible
        quickEditField.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);

        // make gone
        your_expenses.setVisibility(View.GONE);
        expenses_ratio.setVisibility(View.GONE);
        eventInfo.setVisibility(View.GONE);
        positionCreatorAndDate.setVisibility(View.GONE);
        view.findViewById(R.id.event_dialog_line1).setVisibility(View.GONE);
        view.findViewById(R.id.event_dialog_line2).setVisibility(View.GONE);

        your_expenses_text.setText(context.getString(R.string.event_inspect_event_info));

        quickEditField.setSingleLine(false);
        quickEditField.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        quickEditField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        quickEditField.setVerticalScrollBarEnabled(true);
        quickEditField.setMovementMethod(ScrollingMovementMethod.getInstance());
        quickEditField.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        quickEditField.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        quickEditField.setText(event.getInfo());
        quickEditField.setOnKeyListener((v, keyCode, event1) -> {
            // if enter is pressed start calculating
            if (keyCode == KeyEvent.KEYCODE_ENTER && event1.getAction() == KeyEvent.ACTION_UP) {
                // get EditText text
                String text = ((EditText) v).getText().toString();
                // find how many rows it contains
                int editTextRowCount = text.split("\\n").length;
                // user has input more than limited - lets do something about that
                if (editTextRowCount >= 12) {
                    // find the last break
                    int lastBreakIndex = text.lastIndexOf("\n");
                    // compose new text
                    String newText = text.substring(0, lastBreakIndex);
                    // add new text - delete old one and append new one
                    // (append because I want the cursor to be at the end)
                    ((EditText) v).setText("");
                    ((EditText) v).append(newText);
                }
            }
            return false;
        });

        saveBtn.setOnClickListener(v -> {
            String input = quickEditField.getText().toString();
            if (!input.isEmpty()) {
                event.setInfo(input);
                eventInfo.setText(input);
            }
            DatabaseHandler.updateEvent(event);
            resetBackToNormal(quickEditField, saveBtn, cancelBtn);
        });
        cancelBtn.setOnClickListener(v -> resetBackToNormal(quickEditField, saveBtn, cancelBtn));
        motionEvent.setAction(MotionEvent.ACTION_CANCEL);
    }

    private void resetBackToNormal(EditText quickEditField, Button saveBtn, Button cancelBtn) {
        // make gone
        saveBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        quickEditField.setVisibility(View.GONE);

        // make visible
        your_expenses.setVisibility(View.VISIBLE);
        expenses_ratio.setVisibility(View.VISIBLE);
        eventInfo.setVisibility(View.VISIBLE);
        positionCreatorAndDate.setVisibility(View.VISIBLE);
        view.findViewById(R.id.event_dialog_line1).setVisibility(View.VISIBLE);
        view.findViewById(R.id.event_dialog_line2).setVisibility(View.VISIBLE);

        your_expenses_text.setText(context.getString(R.string.event_expenses_you));
    }

}
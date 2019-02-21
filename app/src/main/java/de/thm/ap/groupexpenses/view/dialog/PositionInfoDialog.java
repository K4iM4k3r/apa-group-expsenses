package de.thm.ap.groupexpenses.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.fragment.UserListDialogFragment;

public class PositionInfoDialog {
    private AlertDialog.Builder positionDialog;
    private AlertDialog dialog;
    private Position position;
    private Event selectedEvent;
    private View view;
    private TextView positionInfo;
    private Spannable creatorAndDateDefaultVal;
    private Button valueEditBtn, payBtn;
    private TextView dept_val, positionDepts;
    private AtomicBoolean clickable;
    private TextView positionCreatorAndDate;
    private String creatorNickname;
    private Context context;
    private float pay_value;

    public PositionInfoDialog(Position selectedPosition, Event selectedEvent, String creatorNickname, Context context) {
        this.context = context;
        this.creatorNickname = creatorNickname;
        this.selectedEvent = selectedEvent;
        positionDialog = new AlertDialog.Builder(context);
        position = selectedPosition;
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_position_view, null);
        payBtn = view.findViewById(R.id.position_dialog_pay_btn);
        valueEditBtn = view.findViewById(R.id.position_dialog_edit_btn);
        dept_val = view.findViewById(R.id.position_dialog_dept_val);
        clickable = new AtomicBoolean(true);
        createDialog();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void createDialog() {
        TextView positionName = view.findViewById(R.id.position_dialog_name);
        positionDepts = view.findViewById(R.id.position_dialog_your_depts);
        positionCreatorAndDate = view.findViewById(R.id.position_dialog_creator_and_date);
        positionInfo = view.findViewById(R.id.position_dialog_info);

        String positionDeptValue;
        String creator;

        if (creatorNickname == null) {
            // user is creator
            creator = context.getString(R.string.you);
            positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_edit_grey_24dp, 0);
            positionInfo.setCompoundDrawablePadding(-20);
            positionDeptValue = context.getResources().getString(R.string.your_dept_claim);
            dept_val.setTextColor(Color.parseColor("#2ba050"));  //green
            payBtn.setText(context.getString(R.string.add_payment));
            valueEditBtn.setVisibility(View.VISIBLE);
            valueEditBtn.setOnClickListener(v -> {
                // value edit btn clicked
                positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        0, 0);
                clickable.set(false);
                onValueEditBtnClick();
            });
            payBtn.setOnClickListener(v2 -> {
                // release dept btn clicked
                positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        0, 0);
                clickable.set(false);
                onPayBtnClick();
            });

            // info edit btn clicked
            positionInfo.setOnTouchListener(new RightDrawableOnTouchListener(positionInfo) {
                @Override
                public boolean onDrawableTouch(final MotionEvent event) {
                    return clickable.get() && infoEditBtnClicked(event);
                }
            });
        } else {
            creator = creatorNickname;
            positionDeptValue = context.getResources().getString(R.string.your_depts);
            payBtn.setOnClickListener(v -> {
                // pay position dept to user here (just one position)
                // TODO: David pay system
                float val = pay_value * (-1);
            });
        }
        displayCreator(creator);
        // close btn clicked
        positionName.setOnTouchListener(new RightDrawableOnTouchListener(positionName) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                dialog.dismiss();
                return clickable.get();
            }
        });
        positionName.setText(position.getTopic());
        positionDepts.setText(positionDeptValue);
        pay_value = Stats.getPositionBalance(position, selectedEvent);
        dept_val.setText(new DecimalFormat("0.00")
                .format(pay_value) + " " + context.getString(R.string.euro));
        String positionInfoString = position.getInfo();
        if (positionInfoString != null && !positionInfoString.isEmpty())
            positionInfo.setText(positionInfoString);
        positionDialog.setView(view);
        dialog = positionDialog.create();
        dialog.show();
    }

    private void displayCreator(String creator) {
        String date = position.getDateString();
        String creatorAndDate = context.getResources().getString(R.string.creator_and_date_position, creator, date);
        creatorAndDateDefaultVal = new SpannableString(creatorAndDate);
        creatorAndDateDefaultVal.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                13, 13 + creator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        positionCreatorAndDate.setText(creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE);
    }

    @SuppressLint("SetTextI18n")
    private void onValueEditBtnClick() {
        EditText quickEditField = view.findViewById(R.id.position_dialog_quick_edit_field);
        Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        valueEditBtn.setVisibility(View.GONE);
        payBtn.setVisibility(View.GONE);
        dept_val.setVisibility(View.GONE);
        quickEditField.setVisibility(View.VISIBLE);
        quickEditField.setText("");  // clear input (could be polluted by info edit)
        saveBtn.setOnClickListener(v2 -> {
            // click on save
            resetBackToNormal("edit_value");

            position.setValue(Float.parseFloat(quickEditField.getText().toString()));
            dept_val.setText(new DecimalFormat("0.00")
                    .format(Stats.getPositionBalance(position, selectedEvent)) + " " + context.getString(R.string.euro));

            if (!selectedEvent.updatePosition(position)) {
                throw new IllegalAccessError("Position '" + position.getTopic() +
                        "' could not be updated in Event '" + selectedEvent.getName() + "'");
            } else
                DatabaseHandler.updateEvent(selectedEvent);
        });
        cancelBtn.setOnClickListener(v3 -> resetBackToNormal("edit_value"));
    }

    @SuppressLint("SetTextI18n")
    private void onPayBtnClick() {
        /*
        UserListLiveData userListLiveData = DatabaseHandler.getAllMembersOfEvent(selectedEvent.getEid());
        userListLiveData.observe(this, membersList -> {
            if (membersList != null) {
                UserListDialogFragment dialog = new UserListDialogFragment();
                dialog.build(membersList);
                dialog.show(((Activity) context).getFragmentManager(), "create_event");
            }
        });
        */

        DatabaseHandler.getAllMembersOfEvent(selectedEvent.getEid(), membersList -> {
            UserListDialogFragment dialog = new UserListDialogFragment();
            dialog.build(membersList);
            dialog.show(((Activity) context).getFragmentManager(), "pay_position");
        });

        /*
        DatabaseHandler.getAllMembersOfEvent(auth.getCurrentUser().getUid());
            List<User> friendsList = result;
            UserListDialogFragment dialog = new UserListDialogFragment();
            dialog.build(eventUsersList, friendsList);
            dialog.show(getFragmentManager(), "create_event");
        });
        UserListDialogFragment dialog = new UserListDialogFragment();
        dialog.build(selectedEvent);
        dialog.show(((Activity) context).getFragmentManager(), "pay_position");

        Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
        ColorStateList oldColors = positionDepts.getTextColors();
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        valueEditBtn.setVisibility(View.GONE);
        payBtn.setVisibility(View.GONE);
        dept_val.setTextColor(Color.parseColor("#ef4545"));  //red
        positionDepts.setTextColor(Color.parseColor("#ef4545"));
        positionDepts.setText(context.getString(R.string.position_inspect_release_dept_claim_ask).toUpperCase());
        positionDepts.setTypeface(Typeface.DEFAULT_BOLD);
        saveBtn.setText(context.getString(R.string.confirm));
        saveBtn.setOnClickListener(v -> {
            // delete position and close dialog
            if (!selectedEvent.deletePosition(position)) {
                throw new IllegalAccessError("Position '" + position.getTopic() +
                        "' could not be deleted from Event '" + selectedEvent.getName() + "'");
            } else {
                DatabaseHandler.updateEvent(selectedEvent);
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(v2 -> {
            resetBackToNormal("release_dept");
            positionDepts.setTextColor(oldColors);
        });
        */
    }

    @SuppressLint("SetTextI18n")
    private boolean infoEditBtnClicked(MotionEvent event) {
        EditText quickEditField = view.findViewById(R.id.position_dialog_quick_edit_field);
        Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
        positionDepts = view.findViewById(R.id.position_dialog_your_depts);
        TextView positionCreatorAndDate = view.findViewById(R.id.position_dialog_creator_and_date);
        TextView positionInfo = view.findViewById(R.id.position_dialog_info);
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        valueEditBtn.setVisibility(View.GONE);
        payBtn.setVisibility(View.GONE);
        quickEditField.setVisibility(View.VISIBLE);
        positionDepts.setVisibility(View.GONE);
        dept_val.setVisibility(View.GONE);
        positionInfo.setVisibility(View.GONE);
        view.findViewById(R.id.position_dialog_line1).setVisibility(View.GONE);
        view.findViewById(R.id.position_dialog_line2).setVisibility(View.GONE);
        positionCreatorAndDate.setText(context.getString(R.string.position_inspect_position_info) + ":");

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) quickEditField.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int appMargin = (int) (context.getResources().getDimension(R.dimen.app_margin)
                / context.getResources().getDisplayMetrics().density);
        lp.rightMargin = appMargin;
        lp.leftMargin = appMargin;
        quickEditField.setSingleLine(false);
        quickEditField.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        quickEditField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        quickEditField.setVerticalScrollBarEnabled(true);
        quickEditField.setMovementMethod(ScrollingMovementMethod.getInstance());
        quickEditField.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        quickEditField.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        quickEditField.setText(position.getInfo());
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
                position.setInfo(input);
                positionInfo.setText(input);
            }
            if (!selectedEvent.updatePosition(position)) {
                throw new IllegalAccessError("Position '" + position.getTopic() +
                        "' could not be updated in Event '" + selectedEvent.getName() + "'");
            } else
                DatabaseHandler.updateEvent(selectedEvent);

            resetBackToNormal("edit_info");
        });

        cancelBtn.setOnClickListener(v -> resetBackToNormal("edit_info"));

        event.setAction(MotionEvent.ACTION_CANCEL);
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void resetBackToNormal(String type) {
        EditText quickEditField = view.findViewById(R.id.position_dialog_quick_edit_field);
        Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
        TextView positionInfo = view.findViewById(R.id.position_dialog_info);
        saveBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        valueEditBtn.setVisibility(View.VISIBLE);
        payBtn.setVisibility(View.VISIBLE);
        positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.ic_edit_grey_24dp, 0);
        positionInfo.setCompoundDrawablePadding(-20);
        clickable.set(true);

        switch (type) {
            case "edit_info":
                positionDepts.setVisibility(View.VISIBLE);
                dept_val.setVisibility(View.VISIBLE);
                positionInfo.setVisibility(View.VISIBLE);
                view.findViewById(R.id.position_dialog_line1).setVisibility(View.VISIBLE);
                view.findViewById(R.id.position_dialog_line2).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.position_dialog_creator_and_date)).setText(
                        creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE
                );
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) quickEditField.getLayoutParams();
                lp.width = 120 / (int) context.getResources().getDisplayMetrics().density;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                int appMargin = (int) (context.getResources().getDimension(R.dimen.app_margin)
                        / context.getResources().getDisplayMetrics().density);
                lp.rightMargin = appMargin;
                lp.leftMargin = appMargin;
                quickEditField.setSingleLine(true);
                quickEditField.setInputType(InputType.TYPE_CLASS_NUMBER);
                quickEditField.setVerticalScrollBarEnabled(false);
                quickEditField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                quickEditField.setVisibility(View.GONE);
                break;
            case "release_dept":
                positionDepts.setText(context.getResources().getString(R.string.your_dept_claim) + ":");
                dept_val.setTextColor(Color.parseColor("#2ba050"));  //green
                break;
            case "edit_value":
                quickEditField.setVisibility(View.GONE);
                dept_val.setVisibility(View.VISIBLE);
                break;
        }
    }
}
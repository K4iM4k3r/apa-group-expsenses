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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;

public class PositionInfoDialog {
    private AlertDialog.Builder positionDialog;
    private AlertDialog dialog;
    private Position position;
    private Event selectedEvent;
    private View view;
    private TextView positionInfo;
    ImageView delete_position_img;
    private Spannable creatorAndDateDefaultVal;
    private TextView debt_val, positionDepts;
    private AtomicBoolean clickable;
    private TextView positionCreatorAndDate;
    private String creatorNickname, creatorUid;
    private Context context;

    public PositionInfoDialog(Position selectedPosition, Event selectedEvent, String creatorNickname,
                              String creatorUid, Context context) {
        this.context = context;
        this.creatorNickname = creatorNickname;
        this.creatorUid = creatorUid;
        this.selectedEvent = selectedEvent;
        positionDialog = new AlertDialog.Builder(context);
        position = selectedPosition;

        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_position_view, null);

        debt_val = view.findViewById(R.id.position_dialog_dept_val);
        clickable = new AtomicBoolean(true);
        createDialog();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void createDialog() {
        delete_position_img = view.findViewById(R.id.position_dialog_delete_img);
        TextView positionName = view.findViewById(R.id.position_dialog_name);
        positionDepts = view.findViewById(R.id.position_dialog_your_depts);
        positionCreatorAndDate = view.findViewById(R.id.position_dialog_creator_and_date);
        positionInfo = view.findViewById(R.id.position_dialog_info);

        String positionDeptValue;
        String creator;

        if (creatorNickname == null) { // user is creator of position
            creator = context.getString(R.string.you);
            delete_position_img.setVisibility(View.VISIBLE);
            debt_val.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_edit_grey_24dp, 0);
            debt_val.setCompoundDrawablePadding(-20);
            positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_edit_grey_24dp, 0);
            positionInfo.setCompoundDrawablePadding(-20);
            positionDeptValue = context.getResources().getString(R.string.your_dept_claim);
            debt_val.setTextColor(Color.parseColor("#2ba050"));  //green

            debt_val.setOnTouchListener(new RightDrawableOnTouchListener(positionInfo) {
                @Override
                public boolean onDrawableTouch(final MotionEvent event) {
                    positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            0, 0);
                    onValueEditBtnClick();
                    return clickable.get();
                }
            });

            // info edit btn clicked
            positionInfo.setOnTouchListener(new RightDrawableOnTouchListener(positionInfo) {
                @Override
                public boolean onDrawableTouch(final MotionEvent event) {
                    return clickable.get() && infoEditBtnClicked(event);
                }
            });

            // delete position icon clicked
            delete_position_img.setOnClickListener(v -> {
                showDeleteConfirmDialog();
            });
        } else {    // user is not creator of position
            creator = creatorNickname;
            positionDeptValue = context.getResources().getString(R.string.your_depts);
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
        float pay_value = Stats.getPositionBalance(position, selectedEvent);
        debt_val.setText(new DecimalFormat("0.00")
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
        positionCreatorAndDate.setOnClickListener(v -> {
            if (creatorUid != null) {
                DatabaseHandler.queryUser(creatorUid, user -> new ProfileInfoDialog(user, context));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void onValueEditBtnClick() {
        EditText quickEditField = view.findViewById(R.id.position_dialog_quick_edit_field);
        Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        debt_val.setVisibility(View.GONE);
        delete_position_img.setVisibility(View.GONE);
        quickEditField.setVisibility(View.VISIBLE);
        quickEditField.setText("");  // clear input (could be polluted by info edit)
        saveBtn.setOnClickListener(v2 -> {
            // click on save
            resetBackToNormal("edit_value");

            position.setValue(Float.parseFloat(quickEditField.getText().toString()));
            debt_val.setText(new DecimalFormat("0.00")
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
    private boolean infoEditBtnClicked(MotionEvent event) {
        EditText quickEditField = view.findViewById(R.id.position_dialog_quick_edit_field);
        Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
        Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
        positionDepts = view.findViewById(R.id.position_dialog_your_depts);
        TextView positionCreatorAndDate = view.findViewById(R.id.position_dialog_creator_and_date);
        TextView positionInfo = view.findViewById(R.id.position_dialog_info);
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        quickEditField.setVisibility(View.VISIBLE);
        positionDepts.setVisibility(View.GONE);
        debt_val.setVisibility(View.GONE);
        delete_position_img.setVisibility(View.GONE);
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
        delete_position_img.setVisibility(View.VISIBLE);
        positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.ic_edit_grey_24dp, 0);
        positionInfo.setCompoundDrawablePadding(-20);
        clickable.set(true);

        switch (type) {
            case "edit_info":
                positionDepts.setVisibility(View.VISIBLE);
                debt_val.setVisibility(View.VISIBLE);
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
                debt_val.setTextColor(Color.parseColor("#2ba050"));  //green
                break;
            case "edit_value":
                quickEditField.setVisibility(View.GONE);
                debt_val.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showDeleteConfirmDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_choose_2_options, null);
        final android.app.AlertDialog confirmDialogBuilder = new android.app.AlertDialog.Builder(context).create();
        TextView dialog_message_text = promptView.findViewById(R.id.dialog_chose_2_options_text);
        Button confirm_btn = promptView.findViewById(R.id.dialog_chose_2_options_option1_btn);
        Button cancel_btn = promptView.findViewById(R.id.dialog_chose_2_options_option2_btn);
        dialog_message_text.setVisibility(View.VISIBLE);
        dialog_message_text.setText(context.getString(R.string.confirm_delete_position));
        confirm_btn.setText(context.getString(R.string.confirm));
        cancel_btn.setText(context.getString(R.string.cancel));

        confirm_btn.setOnClickListener(v -> {
            selectedEvent.deletePosition(position);
            DatabaseHandler.updateEvent(selectedEvent);
            Toast.makeText(context, context.getString(R.string.done_delete_position), Toast.LENGTH_SHORT).show();
            confirmDialogBuilder.dismiss();
            dialog.dismiss();
        });

        cancel_btn.setOnClickListener(v -> {
            confirmDialogBuilder.dismiss();
        });

        confirmDialogBuilder.setView(promptView);
        confirmDialogBuilder.show();
    }
}
package de.thm.ap.groupexpenses.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import de.thm.ap.groupexpenses.fragment.ObjectListFragment;
import de.thm.ap.groupexpenses.fragment.UserListFragmentDialog;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;

public class PositionActivity extends BaseActivity implements ObjectListFragment.ItemClickListener{

    private Event selectedEvent;
    private ArrayList<User> selectedEventUserList;
    private List<Object> positionList;
    private ObjectListFragment objectListFragment;
    private static final int POSITION_CREATE_SUCCESS = 11215;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        getSupportActionBar().setTitle(R.string.position_inspect_positions);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                // cancel Activity
                finish();
            } else {
                selectedEvent = (Event) extras.getSerializable("event");
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("event");
        }

        if(selectedEvent != null){
            selectedEventUserList = new ArrayList<>();
            List<String> eventMemberUids = selectedEvent.getMembers();
            for(int idx = 0; idx < eventMemberUids.size(); ++idx){
                DatabaseHandler.queryUser(eventMemberUids.get(idx), result -> {
                    selectedEventUserList.add(result);
                });

            }
            positionList = (List<Object>)(List<?>) selectedEvent.getPositions();
            objectListFragment = (ObjectListFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.position_fragment);
            // IMPORTANT: add event at the end of list to be used and subsequently deleted
            // in ObjectListFragment
            if(!positionList.isEmpty())
                positionList.add(selectedEvent);
            objectListFragment.createFragmentObjects(positionList, "Position");
        } else
            finish();

        FloatingActionButton createPositionBtn = findViewById(R.id.create_position_btn);
        createPositionBtn.setOnClickListener(v ->
                startActivityForResult(new Intent(PositionActivity.this,
                PositionFormActivity.class), POSITION_CREATE_SUCCESS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.position_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.position_menu_inspect_users:
                UserListFragmentDialog dialog = UserListFragmentDialog
                        .newInstance(selectedEventUserList, selectedEvent.getCreatorId());
                dialog.show(getFragmentManager(), "edit_event");
                break;

            case R.id.position_menu_info:
                // display event info including a cash check btn
                new EventInfoDialog(selectedEvent);
                break;

            case R.id.position_menu_done:
                // return Event to EventActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("inspectedEvent", selectedEvent);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode) {
                case POSITION_CREATE_SUCCESS:
                    Position position  = (Position) data.getExtras().getSerializable("createdPosition");
                    positionList.add(position);
                    positionList.add(selectedEvent);
                    objectListFragment.updateFragmentObjects(positionList, "Position");
                    break;
            }
        }
    }

    @Override
    public void onFragmentObjectClick(Object object) {
        // show a custom alert dialog with position information
        new PositionAlertDialog((Position)object);
    }

   private class PositionAlertDialog {
       private AlertDialog.Builder positionDialog;
       private AlertDialog dialog;
       private Position position;
       private View view;
       private TextView positionInfo;
       private Spannable creatorAndDateDefaultVal;
       private Button valueEditBtn, payBtn;
       private TextView dept_val, positionDepts;
       private boolean position_edited, position_deleted;
       private AtomicBoolean clickable;

       PositionAlertDialog(Position object){
           positionDialog = new AlertDialog.Builder(PositionActivity.this);
           position = object;
           view = getLayoutInflater().inflate(R.layout.dialog_position_view, null);
           payBtn = view.findViewById(R.id.position_dialog_pay_btn);
           valueEditBtn = view.findViewById(R.id.position_dialog_edit_btn);
           dept_val = view.findViewById(R.id.position_dialog_dept_val);
           clickable = new AtomicBoolean(true);
           createDialog();
       }

       @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
       private void createDialog(){
           TextView positionName = view.findViewById(R.id.position_dialog_name);
           positionDepts = view.findViewById(R.id.position_dialog_your_depts);
           TextView positionCreatorAndDate = view.findViewById(R.id.position_dialog_creator_and_date);
           positionInfo = view.findViewById(R.id.position_dialog_info);

           String creator;
           String positionDeptValue;

           if(App.CurrentUser.getUid().equals(position.getCreatorId())){
               // user is creator
               creator = getString(R.string.you);
               positionInfo.setCompoundDrawablesWithIntrinsicBounds(0,0,
                       R.drawable.ic_edit_grey_24dp, 0);
               positionDeptValue = getResources().getString(R.string.your_dept_claim);
               dept_val.setTextColor(Color.parseColor("#2ba050"));  //green
               payBtn.setText(getString(R.string.position_inspect_release_dept_claim));
               valueEditBtn.setVisibility(View.VISIBLE);
               valueEditBtn.setOnClickListener(v -> {
                   // value edit btn clicked
                   positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                           0,0);
                   clickable.set(false);
                   valueEditBtnClicked();
               });
               payBtn.setOnClickListener(v2 -> {
                   // release dept btn clicked
                   positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                           0,0);
                   clickable.set(false);
                   releaseDeptBtnClicked();
               });

               // info edit btn clicked
               positionInfo.setOnTouchListener(new RightDrawableOnTouchListener(positionInfo) {
                   @Override
                   public boolean onDrawableTouch(final MotionEvent event) {
                       return clickable.get() && infoEditBtnClicked(event);
                   }
               });


           } else {
               creator = position.getCreatorId();
               positionDeptValue = getResources().getString(R.string.your_depts);
           }
           positionName.setText(position.getTopic());
           String creatorAndDate = getResources().getString(R.string.creator_and_date_position, creator, position.getDate());
           creatorAndDateDefaultVal = new SpannableString(creatorAndDate);
           creatorAndDateDefaultVal.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                   13, 13 + creator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
           positionCreatorAndDate.setText(creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE);
           positionDepts.setText(positionDeptValue);
           dept_val.setText(new DecimalFormat("0.00")
                   .format(Stats.getPositionBalance(position, selectedEvent))+ " " + getString(R.string.euro));
           String positionInfoString = position.getInfo();
           if(!positionInfoString.isEmpty()) positionInfo.setText(positionInfoString);
           positionDialog.setView(view);
           dialog = positionDialog.create();
           dialog.show();
           dialog.setOnDismissListener(dialog1 -> {
               if(position_edited){
                   for(int idx = 0; idx < positionList.size(); ++idx){
                       if(position.getPid() == ((Position)positionList.get(idx)).getPid()){
                           positionList.set(idx, position);
                           positionList.add(selectedEvent);
                           objectListFragment.updateFragmentObjects(positionList, "Position");
                           break;
                       }
                   }
                   position_edited = false;
               }
               if(position_deleted)
                   objectListFragment.updateFragmentObjects(positionList, "Removal");
           });
       }

       @SuppressLint("SetTextI18n")
       private void valueEditBtnClicked(){
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
           saveBtn.setOnClickListener(v2->{
               // click on save
               resetBackToNormal("edit_value");

               position.setValue(Float.parseFloat(quickEditField.getText().toString()));
               dept_val.setText(new DecimalFormat("0.00")
                       .format(Stats.getPositionBalance(position, selectedEvent))+ " " + getString(R.string.euro));
               position_edited = true;
           });
           cancelBtn.setOnClickListener(v3 -> resetBackToNormal("edit_value"));
       }
       @SuppressLint("SetTextI18n")
       private void releaseDeptBtnClicked() {
           Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
           Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
           ColorStateList oldColors =  positionDepts.getTextColors();
           saveBtn.setVisibility(View.VISIBLE);
           cancelBtn.setVisibility(View.VISIBLE);
           valueEditBtn.setVisibility(View.GONE);
           payBtn.setVisibility(View.GONE);
           dept_val.setTextColor(Color.parseColor("#ef4545"));  //red
           positionDepts.setTextColor(Color.parseColor("#ef4545"));
           positionDepts.setText(getString(R.string.position_inspect_release_dept_claim_ask).toUpperCase());
           positionDepts.setTypeface(Typeface.DEFAULT_BOLD);
           saveBtn.setText(getString(R.string.confirm));
           saveBtn.setOnClickListener(v -> {
               // delete position and close dialog
               boolean positionFound = false;
               for(int idx = 0; idx < positionList.size(); ++idx){
                   if(((Position)positionList.get(idx)).getPid() == position.getPid()){
                       positionList.remove(idx);
                       positionFound = true;
                       position_deleted = true;
                       break;
                   }
               }
               if(!positionFound) throw new IllegalAccessError("Position " + position.toString()
                       +  "not found, cannot be removed!");
               else
                   dialog.dismiss();
           });

           cancelBtn.setOnClickListener(v2 -> {
               resetBackToNormal("release_dept");
               positionDepts.setTextColor(oldColors);
           });
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
           positionCreatorAndDate.setText(getString(R.string.position_inspect_position_info) + ":");

           LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)quickEditField.getLayoutParams();
           lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
           lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
           int appMargin = (int) (getResources().getDimension(R.dimen.app_margin)
                   / getResources().getDisplayMetrics().density);
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
               if(!input.isEmpty()){
                   position.setInfo(input);
                   positionInfo.setText(input);
               }
               resetBackToNormal("edit_info");
           });

           cancelBtn.setOnClickListener(v -> resetBackToNormal("edit_info"));

           event.setAction(MotionEvent.ACTION_CANCEL);
           return false;
       }

       @SuppressLint("SetTextI18n")
       private void resetBackToNormal(String type){
           EditText quickEditField = view.findViewById(R.id.position_dialog_quick_edit_field);
           Button saveBtn = view.findViewById(R.id.position_dialog_save_btn);
           Button cancelBtn = view.findViewById(R.id.position_dialog_cancel_btn);
           TextView positionInfo = view.findViewById(R.id.position_dialog_info);
           saveBtn.setVisibility(View.GONE);
           cancelBtn.setVisibility(View.GONE);
           valueEditBtn.setVisibility(View.VISIBLE);
           payBtn.setVisibility(View.VISIBLE);
           positionInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                   R.drawable.ic_edit_grey_24dp,0);
           clickable.set(true);

           switch(type){
               case "edit_info":
                   positionDepts.setVisibility(View.VISIBLE);
                   dept_val.setVisibility(View.VISIBLE);
                   positionInfo.setVisibility(View.VISIBLE);
                   view.findViewById(R.id.position_dialog_line1).setVisibility(View.VISIBLE);
                   view.findViewById(R.id.position_dialog_line2).setVisibility(View.VISIBLE);
                   ((TextView)view.findViewById(R.id.position_dialog_creator_and_date)).setText(
                           creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE
                   );
                   LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)quickEditField.getLayoutParams();
                   lp.width = 120 / (int)getResources().getDisplayMetrics().density;
                   lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                   int appMargin = (int) (getResources().getDimension(R.dimen.app_margin)
                           / getResources().getDisplayMetrics().density);
                   lp.rightMargin = appMargin;
                   lp.leftMargin = appMargin;
                   quickEditField.setSingleLine(true);
                   quickEditField.setInputType(InputType.TYPE_CLASS_NUMBER);
                   quickEditField.setVerticalScrollBarEnabled(false);
                   quickEditField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                   quickEditField.setVisibility(View.GONE);
                   break;
               case "release_dept":
                   positionDepts.setText(getResources().getString(R.string.your_dept_claim) + ":");
                   dept_val.setTextColor(Color.parseColor("#2ba050"));  //green
                   break;
               case "edit_value":
                   quickEditField.setVisibility(View.GONE);
                   dept_val.setVisibility(View.VISIBLE);
                   break;
           }
       }

       private abstract class RightDrawableOnTouchListener implements View.OnTouchListener {
           private Drawable drawable;
           private final int FUZZ = 10;

           RightDrawableOnTouchListener(TextView view) {
               super();
               final Drawable[] drawables = view.getCompoundDrawables();
               if (drawables.length == 4)
                   this.drawable = drawables[2];
           }
           @Override
           public boolean onTouch(final View v, final MotionEvent event) {
               if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
                   final int x = (int) event.getX();
                   final int y = (int) event.getY();
                   final Rect bounds = drawable.getBounds();
                   if (x >= (v.getRight() - bounds.width() - FUZZ) && x <= (v.getRight() - v.getPaddingRight() + FUZZ)
                           && y >= (v.getPaddingTop() - FUZZ) && y <= (v.getHeight() - v.getPaddingBottom()) + FUZZ) {
                       return onDrawableTouch(event);
                   }
               }
               return false;
           }
           public abstract boolean onDrawableTouch(final MotionEvent event);

       }
   }

    private class EventInfoDialog {
        private AlertDialog.Builder eventDialog;
        private AlertDialog dialog;
        private Event event;
        private View view;
        private TextView eventInfo, eventDepts;
        private Button cash_check_btn;
        private TextView dept_val;

        EventInfoDialog(Event object){
            eventDialog = new AlertDialog.Builder(PositionActivity.this);
            event = object;
            view = getLayoutInflater().inflate(R.layout.dialog_event_view, null);
            cash_check_btn = view.findViewById(R.id.event_dialog_cach_check_btn);
            dept_val = view.findViewById(R.id.event_dialog_dept_val);
            createDialog();
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        private void createDialog(){
            ((TextView)view.findViewById(R.id.event_dialog_name)).setText(event.getName());
            eventDepts = view.findViewById(R.id.event_dialog_your_depts);
            TextView positionCreatorAndDate = view.findViewById(R.id.event_dialog_creator_and_date);
            eventInfo = view.findViewById(R.id.event_dialog_info);

            String creator;

            if(App.CurrentUser.getUid().equals(event.getCreatorId())){
                // user is creator
                creator = getString(R.string.you);
            } else {
                creator = event.getCreatorId();
            }
            String creatorAndDate = getResources().getString(R.string.creator_and_date_event, creator, event.getDate());
            Spannable creatorAndDateDefaultVal = new SpannableString(creatorAndDate);
            creatorAndDateDefaultVal.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                    13, 13 + creator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            positionCreatorAndDate.setText(creatorAndDateDefaultVal, TextView.BufferType.SPANNABLE);

            float balance = Stats.getEventBalance(event);
            if(balance >= 0){
                eventDepts.setText(R.string.your_dept_claim_event);
                dept_val.setTextColor(Color.parseColor("#2ba050")); // green
            }
            dept_val.setText(new DecimalFormat("0.00")
                    .format(balance)+ " " + getString(R.string.euro));
            String eventInfoString = event.getInfo();
            if(!eventInfoString.isEmpty()) eventInfo.setText(eventInfoString);
            cash_check_btn.setOnClickListener(v -> {
                // do cash check here
            });
            eventDialog.setView(view);
            dialog = eventDialog.create();
            dialog.show();
            dialog.setOnDismissListener(dialog1 -> {
                // nothing
            });
        }
    }
}

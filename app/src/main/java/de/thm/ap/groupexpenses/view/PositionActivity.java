package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.fragment.ObjectListFragment;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;

public class PositionActivity extends BaseActivity implements ObjectListFragment.ItemClickListener{

    private Event selectedEvent;
    private List<Object> positionList;
    private ObjectListFragment objectListFragment;
    private static final int POSITION_CREATE_SUCCESS = 11215;
    private static final int POSITION_EDIT_SUCCESS = 42562;

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
            case R.id.position_menu_done:
                // return Event to EventActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("inspectedEvent", selectedEvent);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;

            case R.id.position_menu_info:
                // display event info, date and members

                AlertDialog.Builder builder = new AlertDialog.Builder( PositionActivity.this);
                builder.setTitle(R.string.info);
                builder.setMessage(
                        Html.fromHtml(selectedEvent.getInfo() + "<br><br>" + "<b>"
                                + getString(R.string.event_form_begin) + ": </b><br>" + selectedEvent.getDate()
                                + "<br><br><b>"
                                + getString(R.string.event_form_users) + "("
                                + selectedEvent.getMembers().size() + "): " + "</b><br>" +
                        App.listToHTMLString(selectedEvent.getMembers()))
                );
                builder.show();
                break;
        }

        if (id == R.id.position_menu_done) {

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
       private Position position;
       private View view;
       private Button valueEditBtn, payBtn;
       private TextView dept_val;
       private boolean changes_made;

       PositionAlertDialog(Position object){
           positionDialog = new AlertDialog.Builder(PositionActivity.this);
           position = object;
           view = getLayoutInflater().inflate(R.layout.dialog_position_view, null);
           payBtn = view.findViewById(R.id.position_dialog_pay_btn);
           valueEditBtn = view.findViewById(R.id.position_dialog_edit_btn);
           dept_val = view.findViewById(R.id.position_dialog_dept_val);
           changes_made = false;
           createDialog();
       }

       private void createDialog(){
           TextView positionName = view.findViewById(R.id.position_dialog_name);
           TextView positionDepts = view.findViewById(R.id.position_dialog_your_depts);
           TextView positionCreatorAndDate = view.findViewById(R.id.position_dialog_creator_and_date);
           TextView positionInfo = view.findViewById(R.id.position_dialog_info);

           String creator;
           String positionDeptValue;

           if(App.CurrentUser.getId() == position.getCreator().getId()){
               // user is creator
               creator = getString(R.string.you);
               positionDeptValue = getResources().getString(R.string.your_dept_claim) + ":";
               dept_val.setTextColor(Color.parseColor("#2ba050"));
               payBtn.setText(getString(R.string.position_inspect_release_dept_claim));
               valueEditBtn.setVisibility(View.VISIBLE);
               valueEditBtn.setOnClickListener(v -> {
                   valueEditBtnClicked();
               });
           } else {
               creator = position.getCreator().toString();
               positionDeptValue = getResources().getString(R.string.your_depts) + ":";
           }
           positionName.setText(position.getTopic());
           String creatorAndDate = getResources().getString(R.string.creator_and_date, creator, position.getDate());
           Spannable spannable = new SpannableString(creatorAndDate);
           spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                   13, 13 + creator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
           positionCreatorAndDate.setText(spannable, TextView.BufferType.SPANNABLE);
           positionDepts.setText(positionDeptValue);
           dept_val.setText(new DecimalFormat("0.00")
                   .format(Stats.getPositionBalance(position, selectedEvent))+ " " + getString(R.string.euro));
           String positionInfoString = position.getInfo();
           if(!positionInfoString.isEmpty()) positionInfo.setText(positionInfoString);
           positionDialog.setView(view);
           AlertDialog dialog = positionDialog.create();
           dialog.show();
           dialog.setOnDismissListener(dialog1 -> {
               if(changes_made){
                   for(int idx = 0; idx < positionList.size(); ++idx){
                       if(position.getId() == ((Position)positionList.get(idx)).getId()){
                           positionList.set(idx, position);
                           positionList.add(selectedEvent);
                           objectListFragment.updateFragmentObjects(positionList, "Position");
                           break;
                       }
                   }
               }
           });
       }

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
           saveBtn.setOnClickListener(v2->{
               // click on save
               saveBtn.setVisibility(View.GONE);
               cancelBtn.setVisibility(View.GONE);
               quickEditField.setVisibility(View.GONE);
               dept_val.setVisibility(View.VISIBLE);
               valueEditBtn.setVisibility(View.VISIBLE);
               payBtn.setVisibility(View.VISIBLE);

               position.setValue(Float.parseFloat(quickEditField.getText().toString()));
               dept_val.setText(new DecimalFormat("0.00")
                       .format(Stats.getPositionBalance(position, selectedEvent))+ " " + getString(R.string.euro));
               changes_made = true;
           });
           cancelBtn.setOnClickListener(v3 ->{
               // click on cancel
               saveBtn.setVisibility(View.GONE);
               cancelBtn.setVisibility(View.GONE);
               quickEditField.setVisibility(View.GONE);
               dept_val.setVisibility(View.VISIBLE);
               valueEditBtn.setVisibility(View.VISIBLE);
               payBtn.setVisibility(View.VISIBLE);
           });
       }
   }
}

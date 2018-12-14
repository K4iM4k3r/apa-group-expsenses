package de.thm.ap.groupexpenses.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;

public class PositionActivity extends AppCompatActivity {

    private Event selectedEvent;
    private List<Position> positions;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        getSupportActionBar().setTitle(R.string.position_inspect_positions);

        FloatingActionButton fab = findViewById(R.id.create_position_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ListView positionList = findViewById(R.id.position_list);
        headerView = getLayoutInflater().inflate(R.layout.position_list_header, null);
        positionList.addHeaderView(headerView);

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

        if(selectedEvent != null)
            positions = selectedEvent.getPositions();
        else
            finish();

        PositionArrayAdapter positionAdapter = new PositionArrayAdapter(this, positions);
        positionList.setAdapter(positionAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!positions.isEmpty()){
            findViewById(R.id.no_positions).setVisibility(View.GONE);
            // calculate position balance here
            float position_balance = 0;
            for(int idx = 0; idx < positions.size(); ++idx)
                position_balance += positions.get(idx).getValue();

            ((TextView)headerView.findViewById(R.id.position_balance_summary_val))
                    .setText(new DecimalFormat("0.00").format(position_balance) + " " + getString(R.string.euro));
        }
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
                // check for created positions


                finish();
                break;

            case R.id.position_menu_info:
                // display event info, date and members

                AlertDialog.Builder builder = new AlertDialog.Builder( PositionActivity.this);
                builder.setTitle(R.string.event_form_info);
                builder.setMessage(
                        Html.fromHtml(selectedEvent.getInfo() + "<br><br>" + "<b>"
                                + getString(R.string.event_form_begin) + ": </b><br>" + selectedEvent.getDate()
                                + "<br><br><b>"
                                + getString(R.string.event_form_users) + "("
                                + selectedEvent.getUsers().size() + "): " + "</b><br>" +
                        App.listToHTMLString(selectedEvent.getUsers()))
                );
                builder.show();
                break;
        }

        if (id == R.id.position_menu_done) {

        }
        return super.onOptionsItemSelected(item);
    }



    private class PositionArrayAdapter extends ArrayAdapter<Position> {
        private Context mContext;

        public PositionArrayAdapter(@NonNull Context context, List<Position> list) {
            super(context, 0, list);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;

            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.position_list_item, parent,
                        false);

            Position p = getItem(position);

            TextView name = listItem.findViewById(R.id.position_name);
            name.setText(p.getTopic());

            TextView balance =  listItem.findViewById(R.id.position_balance);
            balance.setText(new DecimalFormat("0.00").format(p.getValue()) + "€");

            return listItem;
        }

    }

}

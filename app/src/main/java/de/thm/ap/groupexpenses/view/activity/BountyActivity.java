package de.thm.ap.groupexpenses.view.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;

public class BountyActivity extends BaseActivity {

    private static final String TAG = BaseActivity.class.getName();

    private Event event;

    ListView bountyListView;
    ArrayAdapter<String> bountyAdapter;

    Map<String, Float> currentBounty;
    List<String> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounty);

        setTitle("Kassensturz");
        data = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        if (b==null) throw new IllegalStateException("Called bounty activity without event id");
        if (App.CurrentUser == null) throw new IllegalStateException("Appuser not specified!");

        bountyListView = findViewById(R.id.bounty_list);
        bountyListView.setEmptyView(findViewById(R.id.bounty_list_empty));

        bountyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, data);
        bountyListView.setAdapter(bountyAdapter);


        DatabaseHandler.queryEvent(b.getString("eventId"), event -> {
            if (event==null) return; // illegalstate
            showProgressDialog();
            Toast.makeText(this, App.CurrentUser.getNickname(), Toast.LENGTH_LONG).show();
            currentBounty = event.getBalanceTable(App.CurrentUser.getUid());
            currentBounty.forEach((k,v)-> {
                DatabaseHandler.queryUser(k, user -> {
                    showProgressDialog();
                    if (user == null) {
                        Toast.makeText(this, "Failed to retrieve data for user " + k, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String username = user.getNickname()==null?user.getUid():user.getNickname();
                    float negativeValue = -v;
                    String text = (v<0?"You owe "+username+" "+negativeValue+"€":"You get "+v.toString()+"€ from "+username);

                    data.add(text);
                    bountyAdapter.notifyDataSetChanged();
                    hideProgressDialog();
                });
            });
            hideProgressDialog();
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }
}

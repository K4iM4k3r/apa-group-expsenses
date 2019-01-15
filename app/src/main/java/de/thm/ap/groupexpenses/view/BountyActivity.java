package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class BountyActivity extends AppCompatActivity {

    //region testdata

    Position[] testPositions = new Position[]{
            new Position(App.TestValues.USERS[1].getUid(), "Bier", 90f),
            new Position(App.TestValues.USERS[2].getUid(), "Sprit", 120f),
            new Position(App.TestValues.USERS[3].getUid(), "Essen", 15f),
            new Position(App.TestValues.USERS[3].getUid(), "Kuchen", 30f),
            new Position(App.TestValues.USER.getUid(), "App", 300f)
    };

    Event testEvent = new Event(App.TestValues.USER.getUid(), "Festival", "11.01.2015", "",
            Arrays.stream(App.TestValues.USERS).map(User::getUid).collect(Collectors.toList()), Arrays.asList(testPositions));

    // endregion

    ListView bountyListView;
    ArrayAdapter<String> bountyAdapter;

    Map<String, Float> currentBounty;
    List<String> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounty);

        setTitle("Bounty");
        data = new ArrayList<>();

        bountyListView = findViewById(R.id.bounty_list);
        bountyListView.setEmptyView(findViewById(R.id.bounty_list_empty));

        currentBounty = getBounty(App.TestValues.USER.getUid(), testEvent);
        currentBounty.forEach((k,v)->data.add(k+": "+v.toString()));

        bountyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, data);
        bountyListView.setAdapter(bountyAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        bountyAdapter.notifyDataSetChanged();
    }

    private Map<String, Float> getBounty(String userId, Event event){
        return event.getBalanceTable(userId);
    }
}

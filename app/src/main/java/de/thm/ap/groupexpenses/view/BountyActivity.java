package de.thm.ap.groupexpenses.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
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
    ArrayAdapter<User> bountyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounty);

        setTitle("Bounty");

        bountyListView = findViewById(R.id.bounty_list);
        bountyListView.setEmptyView(findViewById(R.id.bounty_list_empty));


//        bountyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, data);
//        bountyListView.setAdapter(bountyAdapter);

        calculateData();

        int break1 = 0;
    }

    private void calculateData(){
        Map<String, Float> bounty = getBounty(App.TestValues.USER, testEvent);
    }

    private Map<String, Float> getBounty(User currentAppUser, Event e){
        Map<String, Float> bounty = new HashMap<>();
        float credit = 0f;

        for(Position pos : e.getPositions()){
            if (pos.getCreatorId().equals(currentAppUser.getUid())){
                credit += pos.getFactorizedValue(1f/e.getMembers().size());
                continue;
            }
            String creator = pos.getCreatorId();
            Float factorizedValue = Stats.getPositionBalance(App.TestValues.USER, pos, testEvent);
            bounty.merge(creator,factorizedValue, Float::sum);
        }

        float finalCredit = credit;
        testEvent.getMembers().forEach(member -> {
            if (!member.equals(App.TestValues.USER.getUid()))
                bounty.merge(member, finalCredit, Float::sum);
        });

        return bounty;
    }

    private Map<String, Float> replaceIdsWithNames(Map<String, Float> bounty){
        Map<String, Float> result = new HashMap<>();
        bounty.forEach((key, value) -> {
//            DatabaseHandler.queryUser(key, user -> {
//                result.put(user.getFirstName(), value);
//            });
        });


        return result;
    }
}

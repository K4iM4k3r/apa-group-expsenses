package de.thm.ap.groupexpenses;

import org.junit.Test;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;

import static junit.framework.TestCase.assertEquals;


public class StatsTest {

    //region variablen
    private User creator = new User("0", "Nils", "Müller", "", "nMueller@mail.de", null, null);
    private User[] member = new User[]{
            new User("1", "Jan", "Müller","", "jMueller@mail.de", null, null),
            new User("2", "Tom", "Müller","", "tMueller@mail.de", null, null),
            new User("3", "Sina", "Müller","", "sMueller@mail.de", null, null),
            new User("4", "Mia", "Müller","", "mMueller@mail.de", null, null)
    };

    private Position[] positions = new Position[]{
            new Position(member[1].getUid(), "Bier", 90f),
            new Position(member[2].getUid(), "Sprit", 120f),
            new Position(member[3].getUid(), "Essen", 15f)
    };

    private Position[] positions2 = new Position[]{
            new Position(member[1].getUid(), "Bier2", 40f),
            new Position(member[2].getUid(), "Sprit2", 60f),
            new Position(member[3].getUid(), "Essen2", 120f)
    };

    private Position creators_position =  new Position(creator.getUid(), "Tickets", 5000f);
    //endregion

    @Test
    public void getEventBalanceTest(){

        Stats stats = new Stats();
        Event event = new Event(creator.getUid(), "Festival2", "Morgen", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event.addPositions(positions);

        float balance = stats.getEventBalance(creator, event);

        assertEquals(balance, -45, 0.01);

        event.addPositions(new Position(creator.getUid(), "Planung", 60f));

        balance = stats.getEventBalance(creator, event);

        assertEquals(balance, 3, 0.01);
    }

    @Test
    public void getBalanceTest(){
        float expected_balance = 0.0f;
        float actual_balance = 0.0f;

        Stats stats = new Stats();
        List<Event> events = new ArrayList<>();

        Event event = new Event(creator.getUid(), "Festival2", "Morgen", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event.addPositions(positions);
        Event event1 = new Event(creator.getUid(), "bla", "blub", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event1.addPositions(positions2);

        events.add(event);

        expected_balance = stats.getEventBalance(creator, event);
        actual_balance = stats.getBalance(creator, events);

        assertEquals(actual_balance, expected_balance, 0.001);

        events.add(event1);

        expected_balance = stats.getEventBalance(creator, event) + stats.getEventBalance(creator, event1);
        actual_balance = stats.getBalance(creator, events);

        assertEquals(actual_balance, expected_balance);

        events.get(1).addPosition(creators_position);

        expected_balance = stats.getEventBalance(creator, event) + stats.getEventBalance(creator, event1);
        actual_balance = stats.getBalance(creator, events);

        assertEquals(actual_balance, expected_balance);

        int BREAK = 0;
    }

    @Test(expected = IllegalStateException.class)
    public void getEventBalanceErrorTest(){
        Stats stats = new Stats();
        Event event = new Event(creator.getUid(), "Festival2", "Morgen", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));

        stats.getEventBalance(event);
    }

    @Test
    public void calculateAllTest(){

        Stats stats = new Stats();
        List<Event> events = new ArrayList<>();

        Event event = new Event(creator.getUid(), "Festival2", "Morgen", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event.addPositions(positions);
        Event event1 = new Event(creator.getUid(), "bla", "blub", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event1.addPositions(positions2);

        events.add(event);
        events.add(event1);

        Map<Event, Float> result = stats.calculateAll(creator, events);

        int BREAK = 0;
    }
}
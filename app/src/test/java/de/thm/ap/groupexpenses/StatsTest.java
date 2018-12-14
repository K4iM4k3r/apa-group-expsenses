package de.thm.ap.groupexpenses;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;

import static junit.framework.TestCase.assertEquals;


public class StatsTest {

    User creator = new User(0, "Nils", "Müller", "nMueller@mail.de");
    User[] member = new User[]{
            new User(1, "Jan", "Müller", "jMueller@mail.de"),
            new User(2, "Tom", "Müller", "tMueller@mail.de"),
            new User(3, "Sina", "Müller", "sMueller@mail.de"),
            new User(4, "Mia", "Müller", "mMueller@mail.de")
    };

    Position[] positions = new Position[]{
            new Position(member[1], "Bier", 90),
            new Position(member[2], "Sprit", 120),
            new Position(member[3], "Essen", 15)
    };

    Position[] positions2 = new Position[]{
            new Position(member[1], "Bier2", 40),
            new Position(member[2], "Sprit2", 60),
            new Position(member[3], "Essen2", 120)
    };

    Position creators_position =  new Position(creator, "Tickets", 5000);


    @Test
    public void getEventBalanceTest(){

        Stats stats = new Stats();
        Event event = new Event(creator, "Festival2", "Morgen", "", Arrays.asList(member));
        event.addPositions(positions);

        float balance = stats.getEventBalance(creator, event);

        assertEquals(balance, -45, 0.01);

        event.addPositions(new Position(creator, "Planung", 60));

        balance = stats.getEventBalance(creator, event);

        assertEquals(balance, 3, 0.01);
    }

    @Test
    public void getBalanceTest(){
        float expected_balance = 0.0f;
        float actual_balance = 0.0f;

        Stats stats = new Stats();
        List<Event> events = new ArrayList<>();

        Event event = new Event(creator, "Festival2", "Morgen", "", Arrays.asList(member));
        event.addPositions(positions);
        Event event1 = new Event(creator, "bla", "blub", "", Arrays.asList(member));
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
        Event event = new Event(creator, "Festival2", "Morgen", "", Arrays.asList(member));

        stats.getEventBalance(event);
    }

    @Test
    public void calculateAllTest(){

        Stats stats = new Stats();
        List<Event> events = new ArrayList<>();

        Event event = new Event(creator, "Festival2", "Morgen", "", Arrays.asList(member));
        event.addPositions(positions);
        Event event1 = new Event(creator, "bla", "blub", "", Arrays.asList(member));
        event1.addPositions(positions2);

        events.add(event);
        events.add(event1);

        Map<Event, Float> result = stats.calculateAll(creator, events);

        int BREAK = 0;
    }
}
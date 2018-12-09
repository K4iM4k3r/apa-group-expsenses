package de.thm.ap.groupexpenses;

import org.junit.Test;

import java.util.Arrays;

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


    @Test
    public void getEventBalanceTest(){

        Stats stats = new Stats();

        Event event = new Event(creator, "Festival2", "Morgen", "", Arrays.asList(member));

        Position[] positions = new Position[]{
                new Position(member[1], "Bier", 90),
                new Position(member[2], "Sprit", 120),
                new Position(member[3], "Essen", 15)
        };
        event.addPositions(positions);

        float balance = stats.getEventBalance(creator, event);

        assertEquals(balance, -45, 0.01);

        event.addPositions(new Position(creator, "Planung", 60));

        balance = stats.getEventBalance(creator, event);

        assertEquals(balance, 3, 0.01);
    }

    @Test(expected = IllegalStateException.class)
    public void getEventBalanceErrorTest(){
        Stats stats = new Stats();
        Event event = new Event(creator, "Festival2", "Morgen", "", Arrays.asList(member));

        stats.getEventBalance(event);
    }
}
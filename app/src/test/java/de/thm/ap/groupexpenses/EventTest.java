package de.thm.ap.groupexpenses;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class EventTest {

    private Event event;
    private User creator;
    private User[] member;
    private Position[] positions;

    @Before
    public void setup(){
        member = new User[]{
                new User("Nils", "Nils", "Müller", "", "nMueller@mail.de", null, null),
                new User("Jan", "Jan", "Müller","", "jMueller@mail.de", null, null),
                new User("Tom", "Tom", "Müller","", "tMueller@mail.de", null, null),
                new User("Sina", "Sina", "Müller","", "sMueller@mail.de", null, null),
                new User("Mia", "Mia", "Müller","", "mMueller@mail.de", null, null)
        };

        creator = member[0];

        positions = new Position[]{
                new Position(member[1].getUid(), "Bier", 90f),
                new Position(member[2].getUid(), "Sprit", 120f),
                new Position(member[3].getUid(), "Essen", 15f)
        };

        event = new Event(creator.getUid(), "Festival2", "Morgen", "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event.addPositions(positions);
    }

    @Test
    public void getBalanceTableTest(){

        /*  Expected Results for default situation: - indicates debts

            member  Kosten  pP
            Nils    0       0       -> Jan:-18€, Tom:-24€, Sina:-3€
            Jan     90      18      -> Nils:+18, Tom:-6, Sina:+15€, Mia:+18€
            Tom     120     24      ->          -------
            Sina    15      3       ->          | etc |
            Mia     0       0       ->          -------

        */

        Map<String, Float> nilsBalance = event.getBalanceTable("Nils");
        Map<String, Float> jansBalance = event.getBalanceTable("Jan");
        Map<String, Float> tomsBalance = event.getBalanceTable("Tom");
        Map<String, Float> sinasBalance = event.getBalanceTable("Sina");
        Map<String, Float> miasBalance = event.getBalanceTable("Mia");


        int brea = 0;

    }
}

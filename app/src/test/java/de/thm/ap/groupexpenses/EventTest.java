package de.thm.ap.groupexpenses;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;

public class EventTest {

    private Event event;
    private User creator;
    private User[] member;
    private Position[] positions;

    private long date_now;

    private long date_begin;
    private long date_end;
    private long date_deadlineDay;


    @Before
    public void setup(){

        date_now = Calendar.getInstance().getTimeInMillis();

        date_begin = 1557439200000L; //10.05.2019
        date_end = 1557698400000L; //13.05.2019
        date_deadlineDay = 1558908000000L; //27.05.2019

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

        event = new Event(creator.getUid(), "Festival2", date_begin, date_end, date_deadlineDay, "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()));
        event.addPositions(positions);
    }

    @Ignore("Test does not assert things. Gotta look if its fine.")
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

    @Test
    public void removeAllDebtsOfTest(){

        // Nils has no credits, only debts. Those get removed, Nils has nothing.

        event.removeAllDebtsOf("Nils");
        float nilsBalance = event.getBalance("Nils");

        assertEquals(0f, nilsBalance, 0.01);


        // Sina gets released from all payments, yet still gets 9€ for her expenses (nils was removed before)

        event.removeAllDebtsOf("Sina");
        float sinasBalance = event.getBalance("Sina");

        assertEquals(9f, sinasBalance, 0.01);
    }

    @Test
    public void removeAllDebtsOfUserOwedToOtherUserTest1(){

        float nilsBalanceBefore = event.getBalance("Nils");
        Map<String, Float> nilsBalanceTableBefore = event.getBalanceTable("Nils");

        assertEquals(-45, nilsBalanceBefore, 0.01);
        assertTrue(nilsBalanceTableBefore.containsKey("Jan"));

        event.removeAllDebtsOfUserOwedToOtherUser("Nils", "Jan");

        float nilsBalanceAfter = event.getBalance("Nils");
        Map<String, Float> nilsBalanceTableAfter = event.getBalanceTable("Nils");

        assertEquals(-45, nilsBalanceBefore, 0.01);
        assertTrue(nilsBalanceTableBefore.containsKey("Jan"));

        int breaki = 0;
    }

    @Test
    public void removeAllDebtsOfUserOwedToOtherUserTest2(){

        event.addPosition(new Position("Jan", "Spielzeug", 50f));

        float nilsBalanceBefore = event.getBalance("Nils");
        Map<String, Float> nilsBalanceTableBefore = event.getBalanceTable("Nils");

        assertEquals(-55, nilsBalanceBefore, 0.01);
        assertTrue(nilsBalanceTableBefore.containsKey("Jan"));

        event.removeAllDebtsOfUserOwedToOtherUser("Nils", "Jan");

        float nilsBalanceAfter = event.getBalance("Nils");
        Map<String, Float> nilsBalanceTableAfter = event.getBalanceTable("Nils");

        assertEquals(-27, nilsBalanceAfter, 0.01);
        assertFalse(nilsBalanceTableAfter.containsKey("Jan"));

        int breaki = 0;
    }

    @Test
    public void removePositionsOfTest(){

        // Jan occurs 1 time
        assertEquals(1, event.getPositions().stream().map(Position::getCreatorId).filter(id -> id.equals("Jan")).count());

        event.removePositionsOf("Jan");

        // Jan does not occure.
        assertEquals(0, event.getPositions().stream().map(Position::getCreatorId).filter(id -> id.equals("Jan")).count());

    }

    @Test
    public void isClosableTest(){

        // Open transactions
        assertFalse(event.isClosable());

        // Settle only a few debts
        event.removeAllDebtsOf("Mia");
        event.removeAllDebtsOf("Tom");
        assertFalse(event.isClosable());

        // Settle all debts
        for(String member: event.getMembers())
            event.removeAllDebtsOf(member);
        assertTrue(event.isClosable());

        // No positions
        List<Position> emptyPositions = new ArrayList<>();
        Event event1 = new Event(creator.getUid(), "", date_begin, date_end, date_deadlineDay, "", Arrays.stream(member).map(User::getUid).collect(Collectors.toList()), emptyPositions);
        assertTrue(event1.isClosable());
    }

    @Test
    public void isEvenTest(){

        assertFalse(event.isEven("Nils"));
        assertFalse(event.isEven("Jan"));
        assertFalse(event.isEven("Tom"));
        assertFalse(event.isEven("Sina"));
        assertFalse(event.isEven("Mia"));

        event.removeAllDebtsOf("Mia");

        assertFalse(event.isEven("Nils"));
        assertFalse(event.isEven("Jan"));
        assertFalse(event.isEven("Tom"));
        assertFalse(event.isEven("Sina"));
        assertTrue(event.isEven("Mia"));

        event.removeAllDebtsOf("Nils");

        assertTrue(event.isEven("Nils"));
        assertFalse(event.isEven("Jan"));
        assertFalse(event.isEven("Tom"));
        assertFalse(event.isEven("Sina"));
        assertTrue(event.isEven("Mia"));

        event.removeAllDebtsOf("Tom");

        assertTrue(event.isEven("Nils"));
        assertFalse(event.isEven("Jan"));
        assertFalse(event.isEven("Tom")); // still false till he still gets money
        assertFalse(event.isEven("Sina"));
        assertTrue(event.isEven("Mia"));


        int bre = 0;
    }
}

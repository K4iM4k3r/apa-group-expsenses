package de.thm.ap.groupexpenses;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.thm.ap.groupexpenses.model.Position;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PositionTest {

    private Position position = new Position(1,"id1", "Bier", 30f);
    private List<String> involvedPeople;

    @Before
    public void setup(){
        involvedPeople = new ArrayList<>();
        involvedPeople.add("id1");  // the creator is always involved
        involvedPeople.add("John");
        involvedPeople.add("Tina");
        involvedPeople.add("Tom");
        involvedPeople.add("Jan");
        involvedPeople.add("Dan");
    }

    @Test
    public void positionTest(){

        assertEquals(position.getTopic(), "Bier");
        assertEquals(position.getValue(), 30f, 0.01);

        position.setTopic("Käse");

        assertEquals(position.getTopic(), "Käse");
        assertEquals(position.getTopicHistory().size(), 2);
        assertEquals(position.getValueHistory().size(), 1);

        position.setValue(50000f);

        assertEquals(position.getValue(), 50000f, 0.01);
        assertEquals(position.getValueHistory().size(), 2);

        int BREAKPOINT = 0;
    }

    @Test
    public void getDebtTest(){

        int randomUserCount = getRandomInt();
        float debt = position.getDebtOfUser("userThatIsNotExcludedFromPaymentForSure", randomUserCount);

        //value = 30, shared by 5 users = 6€ per user
        assertEquals(debt, 30f/randomUserCount, 0.01);

        debt = position.getDebtOfUser("id1", getRandomInt());

        // the user is the creator - he does not owe himself: debt=0f
        assertEquals(debt,0f, 0.01);

        position.removeDebtor("John");
        debt = position.getDebtOfUser("John", getRandomInt());

        // John has payed - therefore he has no depts in here
        assertEquals(debt, 0f, 0.01);
    }

    @Test
    public void getCreditTest(){

        float credit = position.getCredit(getRandomString(10), involvedPeople);

        // credit is 0 cause the creditor doesnt match the creator, therefore they cant have a credit
        assertEquals(credit, 0f, 0.01);

        credit = position.getCredit("id1", involvedPeople);

        //value=30, 6 ppl involved, 5 have to pay: 30/6*5 = 25
        assertEquals(credit, 25, 0.01);

        position.removeDebtor("John"); // john payed

        credit = position.getCredit("id1", involvedPeople);

        //value=30, 6 ppl involved, 4 have to pay: 30/6*4 = 20
        assertEquals(credit, 20, 0.01);
    }

    @Test
    public void isClosableTest(){

        assertFalse(position.isClosable(involvedPeople));

        position.removeDebtor("John");
        position.removeDebtor("Tina");
        position.removeDebtor("Tom");
        position.removeDebtor("Jan");

        assertFalse(position.isClosable(involvedPeople));

        position.removeDebtor("Dan");

        assertTrue(position.isClosable(involvedPeople));
    }

    @Test
    public void getBalanceTest(){

        float balance = position.getBalance("id1", involvedPeople);

        // balance equals credit since user is creator
        assertEquals(balance, position.getCredit("id1", involvedPeople), 0.01);

        balance = position.getBalance("John", involvedPeople);

        // balance equals negative debts of user
        assertEquals(balance, -position.getDebtOfUser("John", involvedPeople.size()), 0.01);

        balance = position.getBalance("NOT_INVOLVED_PERSON", involvedPeople);

        // not involved users wont have a balance
        assertEquals(balance, 0, 0.01);

    }

    //region helper
    private int getRandomInt(){
        return (int)(Math.random()*100);
    }
    private String getRandomString(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
    //endregion
}

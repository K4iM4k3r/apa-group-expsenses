package de.thm.ap.groupexpenses;

import org.junit.Test;

import java.util.Date;
import java.util.Map;

import de.thm.ap.groupexpenses.model.Position;

import static org.junit.Assert.assertEquals;

public class PositionTest {

    Position position = new Position(Constants.creator, "Bier", 30);

    @Test
    public void positionTest(){

        assertEquals(position.getCreator(), Constants.creator);
        assertEquals(position.getTopic(), "Bier");
        assertEquals(position.getValue(), 30);

        position.setTopic("Käse");

        assertEquals(position.getTopic(), "Käse");
        assertEquals(position.getTopicHistory().size(), 2);
        assertEquals(position.getValueHistory().size(), 1);

        position.setValue(50000);

        assertEquals(position.getValue(), 50000);
        assertEquals(position.getValueHistory().size(), 2);

        int BREAKPOINT = 0;
    }


}

package de.thm.ap.groupexpenses;

import org.junit.Test;

import java.util.Date;
import java.util.Map;

import de.thm.ap.groupexpenses.model.Position;

import static org.junit.Assert.assertEquals;

public class PositionTest {

    Position position = new Position(1,"id1", "Bier", 30f);

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


}

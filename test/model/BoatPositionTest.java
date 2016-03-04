package model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Antoine NOSAL
 * @author Maxime BLAISE    
 */
public class BoatPositionTest {
    
    public BoatPositionTest() {
    }

    @Test
    public void verifierTest() {
        BoatPosition bp = new BoatPosition("H",4,5);
        assertTrue(bp.verifier());
        bp = new BoatPosition("H",9,9);
        assertFalse(bp.verifier());
    }
    
}

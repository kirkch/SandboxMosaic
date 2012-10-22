package com.mosaic.lang.math;

import com.mosaic.lang.Cloner;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class QuantityTest {
    @Test
    public void isSerializable() {
        assertEquals( new Quantity(35), new Cloner().deepCopy(new Quantity(35)) );
    }
}

package com.mosaic.lang.math;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 *
 */
public class MoneyTest {
    @Test
    public void testIsGTZero() throws Exception {
        assertTrue( new Money(10).isGTZero() );
    }
}

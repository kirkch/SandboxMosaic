package com.mosaic.lang.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PercentageTest {
    @Test
    public void multiplyByMoney() {
        Percentage percentage = new Percentage(10.0);
        Money amount = new Money(10.0);

        assertEquals( new Money(1.0), percentage.multiply(amount) );
    }
}

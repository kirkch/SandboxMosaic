package com.mosaic.lang.functional;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


public class VoidFunction0Test {

    @Test
    public void givenNull_callAnd_expectLHSToBeReturnedUnwrapped() {
        VoidFunction0 a = () -> {};

        assertSame( a, a.and(null) );
    }

    @Test
    public void givenTwoFunctions_callAnd_expectBothToBeInvoked() {
        AtomicInteger aCalled = new AtomicInteger(0);
        AtomicInteger bCalled = new AtomicInteger(0);

        VoidFunction0 a = aCalled::incrementAndGet;
        VoidFunction0 b = bCalled::incrementAndGet;

        VoidFunction0 c = a.and(b);

        assertEquals( 0, aCalled.get() );
        assertEquals( 0, bCalled.get() );

        c.invoke();

        assertEquals( 1, aCalled.get() );
        assertEquals( 1, bCalled.get() );

        c.invoke();

        assertEquals( 2, aCalled.get() );
        assertEquals( 2, bCalled.get() );
    }

}
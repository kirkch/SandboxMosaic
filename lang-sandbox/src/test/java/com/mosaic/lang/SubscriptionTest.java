package com.mosaic.lang;

import org.junit.Test;

import static org.junit.Assert.*;


public class SubscriptionTest {

// and

    @Test
    public void givenNullOther_and_expectAndToReturnItself() {
        Subscription a = new Subscription();

        assertTrue( a == a.and(null) );
    }

    @Test
    public void givenActiveOther_expectCompositeSubscriptionToAlsoBeActive() {
        Subscription a = new Subscription();
        Subscription b = new Subscription();

        Subscription all = a.and(b);

        assertTrue( a.isActive() );
        assertTrue( b.isActive() );
        assertTrue( all.isActive() );
    }

    @Test
    public void givenActiveOther_cancelComposite_expectAllSubsToBecomeInactive() {
        Subscription a = new Subscription();
        Subscription b = new Subscription();

        Subscription all = a.and(b);

        all.cancel();

        assertFalse( a.isActive() );
        assertFalse( b.isActive() );
        assertFalse( all.isActive() );
    }

    @Test
    public void givenActiveOther_cancelA_expectCompositeToRemainActive() {
        Subscription a = new Subscription();
        Subscription b = new Subscription();

        Subscription all = a.and(b);

        a.cancel();

        assertFalse( a.isActive() );
        assertTrue( b.isActive() );
        assertTrue( all.isActive() );
    }

    @Test
    public void givenActiveOther_cancelB_expectCompositeToRemainActive() {
        Subscription a = new Subscription();
        Subscription b = new Subscription();

        Subscription all = a.and(b);

        b.cancel();

        assertTrue( a.isActive() );
        assertFalse( b.isActive() );
        assertTrue( all.isActive() );
    }

}
package com.mosaic.utils.concurrent;

import com.mosaic.lang.functional.Function1;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 *
 */
public class AtomicTest {

    @Test
    public void givenNull_callGet_expectNull() {
        Atomic<String> atomic = new Atomic<>();

        assertNull( atomic.get() );
    }

    @Test
    public void givenString_callGet_expectString() {
        Atomic<String> atomic = new Atomic<>("a");

        assertEquals( "a", atomic.get() );
    }

    @Test
    public void givenNull_putStringCallGet_expectString() {
        Atomic<String> atomic = new Atomic<>();

        atomic.update( new Function1<String, String>() {
            public String invoke(String arg) {
                return "a";
            }
        });

        assertEquals("a", atomic.get());
    }


    // todo benchmark
    // todo random input
    // todo concurrency test

}

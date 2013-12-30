package com.mosaic.lang.functional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class StreamTest {

    private Stream<Integer> from( final int i ) {
        return Stream.create( i, new Function0<Stream<Integer>>() {
            public Stream<Integer> invoke() {
                return from( i + 1 );
            }
        } );
    }

    @Test
    public void infiniteSeries() {
        Stream<Integer> v1 = from(0);

        assertEquals( 0, v1.head().longValue() );
        assertEquals( 1, v1.tail().head().longValue() );
        assertEquals( 2, v1.tail().tail().head().longValue() );
        assertEquals( 3, v1.tail().tail().tail().head().longValue() );
        assertEquals( 4, v1.tail().tail().tail().tail().head().longValue() );
        assertEquals( 5, v1.tail().tail().tail().tail().tail().head().longValue() );
    }

}

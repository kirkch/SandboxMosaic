package com.mosaic.utils;

import com.mosaic.lang.Factory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ArrayUtils_fill_Tests {

    @Test
    public void createZeroLengthStringArray() {
        String[] a = ArrayUtils.fill( String.class, 0, new MyStringFactory() );

        assertEquals( 0, a.length );
    }

    @Test
    public void createSingleElementLengthStringArray() {
        String[] a = ArrayUtils.fill( String.class, 1, new MyStringFactory() );

        assertEquals( 1, a.length );
        assertEquals( "s0", a[0] );
    }

    @Test
    public void createDoubleElementLengthStringArray() {
        String[] a = ArrayUtils.fill( String.class, 2, new MyStringFactory() );

        assertEquals( 2, a.length );
        assertEquals( "s0", a[0] );
        assertEquals( "s1", a[1] );
    }


    private static class MyStringFactory implements Factory<String> {
        private int i = 0;

        @Override
        public String create() {
            return "s"+(i++);
        }
    }
}

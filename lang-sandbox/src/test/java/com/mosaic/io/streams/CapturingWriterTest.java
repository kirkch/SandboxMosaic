package com.mosaic.io.streams;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class CapturingWriterTest {

    @Test
    public void writeFloat() {
        assertWriteFloat( "3.15",  3.145f,   2 );
        assertWriteFloat( "3.145", 3.145f,   3 );
        assertWriteFloat( "3.15",  3.14549f, 2 );
        assertWriteFloat( "3.14",  3.144f,   2 );
        assertWriteFloat( "3.14",  3.1449f,  2 );
        assertWriteFloat( "3.1",   3.1449f,  1 );
        assertWriteFloat( "3.145", 3.1449f,  3 );
        assertWriteFloat( "3",     3.1449f,  0 );
        assertWriteFloat( "4",     3.50f,    0 );
    }

    @Test
    public void writeDouble() {
        assertWriteDouble( "3.15", 3.145, 2 );
        assertWriteDouble( "3.145", 3.145, 3 );
        assertWriteDouble( "3.15", 3.14549, 2 );
        assertWriteDouble( "3.14", 3.144, 2 );
        assertWriteDouble( "3.14", 3.1449, 2 );
        assertWriteDouble( "3.1", 3.1449, 1 );
        assertWriteDouble( "3.145", 3.1449, 3 );
        assertWriteDouble( "3", 3.1449, 0 );
        assertWriteDouble( "4", 3.50, 0 );
    }



    private void assertWriteFloat( String expected, float v, int dp ) {
        CapturingWriter out = new CapturingWriter();

        out.writeFloat( v, dp );

        assertEquals( expected, out.audit.get( 0 ) );
    }

    private void assertWriteDouble( String expected, double v, int dp ) {
        CapturingWriter out = new CapturingWriter();

        out.writeDouble( v, dp );

        assertEquals( expected, out.audit.get(0) );
    }

}

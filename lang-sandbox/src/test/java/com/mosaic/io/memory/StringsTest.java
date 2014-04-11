package com.mosaic.io.memory;

import com.mosaic.lang.text.UTF8;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class StringsTest {

    private MemoryRegion region  = MemoryRegionImpl.createOnHeap( 1024 );
    private Strings      strings = new Strings( region );


// ALLOCATE STRING

    @Test
    public void allocateString_fetch_expectCorrectStringBack() {
        int address = strings.allocate( "foo" );

        assertEquals( "foo", strings.fetch(address).toString() );
    }


// ALLOCATE UTF8

    @Test
    public void allocateUTF8_fetch_expectCorrectStringBack() {
        int address = strings.allocate( new UTF8("foo") );

        assertEquals( "foo", strings.fetch(address).toString() );
    }

// FETCH

    @Test
    public void givenEmptyStrings_fetchInvalidAddress_expectException() {
        try {
            strings.fetch( 24 );

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "Address 0x24 is an invalid address", e.getMessage() );
        }
    }


// FREE

    @Test
    public void givenString_freeIt_expectTryingToReadItBackToNowError() {
        int address = strings.allocate( "foo" );

        strings.free( address );

        try {
            strings.fetch( address );

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "Address 0x0 is an invalid address", e.getMessage() );
        }
    }

    @Test
    public void givenString_freeItTwice_expectErrorOnTheSecondInvocationOfFree() {
        int address = strings.allocate( "foo" );

        strings.free( address );

        try {
            strings.free( address );

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "Address 0x0 is an invalid address", e.getMessage() );
        }
    }

// WRITE TO

    @Test
    public void givenString_writeToWriter() {
        new StringWriter();
//        WriterX out = new PrintStreamWriterX(  );
    }

}

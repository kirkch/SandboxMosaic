package com.mosaic.io.memory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class FixedWidthRecordStoreTest {



// BOOLEAN
    @Test
    public void givenSingleRecord_readByte0_expectFalse() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocate( 1 );
        store.select( 0 );

        assertFalse( store.readBoolean(0) );
    }

    @Test
    public void givenSingleRecord_writeByte0_expectToReadItBack() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocate( 1 );
        store.select( 0 );

        store.writeBoolean( 0, true );

        assertTrue( store.readBoolean( 0 ) );
    }

// MULTIPLE RECORD SUPPORT

    @Test
    public void readAndWriteMultipleRecords() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocate( 3 );

        store.select( 0 );
        store.writeInteger( 0, 1 );
        store.writeInteger( 4, 2 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 1 );
        store.writeInteger( 0, 3 );
        store.writeInteger( 4, 4 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 2 );
        store.writeInteger( 0, 5 );
        store.writeInteger( 4, 6 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );


        store.select( 0 );
        assertEquals( 1, store.readInteger(0) );
        assertEquals( 2, store.readInteger(4) );
        assertEquals( '-', store.readCharacter(9) );
        assertEquals( 99, store.readUnsignedShort(11) );

        store.select( 1 );
        assertEquals( 3, store.readInteger(0) );
        assertEquals( 4, store.readInteger(4) );
        assertEquals( '-', store.readCharacter(9) );
        assertEquals( 99, store.readUnsignedShort(11) );

        store.select( 2 );
        assertEquals( 5, store.readInteger(0) );
        assertEquals( 6, store.readInteger(4) );
        assertEquals( '-', store.readCharacter(9) );
        assertEquals( 99, store.readUnsignedShort(11) );
    }

    @Test
    public void iterateOverMultipleRecords() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocate( 3 );

        store.select( 0 );
        store.writeInteger( 0, 1 );
        store.writeInteger( 4, 2 );

        store.select( 1 );
        store.writeInteger( 0, 3 );
        store.writeInteger( 4, 4 );

        store.select( 2 );
        store.writeInteger( 0, 5 );
        store.writeInteger( 4, 6 );


        store.select( 0 );

        assertTrue( store.hasNext() );
        assertEquals( 1, store.readInteger(0) );
        assertEquals( 2, store.readInteger(4) );

        assertTrue( store.next() );
        assertEquals( 3, store.readInteger(0) );
        assertEquals( 4, store.readInteger(4) );
        assertTrue( store.hasNext() );

        assertTrue( store.next() );
        assertEquals( 5, store.readInteger(0) );
        assertEquals( 6, store.readInteger(4) );

        assertFalse( store.hasNext() );
        assertFalse( store.next() );

        assertEquals( 5, store.readInteger(0) );
        assertEquals( 6, store.readInteger(4) );
    }


// CLEAR ALL


    @Test
    public void clearAll() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocate( 3 );

        store.select( 0 );
        store.writeInteger( 0, 1 );
        store.writeInteger( 4, 2 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 1 );
        store.writeInteger( 0, 3 );
        store.writeInteger( 4, 4 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 2 );
        store.writeInteger( 0, 5 );
        store.writeInteger( 4, 6 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.clearAll();

        assertEquals( 0, store.elementCount() );
        assertFalse( store.select(0) );
        assertFalse( store.hasNext() );
    }

}

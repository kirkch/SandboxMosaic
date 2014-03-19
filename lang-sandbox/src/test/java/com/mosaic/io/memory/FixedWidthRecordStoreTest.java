package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class FixedWidthRecordStoreTest {



// BOOLEAN
    @Test
    public void givenSingleRecord_readByte0_expectFalse() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 1 );
        store.select( 0 );

        assertFalse( store.readBoolean(0) );
    }

    @Test
    public void givenSingleRecord_writeByte0_expectToReadItBack() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 1 );
        store.select( 0 );

        store.writeBoolean( 0, true );

        assertTrue( store.readBoolean( 0 ) );
    }

// MULTIPLE RECORD SUPPORT

    @Test
    public void readAndWriteMultipleRecords() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 3 );

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
        assertEquals( 1, store.readInteger( 0 ) );
        assertEquals( 2, store.readInteger( 4 ) );
        assertEquals( '-', store.readCharacter( 9 ) );
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

        store.allocateNewRecord( 3 );

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
        assertEquals( 5, store.readInteger( 0 ) );
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

        store.allocateNewRecord( 3 );

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

        assertEquals( 0, store.recordCount() );
        assertFalse( store.select(0) );
        assertFalse( store.hasNext() );
    }


// SELECTED INDEX

    @Test
    public void givenNonEmptyStore_checkSelectedIndexReturnsCorrectly() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 2 );

        assertEquals( -1, store.selectedIndex() );

        store.select( 0 );
        assertEquals( 0, store.selectedIndex() );

        store.select( 1 );
        assertEquals( 1, store.selectedIndex() );
    }

// MOVE SELECTED RECORD TO

    @Test
    public void givenNonEmptyStore_moveSelectedRecordToForwards_expectMove() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 2 );

        store.select( 0 );
        store.writeInteger( 0, 1 );
        store.writeInteger( 4, 2 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 1 );
        store.writeInteger( 0, 3 );
        store.writeInteger( 4, 4 );
        store.writeCharacter( 9, ',' );
        store.writeUnsignedShort( 11, 42 );


        // do move
        store.select( 0 );
        store.moveSelectedRecordTo( 1 );


        // assert results, both records should be identical
        store.select( 0 );

        assertTrue( store.hasNext() );
        assertEquals( 1, store.readInteger(0) );
        assertEquals( 2, store.readInteger(4) );
        assertEquals( '-', store.readCharacter(9) );
        assertEquals( 99, store.readUnsignedShort(11) );


        store.select( 1 );

        assertFalse( store.hasNext() );
        assertEquals( 1, store.readInteger(0) );
        assertEquals( 2, store.readInteger(4) );
        assertEquals( '-', store.readCharacter(9) );
        assertEquals( 99, store.readUnsignedShort(11) );
    }

    @Test
    public void givenNonEmptyStore_moveSelectedRecordToBackwards_expectMove() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 2 );

        store.select( 0 );
        store.writeInteger( 0, 1 );
        store.writeInteger( 4, 2 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 1 );
        store.writeInteger( 0, 3 );
        store.writeInteger( 4, 4 );
        store.writeCharacter( 9, ',' );
        store.writeUnsignedShort( 11, 42 );


        // do move
        store.select( 1 );
        store.moveSelectedRecordTo( 0 );


        // assert results, both records should be identical
        store.select( 0 );

        assertTrue( store.hasNext() );
        assertEquals( 3, store.readInteger(0) );
        assertEquals( 4, store.readInteger(4) );
        assertEquals( ',', store.readCharacter(9) );
        assertEquals( 42, store.readUnsignedShort(11) );


        store.select( 1 );

        assertFalse( store.hasNext() );
        assertEquals( 3, store.readInteger(0) );
        assertEquals( 4, store.readInteger(4) );
        assertEquals( ',', store.readCharacter(9) );
        assertEquals( 42, store.readUnsignedShort(11) );
    }

    @Test
    public void givenNonEmptyStore_moveSelectedRecordOverItself_expectNoChange() {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        store.allocateNewRecord( 2 );

        store.select( 0 );
        store.writeInteger( 0, 1 );
        store.writeInteger( 4, 2 );
        store.writeCharacter( 9, '-' );
        store.writeUnsignedShort( 11, 99 );

        store.select( 1 );
        store.writeInteger( 0, 3 );
        store.writeInteger( 4, 4 );
        store.writeCharacter( 9, ',' );
        store.writeUnsignedShort( 11, 42 );


        // do move
        store.select( 1 );
        store.moveSelectedRecordTo( 1 );


        // assert results, both records should be identical
        store.select( 0 );

        assertTrue( store.hasNext() );
        assertEquals( 1, store.readInteger( 0 ) );
        assertEquals( 2, store.readInteger( 4 ) );
        assertEquals( '-', store.readCharacter( 9 ) );
        assertEquals( 99, store.readUnsignedShort(11) );


        store.select( 1 );

        assertFalse( store.hasNext() );
        assertEquals( 3, store.readInteger( 0 ) );
        assertEquals( 4, store.readInteger( 4 ) );
        assertEquals( ',', store.readCharacter( 9 ) );
        assertEquals( 42, store.readUnsignedShort( 11 ) );
    }


// COPY SELECTED RECORD TO (BYTES)

    @Test
    public void givenNonEmptyStore_copySelectedRowToBytes_expectCopy() {
        FixedWidthRecordStore store = createBasicPopulatedStore(3);

        Bytes buf = Bytes.allocOnHeap( 200 );

        // do move
        store.select( 1 );
        store.copySelectedRecordTo( buf, 3 );


        assertEquals( 1, buf.readInteger(3) );
        assertEquals( 100, buf.readInteger(3+4) );
        assertEquals( 'b', buf.readCharacter(3+9) );
        assertEquals( 1, buf.readUnsignedShort(3+11) );
    }

// COPY SELECTED RECORD FROM (BYTES)

    @Test
    public void givenNonEmptyStore_copySelectedRowFromBytes_expectCopy() {
        FixedWidthRecordStore store = createBasicPopulatedStore(3);

        Bytes buf = Bytes.allocOnHeap( 200 );

        // do move
        store.select( 1 );
        store.copySelectedRecordTo( buf, 3 );

        store.select( 2 );
        store.copySelectedRecordFrom( buf, 3 );


        assertEquals( 1, store.readInteger(0) );
        assertEquals( 100, store.readInteger(4) );
        assertEquals( 'b', store.readCharacter(9) );
        assertEquals( 1, store.readUnsignedShort(11) );
    }


    private FixedWidthRecordStore createBasicPopulatedStore( int numRecords ) {
        FixedWidthRecordStore store = FixedWidthRecordStore.allocOnHeap( 1024,  20 );

        if ( numRecords <= 0 ) {
            return store;
        }


        store.allocateNewRecord( numRecords );

        for ( int i=0; i<numRecords; i++ ) {
            store.select( i );

            store.writeInteger( 0, i );
            store.writeInteger( 4, i*100 );
            store.writeCharacter( 9, (char) ('a' + i) );
            store.writeUnsignedShort( 11, i );
        }

        return store;
    }

}

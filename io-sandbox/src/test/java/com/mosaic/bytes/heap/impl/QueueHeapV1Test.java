package com.mosaic.bytes.heap.impl;

import com.mosaic.bytes.heap.Heap;
import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.BytesView2;
import com.mosaic.bytes2.impl.ArrayBytes2;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;


public class QueueHeapV1Test {


// NEWLY CREATED HEAP

    @Test
    public void givenNew1KBHeap_sizeBytes_expect1KB() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        assertEquals( 1024, heap.sizeBytes() );
    }

    @Test
    public void givenNew1KBHeap_usedBytes_expect200WhichIsTheHeader() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        assertEquals( QueueHeapV1.HEADER_SIZE_BYTES, heap.usedBytes() );
    }

    @Test
    public void givenNew1KBHeap_numAllocatedRecords_expect0() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        assertEquals( 0, heap.numAllocatedRecords() );
    }

    @Test
    public void givenNew1KBHeap_flush_expectFlushToBeCalledOnUnderlyingBytes() {
        AtomicBoolean flushFlag = new AtomicBoolean(false);
        Bytes2 bytes = new ArrayBytes2( 1024 ) {
            public void flush() {
                flushFlag.set( true );

                super.flush();
            }
        };

        Heap heap = QueueHeapV1.initNewHeap( bytes );

        heap.flush();

        assertTrue( flushFlag.get() );
    }



    @Test
    public void givenNew1KBHeap_allocated10Bytes_sizeBytes_expect1KB() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        heap.allocateRecord( 10 );

        assertEquals( 1024, heap.sizeBytes() );
    }

    @Test
    public void givenNew1KBHeap_allocated10Bytes_usedBytes_expect210() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        heap.allocateRecord( 10 );

        assertEquals( QueueHeapV1.HEADER_SIZE_BYTES+10+4, heap.usedBytes() );
    }

    @Test
    public void givenNew1KBHeap_allocated10Bytes_numAllocatedRecords_expect1() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        heap.allocateRecord( 10 );

        assertEquals( 1, heap.numAllocatedRecords() );
    }

    @Test
    public void givenNew1KBHeap_fillHeap_expectLastRecordAllocationToFail() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap  heap  = QueueHeapV1.initNewHeap( bytes );

        int  recordSize = 10;
        long capacity   = (bytes.sizeBytes()-QueueHeapV1.HEADER_SIZE_BYTES)/(recordSize+4);

        for ( int i=0; i<capacity; i++ ) {
            assertTrue( heap.allocateRecord( recordSize ) >= 0 );
        }

        // ensure allocations up to capacity was successful
        assertEquals( capacity, heap.numAllocatedRecords() );
        assertEquals( QueueHeapV1.HEADER_SIZE_BYTES+capacity*(recordSize+4), heap.usedBytes() );


        // expect the next allocation to go over the cliff and fail
        assertEquals( -1, heap.allocateRecord( 10 ) );

        assertEquals( capacity, heap.numAllocatedRecords() );
        assertEquals( QueueHeapV1.HEADER_SIZE_BYTES+capacity*(recordSize+4), heap.usedBytes() );
        assertEquals( heap.sizeBytes()-heap.usedBytes(), heap.remainingBytes() );
    }

    @Test
    public void givenNew1KBHeap_allocated10Bytes_releaseRecord_expectFalse() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap   heap  = QueueHeapV1.initNewHeap( bytes );

        long ptr = heap.allocateRecord( 10 );

        assertFalse( heap.releaseRecord(ptr) );

        assertEquals( 1, heap.numAllocatedRecords() );
    }

    @Test
    public void givenHeapWith1Record_attemptRelease_sizeBytes_expectUnchanged() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap   heap  = QueueHeapV1.initNewHeap( bytes );

        long ptr       = heap.allocateRecord( 10 );
        long beforeSize = heap.sizeBytes();

        assertFalse( heap.releaseRecord( ptr) );
        assertEquals( beforeSize, heap.sizeBytes() );
    }

    @Test
    public void givenHeapWith1Record_attemptRelease_usedBytes_expectUnchanged() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap   heap  = QueueHeapV1.initNewHeap( bytes );

        long ptr    = heap.allocateRecord( 10 );
        long before = heap.usedBytes();

        assertFalse( heap.releaseRecord( ptr) );
        assertEquals( before, heap.usedBytes() );
    }

    @Test
    public void givenHeapWith1Record_attemptRelease_numAllocatedRecords_expectUnchanged() {
        Bytes2 bytes = new ArrayBytes2( 1024 );
        Heap   heap  = QueueHeapV1.initNewHeap( bytes );

        long ptr    = heap.allocateRecord( 10 );
        long before = heap.numAllocatedRecords();

        assertFalse( heap.releaseRecord( ptr) );
        assertEquals( before, heap.numAllocatedRecords() );
    }



// RELOAD EXISTING EMPTY HEAP

    @Test
    public void reloadExistingEmptyHeap_checkGetterValuesAreAtPreExistingValues() {
        Bytes2 bytes         = new ArrayBytes2( 1024 );
        Heap   heap          = initIntHeap(bytes, 4);

        long beforeSize      = heap.sizeBytes();
        long beforeRemaining = heap.remainingBytes();
        long beforeCount     = heap.numAllocatedRecords();


        Heap heap2 = QueueHeapV1.reloadHeap( bytes );


        assertEquals( beforeSize, heap2.sizeBytes() );
        assertEquals( beforeRemaining, heap2.remainingBytes() );
        assertEquals( beforeCount, heap2.numAllocatedRecords() );
    }

    @Test
    public void reloadExistingEmptyHeap_flush_expectCallToBePastToUnderlyingBytes() {
        AtomicBoolean flushFlag = new AtomicBoolean(false);
        Bytes2 bytes = new ArrayBytes2( 1024 ) {
            public void flush() {
                flushFlag.set( true );

                super.flush();
            }
        };

        Heap heap  = initIntHeap( bytes, 4 );
        Heap heap2 = QueueHeapV1.reloadHeap( bytes );


        assertFalse( flushFlag.get() );

        heap2.flush();

        assertTrue( flushFlag.get() );
    }


    private Heap initIntHeap( Bytes2 bytes, int numRecords ) {
        Heap       heap = QueueHeapV1.initNewHeap( bytes );
        BytesView2 view = new BytesView2();

        for ( int i=0; i<numRecords; i++ ) {
            long ptr = heap.allocateRecord( SystemX.SIZEOF_INT );

            assertTrue( ptr > 0 );

            heap.selectIntoView( view, ptr );
            view.writeInt( 0, SystemX.SIZEOF_INT, i+1 );
        }

        assertEquals( heap.numAllocatedRecords(), numRecords );
        assertEquals( QueueHeapV1.HEADER_SIZE_BYTES+numRecords*(SystemX.SIZEOF_INT*2), heap.usedBytes() );


        for ( int i=0; i<numRecords; i++ ) {
            long ptr = QueueHeapV1.HEADER_SIZE_BYTES + i*(SystemX.SIZEOF_INT*2);

            heap.selectIntoView( view, ptr );
            int v = view.readInt( 0, SystemX.SIZEOF_INT );

            assertEquals( i+1, v );
        }

        return heap;
    }
}
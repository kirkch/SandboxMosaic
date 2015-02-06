package com.mosaic.bytes.heap.impl;

import com.mosaic.bytes.ByteMarkerUtils;
import com.mosaic.bytes.heap.AppendOnlyHeap;
import com.mosaic.bytes.heap.SequentialHeap;
import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.BytesView2;
import com.mosaic.bytes2.fields.ByteArrayField2;
import com.mosaic.bytes2.fields.ByteField2;
import com.mosaic.bytes2.fields.ByteFieldsRegistry2;
import com.mosaic.bytes2.fields.IntField2;
import com.mosaic.bytes2.fields.LongField2;
import com.mosaic.lang.IllegalStateExceptionX;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;


/**
 * An append only, sequential heap that supports variable sized records.  Suitable for
 * implementing journals and variable width constant pools.
 */
public class QueueHeapV1 implements AppendOnlyHeap, SequentialHeap {

    public static final long HEADER_SIZE_BYTES = 200;

    private static final byte[] FILE_MARK  = ByteMarkerUtils.QUEUE_HEAP_MARKER;
    private static final byte   V1         = 1;


    public static QueueHeapV1 initNewHeap( Bytes2 heap ) {
        FILEMARKER_FIELD.set( heap, FILE_MARK );
        VERSION_FIELD.set( heap, V1 );
        NUMRECORDS_FIELD.set( heap, 0 );
        ENDPTR_FIELD.set( heap, FIRSTRECORD_INDEX );

        return new QueueHeapV1( heap );
    }

    public static QueueHeapV1 reloadHeap( Bytes2 heap ) {
        return new QueueHeapV1( heap );
    }


    // Memory Layout
    //
    // |MARK:byte[3]|layoutVersion:byte|numRecords:int,endPtr:long|..reserved...|  records:(length:int,...)*  |
    //                                                          12bytes      200bytes

    private static final ByteFieldsRegistry2 registry = new ByteFieldsRegistry2();

    private static final ByteArrayField2 FILEMARKER_FIELD = registry.registerByteArray( FILE_MARK.length );
    private static final ByteField2      VERSION_FIELD    = registry.registerByte();
    private static final IntField2       NUMRECORDS_FIELD = registry.registerInteger();
    private static final LongField2      ENDPTR_FIELD     = registry.registerLong();
    private static final ByteArrayField2 RESERVED_FIELD   = registry.registerByteArray( (int) (HEADER_SIZE_BYTES - registry.sizeBytes()) );


    private static final long FIRSTRECORD_INDEX = registry.sizeBytes();



    private Bytes2 heap;


    private QueueHeapV1( Bytes2 heap ) {
        if ( !FILEMARKER_FIELD.isEqualTo( heap, FILE_MARK ) ) {
            throw new IllegalStateExceptionX( "invalid heap, it does not start with the correct marker" );
        }

        this.heap = heap;
    }


    public long allocateRecord( int numBytes ) {
        int recordSize = numBytes + SystemX.SIZEOF_INT;

        QA.isGT( recordSize, numBytes, "numBytes overflowed" );

        long newPtr = ENDPTR_FIELD.get( heap );
        if ( newPtr+recordSize >= heap.sizeBytes() ) {   // overflow
            return -1;
        }

        ENDPTR_FIELD.set( heap, newPtr+recordSize );
        NUMRECORDS_FIELD.incrementByOne( heap );

        heap.writeInt( newPtr, newPtr+SystemX.SIZEOF_INT, numBytes );

        return newPtr;
    }


    public void selectIntoView( BytesView2 view, long ptr ) {
        // NB records are:   (payloadLength:int, ...payload...)
        long payloadPtr    = ptr + SystemX.SIZEOF_INT;
        int  sizeOfPayload = heap.readInt( ptr, payloadPtr );

        view.setBytes( heap, payloadPtr, payloadPtr+sizeOfPayload );
    }

    public boolean releaseRecord( long ptr ) {
        return false;
    }

    public long sizeBytes() {
        return heap.sizeBytes();
    }

    public long remainingBytes() {
        return sizeBytes() - usedBytes();
    }

    public long usedBytes() {
        return ENDPTR_FIELD.get(heap);
    }

    public long numAllocatedRecords() {
        return NUMRECORDS_FIELD.get( heap );
    }

    public void flush() {
        heap.flush();
    }

}

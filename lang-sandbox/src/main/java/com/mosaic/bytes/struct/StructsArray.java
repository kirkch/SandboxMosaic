package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.ByteFactories;
import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.LongFunction1;
import com.mosaic.lang.system.SystemX;

import java.util.Comparator;
import java.util.Iterator;

import static com.mosaic.lang.system.SystemX.SIZEOF_LONG;


/**
 * Stores structs closely packaged together.  Resizes the underlying data structure and by allocating
 * a new array and then copying.
 */
public class StructsArray<T extends ByteView> implements Structs<T> {

    private static final long HEADER_SIZE = SIZEOF_LONG;


    public static long requiredSize( long numRecords, long recordSize ) {
        return HEADER_SIZE + numRecords*recordSize;
    }


    public static <T extends ByteView> StructsArray<T> allocate( long initialCapacity, Function0<T> viewFactory, LongFunction1<Bytes> bytesFactory ) {
        long  recordSize = viewFactory.invoke().sizeBytes();
        long  numBytes   = requiredSize( initialCapacity, recordSize );
        Bytes bytes      = bytesFactory.invoke( numBytes );

        return new StructsArray<T>( bytes, viewFactory );
    }

    public static <T extends ByteView> StructsArray<T> allocateOffHeap( long initialCapacity, Function0<T> viewFactory ) {
        return allocate( initialCapacity, viewFactory, ByteFactories.OFFHEAP );
    }

    public static <T extends ByteView> StructsArray<T> allocateOnHeap( long initialCapacity, Function0<T> viewFactory ) {
        return allocate( initialCapacity, viewFactory, ByteFactories.ONHEAP );
    }



    private final long         recordSize;
    private final Function0<T> viewFactory;

    private Bytes bytes;


    public StructsArray( Bytes bytes, Function0<T> viewFactory ) {
        this.viewFactory  = viewFactory;
        this.recordSize   = viewFactory.invoke().sizeBytes();
        this.bytes        = bytes;
    }

    public long numRecords() {
        return bytes.readLong(0, SIZEOF_LONG);
    }

    public void selectInto( T view, long index ) {
        throwIfInvalidIndex( index );

        long fromOffset = index2Offset( index );

        view.setBytes( bytes, fromOffset, fromOffset+recordSize );
    }

    public T select( long index ) {
        T view = viewFactory.invoke();

        selectInto( view, index );

        return view;
    }

    public long allocateNewRecord() {
        return allocateNewRecords( 1 );
    }

    public long allocateNewRecords( long numRecords ) {
        long fromIndex = numRecords();
        long nextIndex = fromIndex+numRecords;

        bytes.writeLong( 0, SIZEOF_LONG, nextIndex );

        long maxOffsetExc = index2Offset(nextIndex);
        if ( bytes.sizeBytes() < maxOffsetExc ) {
            bytes.resize( maxOffsetExc );
        }

        return fromIndex;
    }

    public void clearAll() {
        if ( numRecords() == 0 ) {
            return;
        }

        bytes.fill( 0, bytes.sizeBytes(), (byte) 0 );
    }

    public void sort( Comparator<T> comparator ) {
        Bytes tmpBuffer = new ArrayBytes( recordSize );

        T view1 = viewFactory.invoke();
        T view2 = viewFactory.invoke();

        QuickSortAlgorithm<StructsArray<T>,T> sorter = new QuickSortAlgorithm<>(
            comparator,
            (structs,i) -> {
                structs.selectInto( view1, i );
                return view1;
            },
            (structs,i) -> {
                structs.selectInto( view2, i );
                return view2;
            },
            (structs,l,r) -> structs.swapRecords(tmpBuffer,l,r),
            StructsArray::numRecords
        );

        sorter.sort( this );
    }

    public void swapRecords( Bytes tmpBytes, long index1, long index2 ) {
        long structOffset1 = index2Offset( index1 );
        long structMaxExc1 = structOffset1 + recordSize;

        long structOffset2 = index2Offset( index2 );
        long structMaxExc2 = structOffset2 + recordSize;


        // struct 1 to buffer
        tmpBytes.writeBytes( 0, recordSize, bytes, structOffset1, structMaxExc1 );

        // struct 2 to struct 1
        bytes.writeBytes( structOffset1, structMaxExc1, bytes, structOffset2, structMaxExc2 );

        // tmpBytes to struct 2
        bytes.writeBytes( structOffset2, structMaxExc2, tmpBytes, 0, recordSize );
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private long i = 0;

            public boolean hasNext() {
                return i < numRecords();
            }

            public T next() {
                return select( i++ );
            }
        };
    }


    private long index2Offset( long index ) {
        if ( SystemX.isDebugRun() ) {
            QA.isGTEZero( index, "structIndex" );
            QA.isLTE( index, numRecords(), "structIndex", "allocatedRecordCount" );
        }

        return requiredSize(index, recordSize);
    }

    private void throwIfInvalidIndex( long index ) {
        long numRecords = numRecords();
        if ( index < 0 || index >= numRecords ) {
            if ( numRecords == 0 ) {
                throw new IndexOutOfBoundsException( "Unable to index " + index + ", the collection is empty" );
            } else if ( index < 0 ) {
                throw new IndexOutOfBoundsException( "Unable to index "+index+", the min valid index is currently 0" );
            } else {
                throw new IndexOutOfBoundsException( "Unable to index "+index+", the max valid index is currently "+(numRecords-1) );
            }
        }
    }

}



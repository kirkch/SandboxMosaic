package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes2;
import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes2;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 *
 */
public abstract class Structs<T> implements Iterable<T> {

    private final long   offset;
    private final Bytes2 bytes;
    private final long   structSizeBytes;


    private long allocatedRecordCount;


    protected Structs( Bytes2 bytes, long structSizeBytes ) {
        this( 0, bytes, structSizeBytes );
    }

    /**
     *
     * @param offset start the structs n bytes in, reserving 'offset' number of bytes as a header
     */
    protected Structs( long offset, Bytes2 bytes, long structSizeBytes ) {
        this.offset          = offset;
        this.structSizeBytes = structSizeBytes;
        this.bytes           = bytes;
    }


    protected abstract T createBlankStruct();

    protected abstract Struct toStruct( T t );



    public T allocateNew() {
        long id = allocateNewRecords( 1 );

        return get( id );
    }

    public T get( long tradeId ) {
        T struct = createBlankStruct();

        getInto( struct, tradeId );

        return struct;
    }

    public T getInto( T struct, long tradeId ) {
        selectInToView( toStruct(struct), tradeId );

        return struct;
    }



    public long getStructSizeBytes() {
        return structSizeBytes;
    }

    public long allocateNewRecords( long numRecords ) {
        QA.argIsGTEZero( numRecords, "numRecords" );

        long fromId = allocatedRecordCount;

        allocatedRecordCount += numRecords;

        return fromId;
    }

    public long getRecordCount() {
        return allocatedRecordCount;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private long i;

            public boolean hasNext() {
                return i < allocatedRecordCount;
            }

            public T next() {
                if ( !hasNext() ) {
                    throw new NoSuchElementException();
                }

                return get( i++ );
            }
        };
    }

    /**
     * Copy struct to another location, overwritting the destination struct.
     *
     * @param from source struct index
     * @param to   destination struct index
     */
    public void copy( long from, long to ) {
        long sourceOffset      = calcByteOffsetForStructIndex( from );
        long destinationOffset = calcByteOffsetForStructIndex( to );

        bytes.writeBytes( destinationOffset, destinationOffset+structSizeBytes, bytes, sourceOffset, sourceOffset+structSizeBytes );
    }

    public void copyTo( long structIndex, Bytes2 destination, long destinationOffset, long destinationMaxExc ) {
        long sourceOffset       = calcByteOffsetForStructIndex( structIndex );
        long sourceMaxExc       = sourceOffset + structSizeBytes;

        long destinationMaxExc2 = Math.min( destinationOffset + structSizeBytes, destinationMaxExc );

        destination.writeBytes( destinationOffset, destinationMaxExc2, bytes, sourceOffset, sourceMaxExc );
    }

    public void copyFrom( long destinationStructIndex, Bytes2 sourceBytes, long sourceOffset, long sourceMaxExc ) {
        long destinationOffset = calcByteOffsetForStructIndex( destinationStructIndex );
        long destinationMaxExc = destinationOffset + structSizeBytes;

        long sourceMaxExc2     = Math.min( sourceMaxExc, sourceOffset+structSizeBytes );

        this.bytes.writeBytes( destinationOffset, destinationMaxExc, sourceBytes, sourceOffset, sourceMaxExc2 );
    }

    public void swapRecords( long structIndex1, long structIndex2, Bytes2 tmpBuf, long tmpBufOffset, long tmpBufMaxExc ) {
        long structOffset1 = calcByteOffsetForStructIndex( structIndex1 );
        long structMaxExc1 = structOffset1 + structSizeBytes;
        
        long structOffset2 = calcByteOffsetForStructIndex( structIndex2 );
        long structMaxExc2 = structOffset2 + structSizeBytes;

        long tmpBufMaxExc2 = Math.min( tmpBufMaxExc, tmpBufOffset+structSizeBytes );

        // struct 1 to buffer
        tmpBuf.writeBytes( tmpBufOffset, tmpBufMaxExc2, bytes, structOffset1, structMaxExc1 );

        // struct 2 to struct 1
        bytes.writeBytes( structOffset1, structMaxExc1, bytes, structOffset2, structMaxExc2 );

        // buffer to struct 2
        bytes.writeBytes( structOffset2, structMaxExc2, tmpBuf, tmpBufOffset, tmpBufMaxExc2 );
    }



    private class SortContext {
        private final T s1 = createBlankStruct();
        private final T s2 = createBlankStruct();

        private final Bytes2 tmpBuffer = new ArrayBytes2(structSizeBytes);

        private final Comparator<T> comparator;


        public SortContext( Comparator<T> comparator ) {
            this.comparator = comparator;
        }

        public void swapRecords( long lhs, long rhs ) {
            Structs.this.swapRecords( lhs, rhs, tmpBuffer, 0, structSizeBytes );
        }

        public boolean isGT( long lhs, long rhs ) {
            getInto( s1, lhs );
            getInto( s2, rhs );

            return comparator.compare( s1, s2 ) > 0;
        }

        public boolean isGTE( long lhs, long rhs ) {
            getInto( s1, lhs );
            getInto( s2, rhs );

            return comparator.compare( s1, s2 ) >= 0;
        }

        public boolean isEQ( long lhs, long rhs ) {
            getInto( s1, lhs );
            getInto( s2, rhs );

            return comparator.compare( s1, s2 ) == 0;
        }

        public boolean isLT( long lhs, long rhs ) {
            getInto( s1, lhs );
            getInto( s2, rhs );

            return comparator.compare( s1, s2 ) < 0;
        }
    }
    /**
     * Sorts all of the records within this store.  Does not preserve the selected
     * index.
     */
    public void sort( Comparator<T> comparator ) {
        SortContext ctx = new SortContext(comparator);

        sort( ctx, 0, getRecordCount() );
    }

    private void sort( SortContext ctx, long fromInc, long toExc ) {
        long lhs = fromInc;
        long rhs = toExc-1;

        if ( lhs >= rhs ) {
            return;
        } else if ( rhs-lhs == 1 ) {
            if ( ctx.isGT( lhs, rhs ) ) {
                ctx.swapRecords( lhs, rhs );
            }

            return;
        }

        long m = partitionForQuickSort( ctx, lhs, rhs );

        sort( ctx, fromInc, m );

        long m2 = skipIdenticalRecords( ctx, m, toExc );

        if ( m2 < toExc ) {
            sort( ctx, m2, toExc );
        }
    }

    public void clearAll() {
        allocatedRecordCount = 0;
    }

    protected void selectInToView( ByteView view, long structIndex ) {
        if ( structIndex < allocatedRecordCount ) {
            long fromInc    = calcByteOffsetForStructIndex( structIndex );
            long toExc      = fromInc + structSizeBytes;

            view.setBytes( bytes, fromInc, toExc );
        } else {
            throw new IndexOutOfBoundsException( structIndex + " is >= the number of records available ("+getRecordCount()+")"  );
        }
    }

    private long calcByteOffsetForStructIndex( long structIndex ) {
        if ( SystemX.isDebugRun() ) {
            QA.isGTEZero( structIndex, "structIndex" );
            QA.isLT( structIndex, allocatedRecordCount, "structIndex", "allocatedRecordCount" );
        }

        return offset + structIndex * structSizeBytes;
    }






    private long skipIdenticalRecords( SortContext ctx, long m, long toExc ) {
        for ( long i=m; i<toExc; i++ ) {
            if ( !ctx.isEQ(m,i) ) {
                return i;
            }
        }

        return toExc;
    }

    private long partitionForQuickSort( SortContext ctx, long lhs, long rhs ) {
        QA.argIsLT( lhs, rhs, "lhs", "rhs" );
        long m = lhs + (rhs-lhs)/2;

        // we are going to sort all records >= midpoint to the RHS
        // move the midpoint to the very end of the region;  reduces edge cases and always passes
        // the target criteria
        ctx.swapRecords( m, rhs );

        m = rhs;
        rhs--;  // 4 3 5  -> 4 5 3

        while ( lhs < rhs ) {
            lhs = skipLHSRecordsThatAreLTMidPoint( ctx, m, lhs, rhs );
            rhs = skipRHSRecordsThatAreGTEMidPoint( ctx, m, lhs, rhs );

            if ( lhs < rhs ) {
                ctx.swapRecords( lhs, rhs );
                lhs++;
            }

        }

        // move m to the point where lhs and rhs crossed.. this will become
        // the point to divide and recurse the from
        if ( lhs < m ) {
            ctx.swapRecords( lhs, m );
        }

        return lhs;
    }

    private long skipLHSRecordsThatAreLTMidPoint( SortContext ctx, long m, long lhs, long rhs ) {
        for ( long i=lhs; i<=rhs; i++ ) {
            if ( !ctx.isLT(i,m) ) {
                return i;
            }
        }

        return rhs+1;
    }

    private long skipRHSRecordsThatAreGTEMidPoint( SortContext ctx, long m, long lhs, long rhs ) {
        for ( long i=rhs; i>=lhs; i-- ) {
            if ( !ctx.isGTE(i,m) ) {
                return i;
            }
        }

        return lhs-1;
    }

}

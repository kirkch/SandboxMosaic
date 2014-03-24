package com.mosaic.io.memory;

import com.mosaic.collections.concurrent.ForkJoinJob;
import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;

import java.util.Collection;


/**
*
*/
class FlyWeightQuickSortJob<T extends FlyWeight<T>> extends ForkJoinJob<T,T> {

    private FlyWeightComparator<T> comparator;

    private Bytes                  tmpBuffer;
    private long                   tmpBufferOffset;


    FlyWeightQuickSortJob( FlyWeightComparator<T> comparator, Bytes tmpBuffer, long tmpBufferOffset ) {
        QA.argNotNull( comparator, "comparator" );
        QA.argNotNull( tmpBuffer, "tmpBuffer" );
        QA.argIsGTEZero( tmpBufferOffset, "tmpBufferOffset" );

        this.comparator      = comparator;
        this.tmpBuffer       = tmpBuffer;
        this.tmpBufferOffset = tmpBufferOffset;
    }


    @Override
    protected Collection<T> forkData( T data ) {
        long lhs = 0;
        long rhs = data.getRecordCount()-1;

//        if ( lhs >= rhs ) {
            return null;
//        }

//        if ( rhs-lhs == 1 ) {
//            if ( comparator.compare((T)this,lhs,rhs).isGT() ) {
//                swapRecords( lhs,rhs, tmpBuffer, tmpBufferOffset );
//            }
//
//            return data;
//        }
    }

    /**
     * Sorts all of the records within this store.  Does not preserve the selected
     * index.
     */
    @Override
    protected T processData( T data ) {
//        long lhs = 0;
//        long rhs = data.getRecordCount()-1;
//
//        long m = partitionForQuickSort( comparator, lhs, rhs, tmpBuffer, tmpBufferOffset );
//
//        sort( comparator, fromInc, m, tmpBuffer, tmpBufferOffset );
//
//        long m2 = skipIdenticalRecords( comparator, m, toExc );
//
//        if ( m2 < toExc ) {
//            sort( comparator, m2, toExc, tmpBuffer, tmpBufferOffset );
//        }

        return data;
    }

    private long skipIdenticalRecords( FlyWeightComparator<T> comparator, long m, long toExc ) {
        for ( long i=m; i<toExc; i++ ) {
            if ( !comparator.compare((T) this,m,i).isEQ() ) {   // todo are these (T) casts costing us anything?
                return i;
            }
        }

        return toExc;
    }

//    private long partitionForQuickSort( FlyWeightComparator<T> comparator, long lhs, long rhs, Bytes tmpBuffer, long tmpBufferOffset ) {
//        QA.argIsLT( lhs, rhs, "lhs", "rhs" );
//        long m = lhs + (rhs-lhs)/2;
//
//        // we are going to sort all records >= midpoint to the RHS
//        // move the midpoint to the very end of the region;  reduces edge cases and always passes
//        // the target criteria
//        swapRecords( m, rhs, tmpBuffer, tmpBufferOffset );
//
//        m = rhs;
//        rhs--;  // 4 3 5  -> 4 5 3
//
//        while ( lhs < rhs ) {
//            lhs = skipLHSRecordsThatAreLTMidPoint( comparator, m, lhs, rhs );
//            rhs = skipRHSRecordsThatAreGTEMidPoint( comparator, m, lhs, rhs );
//
//            if ( lhs < rhs ) {
//                swapRecords( lhs, rhs, tmpBuffer, tmpBufferOffset );
//                lhs++;
//            }
//
//        }
//
//        // move m to the point where lhs and rhs crossed.. this will become
//        // the point to divide and recurse the from
//        if ( lhs < m ) {
//            swapRecords( lhs, m, tmpBuffer, tmpBufferOffset );
//        }
//
//        return lhs;
//    }

    private long skipLHSRecordsThatAreLTMidPoint( FlyWeightComparator<T> comparator, long m, long lhs, long rhs ) {
        for ( long i=lhs; i<=rhs; i++ ) {
            if ( !comparator.compare((T) this,i,m).isLT() ) {
                return i;
            }
        }

        return rhs+1;
    }

    private long skipRHSRecordsThatAreGTEMidPoint( FlyWeightComparator<T> comparator, long m, long lhs, long rhs ) {
        for ( long i=rhs; i>=lhs; i-- ) {
            if ( !comparator.compare((T) this,i,m).isGTE() ) {
                return i;
            }
        }

        return lhs-1;
    }

}

package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
*
*/
@SuppressWarnings("unchecked")
public class FlyWeightQuickSortOp<T extends FlyWeight<T>> extends FlyWeightUpdateOp<T> {

    private FlyWeightComparator<T> comparator;


    public FlyWeightQuickSortOp( FlyWeightComparator<T> comparator ) {
        QA.argNotNull( comparator, "comparator" );

        this.comparator = comparator;
    }

    protected Collection<FlyWeightRegion> doUpdate( FlyWeight<T> flyWeight, long fromInc, long toExc ) {
        long numRecords = toExc-fromInc;
        if ( numRecords <= 1 ) {
            return null;
        }

        long min = fromInc;
        long max = toExc-1;

        long pivot = partitionForQuickSort( flyWeight, min, max );

        List<FlyWeightRegion> forks = new ArrayList<>(2);

        long numElementsOnLHS = pivot-min;
        if ( numElementsOnLHS > 1 ) {
            forks.add( new FlyWeightRegion(flyWeight,min,pivot) );
        }


        long numElementsOnRHS = max+1-pivot;
        if ( numElementsOnRHS > 1 ) {
            forks.add( new FlyWeightRegion(flyWeight,pivot,toExc) );
        }

        return forks;
    }

    private long partitionForQuickSort( FlyWeight<T> data, long min, long max ) {
        QA.argIsLT( min, max+1, "min", "max" );

        Bytes tmpBuffer = Bytes.allocOnHeap( data.getRecordWidth() );

        long m = min + (max-min)/2;

        // we are going to move all records >= pivot to the RHS
        // move the pivot to the very end of the region; by definition it belongs on the RHS
        data.swapRecords( m, max, tmpBuffer, 0 );

        m = max;

        long lhs = min;
        long rhs = max-1;

        while ( lhs <= rhs ) {
            lhs = skipLHSRecordsThatAreLTMidPoint( data, m, lhs, rhs );
            rhs = skipRHSRecordsThatAreGTEMidPoint( data, m, lhs, rhs );

            if ( lhs < rhs ) {
                data.swapRecords( lhs, rhs, tmpBuffer, 0 );
                lhs++;
            }
        }

        // move m to the point where lhs and rhs crossed.. this will become
        // the point to divide and recurse the from
        if ( lhs < m ) {
            data.swapRecords( lhs, m, tmpBuffer, 0 );
        }

        return skipIdenticalRecords( data, lhs, max );
    }

    private long skipIdenticalRecords( FlyWeight<T> data, long m, long max ) {
        for ( long i=m; i<=max; i++ ) {
            if ( !comparator.compare((T) data,m,i).isEQ() ) {   // todo are these (T) casts costing us anything?
                return i;
            }
        }

        return max;
    }

    private long skipLHSRecordsThatAreLTMidPoint( FlyWeight<T> data, long m, long lhs, long rhs ) {
        for ( long i=lhs; i<=rhs; i++ ) {
            if ( !comparator.compare((T) data,i,m).isLT() ) {
                return i;
            }
        }

        return rhs+1;
    }

    private long skipRHSRecordsThatAreGTEMidPoint( FlyWeight<T> data, long m, long lhs, long rhs ) {
        for ( long i=rhs; i>=lhs; i-- ) {
            if ( !comparator.compare((T) data,i,m).isGTE() ) {
                return i;
            }
        }

        return lhs-1;
    }

}

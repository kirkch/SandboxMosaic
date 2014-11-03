package com.mosaic.bytes.struct;

import com.mosaic.lang.NotThreadSafe;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.FunctionLong;
import com.mosaic.lang.functional.FunctionObjectLong;
import com.mosaic.lang.functional.VoidFunctionObjectLongLong;

import java.util.Comparator;


/**
 *
 */
@NotThreadSafe // due to high contention; performance is best single threaded
public class QuickSortAlgorithm<C,E> {

    private Comparator<E>                 comparator;
    private FunctionObjectLong<C,E>       fetchElement1ByIndexFunc;
    private FunctionObjectLong<C,E>       fetchElement2ByIndexFunc;
    private VoidFunctionObjectLongLong<C> swapElementsByIndexFunc;
    private FunctionLong<C>               lengthFunc;


    public QuickSortAlgorithm(
        Comparator<E>                 comparator,
        FunctionObjectLong<C,E>       fetchElementByIndexFunc,
        VoidFunctionObjectLongLong<C> swapElementsByIndexFunc,
        FunctionLong<C>               lengthFunc
    ) {
        this( comparator, fetchElementByIndexFunc, fetchElementByIndexFunc, swapElementsByIndexFunc, lengthFunc );
    }

    /**
     * Use this constructor when elements are backed by a fly weight.  This version of the constructor
     * offers two separate element getters, this means that each getter can return their own singleton
     * flyweight.  The call to the fetcher will only change where the flyweight points to.
     */
    public QuickSortAlgorithm(
        Comparator<E>                 comparator,
        FunctionObjectLong<C,E>       fetchElement1ByIndexFunc,
        FunctionObjectLong<C,E>       fetchElement2ByIndexFunc,
        VoidFunctionObjectLongLong<C> swapElementsByIndexFunc,
        FunctionLong<C>               lengthFunc
    ) {
        this.comparator               = comparator;
        this.fetchElement1ByIndexFunc = fetchElement1ByIndexFunc;
        this.fetchElement2ByIndexFunc = fetchElement2ByIndexFunc;
        this.swapElementsByIndexFunc  = swapElementsByIndexFunc;
        this.lengthFunc               = lengthFunc;
    }


    public void sort( C collection ) {
        sort( collection, 0, lengthFunc.invoke(collection));
    }

    public void sort( final C collection, final long fromInc, final long toExc ) {
        long lhs = fromInc;
        long rhs = toExc-1;

        if ( lhs >= rhs ) {
            return;
        } else if ( rhs-lhs == 1 ) {
            if ( isGT( collection, lhs, rhs ) ) {
                swapElementsByIndexFunc.invoke( collection, lhs, rhs );
            }

            return;
        }

        long m = partitionForQuickSort( collection, lhs, rhs );

        sort( collection, fromInc, m );

        long m2 = skipIdenticalRecords( collection, m, toExc );

        if ( m2 < toExc ) {
            sort( collection, m2, toExc );
        }
    }



    private long skipIdenticalRecords( C collection, long m, long toExc ) {
        for ( long i=m; i<toExc; i++ ) {
            if ( !isEQ(collection, m,i) ) {
                return i;
            }
        }

        return toExc;
    }

    private long partitionForQuickSort( C collection, long lhs, long rhs ) {
        QA.argIsLT( lhs, rhs, "lhs", "rhs" );
        long m = lhs + (rhs-lhs)/2;

        // we are going to sort all records >= midpoint to the RHS
        // move the midpoint to the very end of the region;  reduces edge cases and always passes
        // the target criteria
        swapElementsByIndexFunc.invoke( collection, m, rhs );

        m = rhs;
        rhs--;  // 4 3 5  -> 4 5 3

        while ( lhs < rhs ) {
            lhs = skipLHSRecordsThatAreLTMidPoint( collection, m, lhs, rhs );
            rhs = skipRHSRecordsThatAreGTEMidPoint( collection, m, lhs, rhs );

            if ( lhs < rhs ) {
                swapElementsByIndexFunc.invoke( collection, lhs, rhs );
                lhs++;
            }

        }

        // move m to the point where lhs and rhs crossed.. this will become
        // the point to divide and recurse the from
        if ( lhs < m ) {
            swapElementsByIndexFunc.invoke( collection, lhs, m );
        }

        return lhs;
    }

    private long skipLHSRecordsThatAreLTMidPoint( C collection, long m, long lhs, long rhs ) {
        for ( long i=lhs; i<=rhs; i++ ) {
            if ( !isLT(collection, i,m) ) {
                return i;
            }
        }

        return rhs+1;
    }

    private long skipRHSRecordsThatAreGTEMidPoint( C collection, long m, long lhs, long rhs ) {
        for ( long i=rhs; i>=lhs; i-- ) {
            if ( !isGTE(collection, i,m) ) {
                return i;
            }
        }

        return lhs-1;
    }



    private boolean isGT( C collection, long lhs, long rhs ) {
        E l = fetchElement1ByIndexFunc.invoke( collection, lhs );
        E r = fetchElement2ByIndexFunc.invoke( collection, rhs );

        return comparator.compare(l, r) > 0;
    }

    private boolean isGTE( C collection, long lhs, long rhs ) {
        E l = fetchElement1ByIndexFunc.invoke( collection, lhs );
        E r = fetchElement2ByIndexFunc.invoke( collection, rhs );

        return comparator.compare(l, r) >= 0;
    }

    private boolean isEQ( C collection, long lhs, long rhs ) {
        E l = fetchElement1ByIndexFunc.invoke( collection, lhs );
        E r = fetchElement2ByIndexFunc.invoke( collection, rhs );

        return comparator.compare(l, r) == 0;
    }

    private boolean isLT( C collection, long lhs, long rhs ) {
        E l = fetchElement1ByIndexFunc.invoke( collection, lhs );
        E r = fetchElement2ByIndexFunc.invoke( collection, rhs );

        return comparator.compare(l, r) < 0;
    }

}

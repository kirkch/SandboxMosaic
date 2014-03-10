package com.mosaic.lang.math;

import com.mosaic.lang.QA;
import com.mosaic.lang.QA;

import java.lang.reflect.Array;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public class MovingAverageAggregator <T extends MathematicalNumber> extends Aggregator<T,T> {
    private T   zero;
    private int nextInsertIndex;

    private T   resultSoFar;
    private int numValues;

    private T[] circularBuffer;

    public MovingAverageAggregator( T zero, int maxSize ) {
        QA.argIsGTZero( maxSize, "maxSize" );

        this.zero        = zero;
        this.resultSoFar = zero;

        this.circularBuffer = (T[]) Array.newInstance( zero.getClass(), maxSize );
    }

    @Override
    public void append(T v) {
        if ( v == null ) {
            v = zero;
        }

        removeValueAt( nextInsertIndex );
        writeValueTo( v, nextInsertIndex );

        final int bufferLength = circularBuffer.length;
        nextInsertIndex = (nextInsertIndex+1) % bufferLength;

        if ( numValues < bufferLength) {
            numValues++;
        }
    }

    @Override
    public T getResult() {
        return numValues == 0 ? zero : (T) resultSoFar.divideBy( numValues );
    }

    private void removeValueAt( int index ) {
        T oldValue = circularBuffer[index];

        if ( oldValue != null ) {
            resultSoFar = (T) resultSoFar.subtract(oldValue);
        }
    }

    private void writeValueTo(T v, int index) {
        circularBuffer[index] = v;

        resultSoFar = (T) resultSoFar.add( v );
    }
}

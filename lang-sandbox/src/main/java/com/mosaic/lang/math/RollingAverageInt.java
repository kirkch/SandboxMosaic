package com.mosaic.lang.math;

import com.mosaic.lang.Validate;

import java.io.Serializable;

/**
 *
 */
public class RollingAverageInt implements Serializable {
    private static final long serialVersionUID = 1290192480384L;

    private int   resultSoFar;
    private int[] circularBuffer;


    private int indexOfNextInsert;
    private int numValuesInBuffer;

    public RollingAverageInt() {
        this(10);
    }

    public RollingAverageInt(int maxSize) {
        Validate.isGTZero( maxSize, "maxSize" );

        this.circularBuffer = new int[maxSize];
    }

    public void append( int v ) {
        removeValueAt( indexOfNextInsert );
        writeValueTo( v, indexOfNextInsert );

        final int bufferLength = circularBuffer.length;
        indexOfNextInsert = (indexOfNextInsert+1)% bufferLength;

        if ( numValuesInBuffer < bufferLength ) {
            numValuesInBuffer++;
        }
    }

    public int getAverage() {
        return numValuesInBuffer == 0 ? 0 : resultSoFar/numValuesInBuffer;
    }

    public void clear() {
        indexOfNextInsert = 0;
        numValuesInBuffer = 0;
    }

    public boolean isFull() {
        return getNumberOfSamples() >= circularBuffer.length;
    }

    public int getNumberOfSamples() {
        return numValuesInBuffer;
    }

    private void removeValueAt(int i) {
        if ( isFull() ) {
            resultSoFar -= circularBuffer[i];
        }
    }

    private void writeValueTo(int v, int i) {
        circularBuffer[i] = v;
        resultSoFar += v;
    }
}

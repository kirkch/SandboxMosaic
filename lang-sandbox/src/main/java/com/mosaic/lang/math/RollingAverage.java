package com.mosaic.lang.math;

import com.mosaic.lang.QA;
import com.mosaic.lang.QA;

import java.io.Serializable;


/**
 *
 */
public class RollingAverage implements Serializable {
    private static final long serialVersionUID = 1290200387935L;


    private double   resultSoFar;
    private double[] circularBuffer;


    private int indexOfNextInsert;
    private int numValuesInBuffer;

    public RollingAverage() {
        this(10);
    }

    public RollingAverage(int maxSize) {
        QA.argIsGTZero( maxSize, "maxSize" );

        this.circularBuffer = new double[maxSize];
    }

    public void append( double v ) {
        removeValueAt( indexOfNextInsert );
        writeValueTo( v, indexOfNextInsert );

        final int bufferLength = circularBuffer.length;
        indexOfNextInsert = (indexOfNextInsert+1)% bufferLength;

        if ( numValuesInBuffer < bufferLength ) {
            numValuesInBuffer++;
        }
    }

    public double getAverage() {
        return numValuesInBuffer == 0 ? 0.0 : resultSoFar/numValuesInBuffer;
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

    private void writeValueTo(double v, int i) {
        circularBuffer[i] = v;
        resultSoFar += v;
    }
}
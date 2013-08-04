package com.mosaic.collections;

import com.mosaic.utils.MathUtils;

import java.io.IOException;
import java.io.Reader;

import static java.lang.String.format;

/**
 *
 */
public class CircularBufferChar {

    private final char[] ring;

    private int from  = 0;
    private int toExc = 0;

    private       int contentCount = 0;
    private final int moduloBitmask;

    public CircularBufferChar( int maxSize ) {
        int bitmaskSafeMaxSize = MathUtils.roundUpToClosestPowerOf2( maxSize );

        ring          = new char[bitmaskSafeMaxSize];
        moduloBitmask = bitmaskSafeMaxSize-1;
    }


    public boolean append( char c ) {
        if ( isFull() ) {
            return false;
        }

        int writeToPos = incRHS(1);
        ring[writeToPos] = c;


        return true;
    }

    public boolean append( char[] c ) {
        return append( c, 0, c.length );
    }

    public boolean append( char[] c, int offset, int count ) {
        if ( contentCount + count > ring.length ) {
            return false;
        }

        int lengthToEndOfArray = ring.length-toExc;
        if ( lengthToEndOfArray >= count ) {
            System.arraycopy( c, offset, ring, toExc, count );
        } else {
            System.arraycopy( c, offset, ring, toExc, lengthToEndOfArray );

            int remainingCount = count - lengthToEndOfArray;
            System.arraycopy( c, offset+lengthToEndOfArray, ring, 0, remainingCount );
        }

        incRHS( count );

        return true;
    }

    /**
     *
     * @return false when the ring has not been able to accept any new characters because it is full
     */
    public boolean append( Reader in ) throws IOException {
        if ( isFull() ) {
            return false;
        }

        int numCharactersRead = appendFromReaderWithNoWrapping( in );
        if ( hasSpareCapacity() && numCharactersRead > 0 ) {
            appendFromReaderWithNoWrapping( in );
        }

        return true;
    }

    public int popFromRHS() {
        if ( isEmpty() ) {
            return -1;
        }

        decRHS();
        return ring[toExc];
    }

    /**
     *
     * @return -1 if the buffer is empty
     */
    public int popFromLHS() {
        if ( isEmpty() ) {
            return -1;
        }

        char v = ring[from];
        incLHS(1);

        return v;
    }

    public boolean isEmpty() {
        return contentCount == 0;
    }

    public boolean isFull() {
        return contentCount == ring.length;   // replace ring.length with constant? speeds up before runtime optimizer kicks in, then has no effect
    }

    /**
     * How many characters are currently within the ring?
     */
    public int usedCapacity() {
        return contentCount;
    }

    /**
     * Max number of characters that may be stored within this ring.
     */
    public int bufferSize() {
        return ring.length;
    }

    /**
     * How many more characters could be pushed onto the ring before it overflows?
     */
    public int freeSpace() {
        return ring.length- usedCapacity();
    }


    public int peekLHS() {
        return peekLHS(0);
    }

    public int peekLHS( int delta ) {
        if ( delta >= usedCapacity() ) {
            return -1;
        }

        return ring[ wrapIndex(from+delta) ];
    }

    public int peekRHS() {
        return peekRHS(0);
    }

    public int peekRHS( int delta ) {
        if ( delta >= usedCapacity() ) {
            return -1;
        }

        return ring[ wrapIndex(toExc-delta-1) ];
    }

    public String popStringLHS( int numCharacters ) {
        if ( numCharacters > usedCapacity() ) {
            throw new ArrayIndexOutOfBoundsException( format("cannot read %s characters from a ring that only contains %s characters",numCharacters, usedCapacity()) );
        }

        if ( from+numCharacters <= toExc ) {
            String v = new String(ring,from, numCharacters);
            incLHS(numCharacters);

            return v;
        }

        StringBuilder buf = new StringBuilder(numCharacters);
        int firstSegmentLength = Math.min(numCharacters, ring.length - from);

        buf.append( ring, from, firstSegmentLength );
        buf.append( ring, 0, numCharacters-firstSegmentLength );
        incLHS( numCharacters );

        return buf.toString();
    }


    public boolean hasSpareCapacity() {
        return usedCapacity() < ring.length;
    }

    private void incLHS( int delta ) {
        from = wrapIndex(from + delta);
        contentCount -= delta;
    }

    private void decRHS() {
        toExc = wrapIndex( toExc-1 );
        contentCount--;
    }

    private int incRHS( int delta ) {
        int oldValue = toExc;

        toExc = wrapIndex(toExc + delta);
        contentCount += delta;

        return oldValue;
    }

    private int wrapIndex( int i ) {
        // & is apx three times faster than using %; it just limits us to buffer sizes of n^2 (and the mask must be n^2-1)
        return (i + ring.length) & moduloBitmask;   // NB always doing + then % is faster than using an if block
    }

    private int appendFromReaderWithNoWrapping( Reader in ) throws IOException {
        int numCharactersRead = in.read( ring, toExc, Math.min(freeSpace(), ring.length-toExc) );
        if ( numCharactersRead > 0 ) {
            incRHS( numCharactersRead );
        }

        return numCharactersRead;
    }
}


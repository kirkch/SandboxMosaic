package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.NotThreadSafe;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;

import static com.mosaic.lang.system.SystemX.SIZEOF_LONG;


/**
 * A Bytes backed array of structures.  Each record is of the same number of
 * bytes and can be stored on or off of the heap.
 */
@NotThreadSafe
public class FixedWidthRecordStore {

    public static FixedWidthRecordStore allocOnHeap( long initialRecordStoreSize, int recordWidth ) {
        return new FixedWidthRecordStore( Bytes.allocOnHeap(initialRecordStoreSize),  recordWidth );
    }

    public static FixedWidthRecordStore allocResizingOnHeap( SystemX system, long initialRecordStoreSize, int recordWidth, long maxExpectedSize ) {
        return new FixedWidthRecordStore( Bytes.allocAutoResizingOnHeap( system, initialRecordStoreSize, maxExpectedSize ),  recordWidth );
    }


//    public static void swapRecords( Bytes tmpBuffer, FlyWeight f, long sourceIndex, long destinationIndex ) {
//
//    }
//
//    public static <T extends FlyWeight> void quickSortAll( Bytes tmpBuffer, T f, Comparator<T> d ) {
//
//    }
//
//    public static <T extends FlyWeight> void forkJoinAll( T f, ForkJoinJob job ) {
//
//    }







    private static final int HEADER_WIDTH = SIZEOF_LONG;

    /**
     * Bytes layout: the number of records allocated followed by the records.
     *
     * numAllocatedRecords  record*
     * 8 bytes              recordWidth
     */
    private Bytes       bytes;
    private final int recordWidth;

    private long selectedRecordByteOffset;
    private long allocatedRecordCount;
    private long maxByteIndexExc;



    protected FixedWidthRecordStore( Bytes bytes, int recordWidth ) {
        QA.argNotNull( bytes, "bytes" );
        QA.argIsGTZero( recordWidth, "recordWidth" );


        this.bytes                    = bytes;
        this.recordWidth = recordWidth;

        this.allocatedRecordCount     = bytes.readLong(0);
        this.maxByteIndexExc          = (allocatedRecordCount* recordWidth) + HEADER_WIDTH;
        this.selectedRecordByteOffset = HEADER_WIDTH - recordWidth;

        QA.isLTE( allocatedRecordCount * recordWidth +HEADER_WIDTH, bytes.bufferLength(), "Allocation count is greater than capacity" );
    }

    public int recordWidth() {
        return recordWidth;
    }

    public boolean hasNext() {
        return (selectedRecordByteOffset+ recordWidth) < maxByteIndexExc;
    }

    public boolean next() {
        if ( hasNext() ) {
            selectedRecordByteOffset += recordWidth;

            return true;
        } else {
            return false;
        }
    }

    public boolean select( long recordIndex ) {
        QA.argIsGTEZero( recordIndex, "recordIndex" );

        long nextOffset = calcByteOffsetForRecordIndex( recordIndex );

        if ( nextOffset < maxByteIndexExc ) {
            selectedRecordByteOffset = nextOffset;

            return true;
        } else {
            return false;
        }
    }

    /**
     * Which record is currently selected?
     *
     * @return -1 if no record has been selected
     */
    public long selectedIndex() {
        return (selectedRecordByteOffset-HEADER_WIDTH)/recordWidth;
    }

    /**
     * The number of elements accessible from this flyweight.  They are indexed
     * from zero to count (exclusive).
     */
    public long recordCount() {
        return allocatedRecordCount;
    }

    /**
     * Declare an extra n records.  The elements will be indexed from the end
     * of the current set of records.
     */
    public long allocateNewRecord( int numElements ) {
        QA.isGTZero( numElements, "numElements" );

        long from = allocatedRecordCount;

        allocatedRecordCount += numElements;
        maxByteIndexExc = (allocatedRecordCount* recordWidth) + HEADER_WIDTH;

        bytes.writeLong( 0, Long.MAX_VALUE ); //allocatedRecordCount );

        long requiredBufferSize = allocatedRecordCount * recordWidth + HEADER_WIDTH;
        if ( bytes.bufferLength() < requiredBufferSize ) {
            assert bytes.bufferLength() < Long.MAX_VALUE/2;

            bytes.resize( bytes.bufferLength()*2 );
        }

        return from;
    }

    public void release() {
        this.bytes.release();

        this.bytes = null;
    }

    public void clearAll() {
        this.allocatedRecordCount = 0;
        bytes.writeLong( 0, 0 );

        this.maxByteIndexExc          = HEADER_WIDTH;
        this.selectedRecordByteOffset = HEADER_WIDTH - recordWidth;
    }

    public void moveSelectedRecordTo( long toIndex ) {
        QA.isGTEZero( selectedIndex(), "selectedIndex()" );
        QA.argIsLT( toIndex, recordCount(), "toIndex", "recordCount()" );
        QA.argIsGTEZero( toIndex, "toIndex" );

        long destinationOffsetBytes = calcByteOffsetForRecordIndex( toIndex );

        bytes.writeBytes( destinationOffsetBytes, bytes, selectedRecordByteOffset, selectedRecordByteOffset+recordWidth );
    }

    public void copySelectedRecordTo( Bytes destinationBytes, long destinationOffsetBytes ) {
        QA.isGTEZero( selectedIndex(), "selectedIndex()" );

        destinationBytes.writeBytes( destinationOffsetBytes, bytes, selectedRecordByteOffset, selectedRecordByteOffset+recordWidth );
    }

    public void copySelectedRecordFrom( Bytes sourceBytes, long sourceOffsetBytes ) {
        QA.isGTEZero( selectedIndex(), "selectedIndex()" );

        bytes.writeBytes( selectedRecordByteOffset, sourceBytes, sourceOffsetBytes, sourceOffsetBytes+recordWidth );
    }





    public boolean readBoolean( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        return bytes.readBoolean( offset );
    }

    public byte readByte( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        return bytes.readByte( offset );
    }

    public short readShort( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        return bytes.readShort( offset );
    }

    public char readCharacter( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 2 );

        return bytes.readCharacter( offset );
    }

    public int readInteger( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 4 );

        return bytes.readInteger( offset );
    }

    public long readLong( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 8 );

        return bytes.readLong( offset );
    }

    public float readFloat( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 4 );

        return bytes.readFloat( offset );
    }

    public double readDouble( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 8 );

        return bytes.readShort( offset );
    }

    public short readUnsignedByte( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        return bytes.readUnsignedByte( offset );
    }

    public int readUnsignedShort( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 2 );

        return bytes.readUnsignedShort( offset );
    }

    public long readUnsignedInteger( long index ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 4 );

        return bytes.readUnsignedInteger( offset );
    }







    public void writeBoolean( long index, boolean v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        bytes.writeBoolean( offset, v );
    }

    public void writeByte( long index, byte v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        bytes.writeByte( offset, v );
    }

    public void writeShort( long index, short v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 2 );

        bytes.writeShort( offset, v );
    }

    public void writeCharacter( long index, char v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 2 );

        bytes.writeCharacter( offset, v );
    }

    public void writeInteger( long index, int v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 4 );

        bytes.writeInteger( offset, v );
    }

    public void writeLong( long index, long v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 8 );

        bytes.writeLong( offset, v );
    }

    public void writeFloat( long index, float v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 4 );

        bytes.writeFloat( offset, v );
    }

    public void writeDouble( long index, double v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 8 );

        bytes.writeDouble( offset, v );
    }

    public void writeUnsignedByte( long index, short v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 1 );

        bytes.writeUnsignedByte( offset, v );
    }

    public void writeUnsignedShort( long index, int v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 2 );

        bytes.writeUnsignedShort( offset, v );
    }

    public void writeUnsignedInt( long index, long v ) {
        long offset = selectedRecordByteOffset + index;

        throwIfInvalidAddress( offset, 4 );

        bytes.writeUnsignedInt( offset, v );
    }




    private void throwIfInvalidAddress( long address, int numBytes ) {
        if ( SystemX.isDebugRun() ) {
            if ( address < selectedRecordByteOffset ) {
                throw new IllegalArgumentException( "Address has under shot the allocated region" );
            } else if ( address+numBytes > selectedRecordByteOffset+ recordWidth ) {
                throw new IllegalArgumentException( "Address has over shot the allocated region" );
            }
        }
    }

    private long calcByteOffsetForRecordIndex( long recordIndex ) {
        return recordIndex * recordWidth + HEADER_WIDTH;
    }
}

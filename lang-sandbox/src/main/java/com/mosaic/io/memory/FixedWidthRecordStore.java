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


    private static final int HEADER_WIDTH = SIZEOF_LONG;

    /**
     * Bytes layout: the number of records allocated followed by the records.
     *
     * numAllocatedRecords  record*
     * 8 bytes              elementWidth
     */
    private Bytes       bytes;
    private final int   elementWidth;

    private long selectedRecordByteOffset;
    private long allocatedRecordCount;
    private long maxByteIndexExc;



    protected FixedWidthRecordStore( Bytes bytes, int elementWidth ) {
        QA.argNotNull( bytes, "bytes" );
        QA.argIsGTZero( elementWidth, "elementWidth" );


        this.bytes                    = bytes;
        this.elementWidth             = elementWidth;

        this.allocatedRecordCount     = bytes.readLong(0);
        this.maxByteIndexExc          = (allocatedRecordCount*elementWidth) + HEADER_WIDTH;
        this.selectedRecordByteOffset = HEADER_WIDTH - elementWidth;

        QA.isLTE( allocatedRecordCount *elementWidth+HEADER_WIDTH, bytes.bufferLength(), "Allocation count is greater than capacity" );
    }

    public int elementWidth() {
        return elementWidth;
    }

    public boolean hasNext() {
        return (selectedRecordByteOffset+elementWidth) < maxByteIndexExc;
    }

    public boolean next() {
        if ( hasNext() ) {
            selectedRecordByteOffset += elementWidth;

            return true;
        } else {
            return false;
        }
    }

    public boolean select( long index ) {
        QA.argIsGTEZero( index, "index" );

        long nextOffset = index * elementWidth + HEADER_WIDTH;

        if ( nextOffset < maxByteIndexExc ) {
            selectedRecordByteOffset = nextOffset;

            return true;
        } else {
            return false;
        }
    }

    /**
     * The number of elements accessible from this flyweight.  They are indexed
     * from zero to count (exclusive).
     */
    public long elementCount() {
        return allocatedRecordCount;
    }

    /**
     * Declare an extra n records.  The elements will be indexed from the end
     * of the current set of records.
     */
    public long allocate( int numElements ) {
        QA.isGTZero( numElements, "numElements" );

        long from = allocatedRecordCount;

        allocatedRecordCount += numElements;
        maxByteIndexExc = (allocatedRecordCount*elementWidth) + HEADER_WIDTH;

        bytes.writeLong( 0, Long.MAX_VALUE ); //allocatedRecordCount );

        long requiredBufferSize = allocatedRecordCount * elementWidth + HEADER_WIDTH;
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
        bytes.writeLong(0, 0);

        this.maxByteIndexExc          = HEADER_WIDTH;
        this.selectedRecordByteOffset = HEADER_WIDTH - elementWidth;
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
            } else if ( address+numBytes > selectedRecordByteOffset+elementWidth ) {
                throw new IllegalArgumentException( "Address has over shot the allocated region" );
            }
        }
    }
}

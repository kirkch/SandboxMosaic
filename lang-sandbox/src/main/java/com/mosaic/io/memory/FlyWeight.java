package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.NotThreadSafe;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;

import static com.mosaic.lang.system.SystemX.SIZEOF_LONG;



/**
 * Accesses a set of fixed width records from a shared Bytes structure.  The Bytes
 * may be on/offheap or mapped to disk, making this structure very versatile.<p/>
 *
 * A single instance of FlyWeight can only point at one record at a time.  It is not
 * thread safe, but it can be cloned to offer multiple pointers into the record
 * structure.
 */
@NotThreadSafe
@SuppressWarnings("unchecked")
public abstract class FlyWeight<T extends FlyWeight> implements Cloneable {

    private static final long RECORD_COUNT_INDEX = 0;
    private static final long MAX_OFFSET_INDEX   = RECORD_COUNT_INDEX+SIZEOF_LONG;


    private static final int HEADER_WIDTH = SIZEOF_LONG*2;


    /* Record Structure:
     *
     * |long recordCount|long maxAddressExc| ... records, each recordWidth bytes long ... |
     */
    private final Bytes records;
    private final int   recordWidth;


    private long    selectedRecordByteOffset;
    private SystemX system;


    /**
     * @param recordWidth  in bytes
     */
    protected FlyWeight( SystemX system, Bytes records, int recordWidth ) {
        QA.argNotNull( system,  "system"  );
        QA.argNotNull( records, "records" );

        this.system      = system;
        this.records     = records;
        this.recordWidth = recordWidth;
    }

    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            return Backdoor.throwException( ex );
        }
    }

    public long getRecordCount() {
        return records.readLong( RECORD_COUNT_INDEX );
    }

    private long getMaxByteIndexExc() {
        return records.readLong( MAX_OFFSET_INDEX );
    }

    public boolean hasNext() {
        return (selectedRecordByteOffset+recordWidth) < getMaxByteIndexExc();
    }

    public boolean next() {
        if ( hasNext() ) {
            selectedRecordByteOffset += recordWidth;

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

    public boolean select( long recordIndex ) {
        QA.argIsGTEZero( recordIndex, "recordIndex" );

        long nextOffset = calcByteOffsetForRecordIndex( recordIndex );

        if ( nextOffset < getMaxByteIndexExc() ) {
            selectedRecordByteOffset = nextOffset;

            return true;
        } else {
            return false;
        }
    }

    public long allocateNewRecords( int numElements ) {
        QA.isGTZero( numElements, "numElements" );


        long currentRecordCount  = getRecordCount();
        long newRecordCount      = currentRecordCount + numElements;

        long newMaxOffsetExc     = calcByteOffsetForRecordIndex( newRecordCount );


        extendRecordBuffer( newMaxOffsetExc );


        records.writeLong( RECORD_COUNT_INDEX, newRecordCount );
        records.writeLong( MAX_OFFSET_INDEX,   newMaxOffsetExc );


        return currentRecordCount;
    }

    private void extendRecordBuffer( long newMaxOffsetExc ) {
        if ( records.bufferLength() < newMaxOffsetExc ) {
            if ( records.bufferLength() < Long.MAX_VALUE/2 ) {
                system.debug( "Growing flyweight '"+records.getName()+"' to "+newMaxOffsetExc+" bytes long" );

                records.resize( records.bufferLength()*2 );
            } else {
                throw new UnsupportedOperationException( "Buffer is already massive, will not resize" );
            }
        }
    }

    public void release() {
        records.release();
    }

    public void clearAll() {
        records.writeLong( RECORD_COUNT_INDEX, 0 );
        records.writeLong( MAX_OFFSET_INDEX,   HEADER_WIDTH );
    }

    public void copySelectedRecordTo( long toDestinationIndex ) {
        QA.isGTEZero( selectedIndex(), "selectedIndex()" );
        QA.argIsLT( toDestinationIndex, getRecordCount(), "toDestinationIndex", "recordCount()" );
        QA.argIsGTEZero( toDestinationIndex, "toDestinationIndex" );

        long destinationOffsetBytes = calcByteOffsetForRecordIndex( toDestinationIndex );

        records.writeBytes( destinationOffsetBytes, records, selectedRecordByteOffset, selectedRecordByteOffset + recordWidth );
    }

    public void copySelectedRecordTo( Bytes destinationBytes, long destinationOffsetBytes ) {
        QA.isGTEZero( selectedIndex(), "selectedIndex()" );

        destinationBytes.writeBytes( destinationOffsetBytes, records, selectedRecordByteOffset, selectedRecordByteOffset + recordWidth );
    }

    public void copySelectedRecordFrom( Bytes sourceBytes, long sourceOffsetBytes ) {
        QA.isGTEZero( selectedIndex(), "selectedIndex()" );

        records.writeBytes( selectedRecordByteOffset, sourceBytes, sourceOffsetBytes, sourceOffsetBytes + recordWidth );
    }






    protected boolean readBoolean( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        return records.readBoolean( offset );
    }

    protected byte readByte( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        return records.readByte( offset );
    }

    protected short readShort( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        return records.readShort( offset );
    }

    protected char readCharacter( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 2 );

        return records.readCharacter( offset );
    }

    protected int readInteger( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 4 );

        return records.readInteger( offset );
    }

    protected long readLong( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 8 );

        return records.readLong( offset );
    }

    protected float readFloat( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 4 );

        return records.readFloat( offset );
    }

    protected double readDouble( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 8 );

        return records.readShort( offset );
    }

    protected short readUnsignedByte( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        return records.readUnsignedByte( offset );
    }

    protected int readUnsignedShort( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 2 );

        return records.readUnsignedShort( offset );
    }

    protected long readUnsignedInteger( long offsetWithinSelectedRecord ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 4 );

        return records.readUnsignedInteger( offset );
    }







    protected void writeBoolean( long offsetWithinSelectedRecord, boolean v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        records.writeBoolean( offset, v );
    }

    protected void writeByte( long offsetWithinSelectedRecord, byte v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        records.writeByte( offset, v );
    }

    protected void writeShort( long offsetWithinSelectedRecord, short v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 2 );

        records.writeShort( offset, v );
    }

    protected void writeCharacter( long offsetWithinSelectedRecord, char v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 2 );

        records.writeCharacter( offset, v );
    }

    protected void writeInteger( long offsetWithinSelectedRecord, int v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 4 );

        records.writeInteger( offset, v );
    }

    protected void writeLong( long offsetWithinSelectedRecord, long v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 8 );

        records.writeLong( offset, v );
    }

    protected void writeFloat( long offsetWithinSelectedRecord, float v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 4 );

        records.writeFloat( offset, v );
    }

    protected void writeDouble( long offsetWithinSelectedRecord, double v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 8 );

        records.writeDouble( offset, v );
    }

    protected void writeUnsignedByte( long offsetWithinSelectedRecord, short v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 1 );

        records.writeUnsignedByte( offset, v );
    }

    protected void writeUnsignedShort( long offsetWithinSelectedRecord, int v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 2 );

        records.writeUnsignedShort( offset, v );
    }

    protected void writeUnsignedInt( long offsetWithinSelectedRecord, long v ) {
        long offset = selectedRecordByteOffset + offsetWithinSelectedRecord;

        throwIfInvalidAddress( offset, 4 );

        records.writeUnsignedInt( offset, v );
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

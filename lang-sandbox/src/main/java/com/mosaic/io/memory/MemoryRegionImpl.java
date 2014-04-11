package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;

import static com.mosaic.lang.system.SystemX.*;


/**
 * Simple malloc/free implementation.  No compaction or reuse yet.
 * Minimal implementation, will come back to this when the extra features are needed.
 */
public class MemoryRegionImpl implements MemoryRegion {

    private static long DATAOFFSET_NEXTDATAADDRESS        = 0;
    private static long DATAOFFSET_NEXTINDEXOFFSET        = SIZEOF_LONG;

    private static long INDEXOFFSET_DATAADDRESS   = 0;
    private static long INDEXOFFSET_BYTECOUNT     = INDEXOFFSET_DATAADDRESS+SIZEOF_LONG;
    private static long INDEXOFFSET_RETAINCOUNT   = INDEXOFFSET_BYTECOUNT+SIZEOF_INT;



    private static long SIZEOF_INDEXRECORD = SIZEOF_LONG + SIZEOF_INT + SIZEOF_UNSIGNED_BYTE;
    private static long SIZEOF_DATAHEADER  = SIZEOF_LONG + SIZEOF_INT;


    public static MemoryRegion allocOnHeap( long maxSize ) {
        QA.argIsGTZero( maxSize, "maxSize" );

        Bytes data  = Bytes.allocOnHeap( maxSize );
        Bytes index = Bytes.allocOnHeap( (maxSize/10) * SIZEOF_INDEXRECORD );

        return new MemoryRegionImpl( data, index );
    }



    /**
     *
     */
    private Bytes data;

    /**
     * Indexed by the pointer address returned by malloc.
     *
     * Data Layout:
     *
     * (offsetIntoData:Long,length:Int,retainCount:UByte)*
     */
    private Bytes index;


    public MemoryRegionImpl( Bytes data, Bytes index ) {
        this.data = data;
        this.index = index;

        setNextDataAddress( SIZEOF_DATAHEADER );
        setNextIndexOffset( 0 );
    }


    public int malloc( int numBytes ) {
        long dataAddress  = getNextDataAddress();
        int  indexAddress = getNextIndexOffset();

        setNextDataAddress( dataAddress+numBytes );
        setNextIndexOffset( indexAddress+1 );

        setIndexRecord( indexAddress, dataAddress, numBytes );

        return indexAddress;
    }



    public void retain( int address ) {
        errorIfInvalidRecordAddress(address);

        long baseAddress = ((long)address)*SIZEOF_INDEXRECORD;

        long retainPtr = baseAddress + INDEXOFFSET_RETAINCOUNT;
        short retainCount = index.readUnsignedByte( retainPtr );

        assert retainCount > 0;

        retainCount += 1;

        index.writeUnsignedByte( retainPtr, retainCount );
    }

    public void free( int address ) {
        errorIfInvalidRecordAddress(address);

        long baseAddress = ((long)address)*SIZEOF_INDEXRECORD;

        long retainPtr = baseAddress + INDEXOFFSET_RETAINCOUNT;
        short retainCount = index.readUnsignedByte( retainPtr );

        assert retainCount > 0;

        if ( retainCount == 1 ) {
            setIndexRecord( address, 0, 0 );  // todo defragmentation and reuse of old spots
        } else {
            retainCount--;

            index.writeUnsignedByte( retainPtr, retainCount );
        }
    }

    public short getCurrentRetainCountFor( int address ) {
        errorIfInvalidRecordAddress(address);

        long baseAddress = ((long)address)*SIZEOF_INDEXRECORD + INDEXOFFSET_RETAINCOUNT;

        return index.readUnsignedByte(baseAddress);
    }

    public void writeBoolean( int baseAddress, int offset, boolean newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_BOOLEAN );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeBoolean( dataAddress + offset, newValue );
    }

    public boolean readBoolean( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_BOOLEAN );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readBoolean( dataAddress + offset );
    }

    public void writeByte( int baseAddress, int offset, byte newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_BYTE );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeByte( dataAddress + offset, newValue );
    }

    public byte readByte( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_BYTE );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readByte( dataAddress + offset );
    }

    public void writeCharacter( int baseAddress, int offset, char newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_CHAR );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeCharacter( dataAddress + offset, newValue );
    }

    public char readCharacter( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_CHAR );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readCharacter( dataAddress + offset );
    }

    public void writeUTF8Character( int baseAddress, int offset, char newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, 1 );

        long dataAddress = getDataAddressFor( baseAddress );

        int numBytesWritten = data.writeUTF8Character( dataAddress + offset, newValue );

        errorIfInvalidRecordAddress( baseAddress, offset, numBytesWritten );
    }

    public void readUTF8Character( int baseAddress, int offset, DecodedCharacter buf ) {
        errorIfInvalidRecordAddress( baseAddress, offset, 1 );

        long dataAddress = getDataAddressFor( baseAddress );


        data.readUTF8Character( dataAddress + offset, buf );

        errorIfInvalidRecordAddress( baseAddress, offset, buf.numBytesConsumed );
    }

    public void writeShort( int baseAddress, int offset, short newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_SHORT );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeShort( dataAddress + offset, newValue );
    }

    public short readShort( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_SHORT );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readShort( dataAddress+offset );
    }

    public void writeInt( int baseAddress, int offset, int newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_INT );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeInt( dataAddress + offset, newValue );
    }

    public int readInt( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_INT );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readInt( dataAddress + offset );
    }

    public void writeLong( int baseAddress, int offset, long newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_LONG );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeLong( dataAddress + offset, newValue );
    }

    public long readLong( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_LONG );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readLong( dataAddress+offset );
    }

    public void writeFloat( int baseAddress, int offset, float newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_FLOAT );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeFloat( dataAddress + offset, newValue );
    }

    public float readFloat( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_FLOAT );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readFloat( dataAddress+offset );
    }

    public void writeDouble( int baseAddress, int offset, double newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_DOUBLE );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeDouble( dataAddress + offset, newValue );
    }

    public double readDouble( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_DOUBLE );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readDouble( dataAddress+offset );
    }

    public void writeUnsignedByte( int baseAddress, int offset, short newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_BYTE );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeUnsignedByte( dataAddress + offset, newValue );
    }

    public short readUnsignedByte( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_BYTE );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readUnsignedByte( dataAddress+offset );
    }

    public void writeUnsignedShort( int baseAddress, int offset, int newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_SHORT );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeUnsignedShort( dataAddress + offset, newValue );
    }

    public int readUnsignedShort( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_SHORT );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readUnsignedShort( dataAddress+offset );
    }

    public void writeUnsignedInt( int baseAddress, int offset, long newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_INT );

        long dataAddress = getDataAddressFor( baseAddress );

        data.writeUnsignedInt( dataAddress + offset, newValue );
    }

    public long readUnsignedInt( int baseAddress, int offset ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_INT );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readUnsignedInt( dataAddress + offset );
    }

    public int writeUTF8String( int baseAddress, int offset, UTF8 newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, newValue.getByteCount() + 2 );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.writeUTF8String( dataAddress+offset, newValue );
    }

    public int writeUTF8String( int baseAddress, int offset, CharSequence newValue ) {
        errorIfInvalidRecordAddress( baseAddress, offset, newValue.length() );


        long dataAddress = getDataAddressFor( baseAddress );

        int numBytesWritten = data.writeUTF8String( dataAddress + offset, newValue );
        errorIfInvalidRecordAddress( baseAddress, offset, numBytesWritten );

        return numBytesWritten;
    }

    public int readUTF8String( int baseAddress, int offset, Appendable out ) {
        errorIfInvalidRecordAddress( baseAddress, offset, SIZEOF_UNSIGNED_INT );

        long dataAddress = getDataAddressFor( baseAddress );

        return data.readUTF8String( dataAddress + offset, out );
    }

    public Bytes asBytes( int baseAddress ) {
        errorIfInvalidRecordAddress( baseAddress, 0, 1 );

        long baseIndexPtr = ((long) baseAddress)*SIZEOF_INDEXRECORD;

        long baseDataPtr = index.readLong( baseIndexPtr + INDEXOFFSET_DATAADDRESS );
        int  numBytes    = index.readInt( baseIndexPtr + INDEXOFFSET_BYTECOUNT );

        return data.narrow( baseDataPtr, baseDataPtr+numBytes );
    }




    private long getNextDataAddress() {
        return data.readLong( DATAOFFSET_NEXTDATAADDRESS );
    }

    private void setNextDataAddress( long nextDataAddress ) {
        QA.argIsGTZero( nextDataAddress, "nextDataAddress" );

        data.writeLong( DATAOFFSET_NEXTDATAADDRESS, nextDataAddress );
    }

    private int getNextIndexOffset() {
        return data.readInt( DATAOFFSET_NEXTINDEXOFFSET );
    }

    private void setNextIndexOffset( int nextIndexOffset ) {
        QA.argIsGTEZero( nextIndexOffset, "nextIndexOffset" );

        data.writeInt( DATAOFFSET_NEXTINDEXOFFSET, nextIndexOffset );
    }

    /**
     *
     * @param indexOffset  the record index for 'index'
     * @param dataAddress   the ptr into 'data'
     * @param numBytes      the amount of memory allocated to the ptr
     */
    private void setIndexRecord( long indexOffset, long dataAddress, int numBytes ) {
        long baseAddress = indexOffset*SIZEOF_INDEXRECORD;

        index.writeLong(         baseAddress+INDEXOFFSET_DATAADDRESS, dataAddress );
        index.writeInt( baseAddress + INDEXOFFSET_BYTECOUNT, numBytes );
        index.writeUnsignedByte( baseAddress + INDEXOFFSET_RETAINCOUNT, (short) 1 );
    }

    private long getDataAddressFor( int indexOffset ) {
        long ptr = ((long) indexOffset)*SIZEOF_INDEXRECORD;

        return index.readLong( ptr + INDEXOFFSET_DATAADDRESS );
    }

    private void errorIfInvalidRecordAddress( int indexOffset ) {
        if ( SystemX.isDebugRun() ) {
            long ptr = ((long) indexOffset)*SIZEOF_INDEXRECORD;

            if ( ptr < 0 ) {
                throw new IllegalArgumentException( "Address 0x"+indexOffset + " undershot the memory regions index" );
            } else if ( ptr >= index.getEndIndexExc() ) {
                throw new IllegalArgumentException( "Address 0x"+indexOffset + " overshot the memory regions index" );
            }

            long dataAddress = index.readLong( ptr + INDEXOFFSET_DATAADDRESS );

            if ( dataAddress == 0 ) {
                throw new IllegalArgumentException( "Address 0x"+indexOffset + " is an invalid address" );
            }
        }
    }

    private void errorIfInvalidRecordAddress( int indexOffset, int offset, int width ) {
        if ( SystemX.isDebugRun() ) {
            errorIfInvalidRecordAddress( indexOffset );

            long ptr           = ((long) indexOffset)*SIZEOF_INDEXRECORD;
            long allocatedSize = index.readInt( ptr + INDEXOFFSET_BYTECOUNT );

            if ( offset < 0 ) {
                throw new IllegalArgumentException( "Offset must not be negative" );
            } else if ( offset+width > allocatedSize ) {
                throw new IllegalArgumentException( "Access has gone outside of the allocated record" );
            }
        }
    }

}

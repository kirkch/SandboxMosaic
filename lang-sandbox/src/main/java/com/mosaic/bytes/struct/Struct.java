package com.mosaic.bytes.struct;

import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;


/**
 * Provides functionality similar to a C struct.  The structs fields are backed by an
 * instance of com.mosaic.bytes.Bytes, which could be on or off heap.<p/>
 *
 * Also see StructRegistry, which provides a type safe set of helper tools for accessing the
 * Struct.
 */
public class Struct extends ByteView {

    // Why use Struct at all?  Why not use Bytes directly.  Using Bytes directly is perfectly valid.
    // The time to consider using Struct in preference to Bytes, is in circumstances where the storage
    // of bytes, base and maxExc elsewhere becomes either inconvenient or costly (in terms of memory
    // usage).


    private final long structSizeBytes;


    public Struct( StructRegistry reg ) {
        this( reg.sizeBytes() );
    }

    /**
     * Create a new instance of StructuredBytes.
     *
     * @param structSizeBytes structs are fixed width
     */
    public Struct( long structSizeBytes ) {
        QA.argIsGTZero( structSizeBytes, "structSizeBytes" );

        this.structSizeBytes = structSizeBytes;
    }

    @Override
    public void setBytes( Bytes bytes, long base, long maxExc ) {
        super.setBytes( bytes, base, maxExc );

        if ( SystemX.isDebugRun() ) {  // maxExc is only used in debug builds
            // do not go over the registered fields, even if the supplied maxExc says that we can
            this.maxExc = Math.min( maxExc, base + structSizeBytes );
        }
    }

    public long sizeBytes() {
        return structSizeBytes;
    }

    boolean readBoolean( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_BOOLEAN );

        return bytes.readBoolean( base+offset, maxExc );
    }

    void writeBoolean( long offset, boolean newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_BOOLEAN );

        bytes.writeBoolean( base+offset, maxExc, newValue );
    }


    byte readByte( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_BYTE );

        return bytes.readByte( base + offset, maxExc );
    }

    void writeByte( long offset, byte newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_BYTE );

        bytes.writeByte( base + offset, maxExc, newValue );
    }


    short readUnsignedByte( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_UNSIGNED_BYTE );

        return bytes.readUnsignedByte( base + offset, maxExc );
    }

    void writeUnsignedByte( long offset, short newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_UNSIGNED_BYTE );

        bytes.writeUnsignedByte( base + offset, maxExc, newValue );
    }


    short readShort( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_SHORT );

        return bytes.readShort( base + offset, maxExc );
    }

    void writeShort( long offset, short newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_SHORT );

        bytes.writeShort( base + offset, maxExc, newValue );
    }


    int readUnsignedShort( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_UNSIGNED_SHORT );

        return bytes.readUnsignedShort( base + offset, maxExc );
    }

    void writeUnsignedShort( long offset, int newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_UNSIGNED_SHORT );

        bytes.writeUnsignedShort( base + offset, maxExc, newValue );
    }


    char readCharacter( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_CHAR );

        return bytes.readCharacter( base + offset, maxExc );
    }

    void writeCharacter( long offset, char newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_CHAR );

        bytes.writeCharacter( base + offset, maxExc, newValue );
    }


    int readInt( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_INT );

        return bytes.readInt( base + offset, maxExc );
    }

    void writeInt( long offset, int newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_INT );

        bytes.writeInt( base + offset, maxExc, newValue );
    }


    long readUnsignedInt( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_UNSIGNED_INT );

        return bytes.readUnsignedInt( base + offset, maxExc );
    }

    void writeUnsignedInt( long offset, long newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_UNSIGNED_INT );

        bytes.writeUnsignedInt( base + offset, maxExc, newValue );
    }


    long readLong( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_LONG );

        return bytes.readLong( base + offset, maxExc );
    }

    void writeLong( long offset, long newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_LONG );

        bytes.writeLong( base + offset, maxExc, newValue );
    }


    float readFloat( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_FLOAT );

        return bytes.readFloat( base + offset, maxExc );
    }

    void writeFloat( long offset, float newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_FLOAT );

        bytes.writeFloat( base + offset, maxExc, newValue );
    }


    double readDouble( long offset ) {
        assertValidIndex( offset, SystemX.SIZEOF_DOUBLE );

        return bytes.readDouble( base + offset, maxExc );
    }

    void writeDouble( long offset, double newValue ) {
        assertValidIndex( offset, SystemX.SIZEOF_DOUBLE );

        bytes.writeDouble( base + offset, maxExc, newValue );
    }

    public void sync() {
        bytes.flush();
    }
    
    
    private void assertValidIndex( long offset, long size ) {
        if ( SystemX.isDebugRun() ) {
            QA.argIsWithinRange( 0, offset, offset + size, structSizeBytes, "offset", "maxExc" );
        }
    }

}

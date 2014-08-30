package com.mosaic.io.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.UTF8;


/**
 * Grows with you.
 */
public class ResizableBytes extends WrappedBytes {

    private SystemX system;
    private long    maxExpectedSize;


    /**
     *
     * @param system used for logging
     * @param wrappedBytes will be copied then released as it gets close to overflowing
     */
    public ResizableBytes( String name, SystemX system, Bytes wrappedBytes, long maxExpectedSize ) {
        super( wrappedBytes );

        QA.argNotNull(  system,          "system"          );
        QA.argIsGTZero( maxExpectedSize, "maxExpectedSize" );

        this.setName( name );

        this.system          = system;
        this.maxExpectedSize = maxExpectedSize;
    }

    public void fill( long from, long toExc, byte v ) {
        resizeIfNeeded( toExc );

        super.fill( from, toExc, v );
    }

    public void writeBoolean( long index, boolean v ) {
        resizeIfNeeded( index+1 );

        super.writeBoolean( index, v );
    }

    public void writeByte( long index, byte v ) {
        resizeIfNeeded( index+1 );

        super.writeByte( index, v );
    }

    public void writeShort( long index, short v ) {
        resizeIfNeeded( index+2 );

        super.writeShort( index, v );
    }

    public void writeCharacter( long index, char v ) {
        resizeIfNeeded( index+2 );

        super.writeCharacter( index, v );
    }

    public void writeInt( long index, int v ) {
        resizeIfNeeded( index+4 );

        super.writeInt( index, v );
    }

    public void writeLong( long index, long v ) {
        resizeIfNeeded( index+8 );

        super.writeLong( index, v );
    }

    public void writeFloat( long index, float v ) {
        resizeIfNeeded( index+4 );

        super.writeFloat( index, v );
    }

    public void writeDouble( long index, double v ) {
        resizeIfNeeded( index+8 );

        super.writeDouble( index, v );
    }

    public void writeUnsignedByte( long index, short v ) {
        resizeIfNeeded( index+1 );

        super.writeUnsignedByte( index, v );
    }

    public void writeUnsignedShort( long index, int v ) {
        resizeIfNeeded( index+2 );

        super.writeUnsignedShort( index, v );
    }

    public void writeUnsignedInt( long index, long v ) {
        resizeIfNeeded( index+4 );

        super.writeUnsignedInt( index, v );
    }

    public int writeUTF8Character( long destinationIndex, char v ) {
        resizeIfNeeded( destinationIndex +3 );

        return super.writeUTF8Character( destinationIndex, v );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray, int sourceFromInc, int sourceToExc ) {
        resizeIfNeeded( destinationIndex +(sourceToExc - sourceFromInc) );

        super.writeBytes( destinationIndex, sourceArray, sourceFromInc, sourceToExc );
    }

    public void writeBytes( long destinationIndex, long sourceFromAddress, int numBytes ) {
        resizeIfNeeded( destinationIndex +numBytes );

        super.writeBytes( destinationIndex, sourceFromAddress, numBytes );
    }

    public void writeBytes( long destinationIndex, Bytes source, long sourceFromInc, long sourceToExc ) {
        resizeIfNeeded( destinationIndex + sourceToExc - sourceFromInc );

        super.writeBytes( destinationIndex, source, sourceFromInc, sourceToExc );
    }

    public void writeBytes( Bytes source ) {
        resizeIfNeeded( positionIndex() + source.bufferLength() );

        super.writeBytes( source );
    }

    public void writeBytes( Bytes source, long sourceFromInc, long sourceToExc ) {
        resizeIfNeeded( positionIndex() + sourceToExc - sourceFromInc );

        super.writeBytes( positionIndex(), source, sourceFromInc, sourceToExc );
    }


    public void writeBoolean( boolean v ) {
        resizeIfNeeded( super.positionIndex()+1 );

        super.writeBoolean( v );
    }

    public void writeByte( byte v ) {
        resizeIfNeeded( super.positionIndex()+1 );

        super.writeByte( v );
    }

    public void writeShort( short v ) {
        resizeIfNeeded( super.positionIndex()+2 );

        super.writeShort( v );
    }

    public void writeCharacter( char v ) {
        resizeIfNeeded( super.positionIndex()+3 );

        super.writeCharacter( v );
    }

    public void writeInteger( int v ) {
        resizeIfNeeded( super.positionIndex()+4 );

        super.writeInteger( v );
    }

    public void writeLong( long v ) {
        resizeIfNeeded( super.positionIndex()+8 );

        super.writeLong( v );
    }

    public void writeFloat( float v ) {
        resizeIfNeeded( super.positionIndex()+4 );

        super.writeFloat( v );
    }

    public void writeDouble( double v ) {
        resizeIfNeeded( super.positionIndex()+8 );

        super.writeDouble( v );
    }

    public void writeUnsignedByte( short v ) {
        resizeIfNeeded( super.positionIndex()+2 );

        super.writeUnsignedByte( v );
    }

    public void writeUnsignedShort( int v ) {
        resizeIfNeeded( super.positionIndex()+2 );

        super.writeUnsignedShort( v );
    }

    public void writeUnsignedInteger( long v ) {
        resizeIfNeeded( super.positionIndex()+4 );

        super.writeUnsignedInteger( v );
    }

    public void writeBytes( long fromAddress, int numBytes ) {
        resizeIfNeeded( super.positionIndex()+numBytes );

        super.writeBytes( fromAddress, numBytes );
    }

    public int writeUTF8( char v ) {
        resizeIfNeeded( super.positionIndex()+3 );

        return super.writeUTF8( v );
    }

    public int writeUTF8StringWithLengthPrefix( CharSequence characters ) {
        resizeIfNeeded( super.positionIndex()+characters.length()*3 );

        return super.writeUTF8StringWithLengthPrefix( characters );
    }

    public void writeBytes( byte[] array, int fromInc, int toExc ) {
        resizeIfNeeded( super.positionIndex()+toExc-fromInc );

        super.writeBytes( array, fromInc, toExc );
    }

    public void writeBytes( byte[] array ) {
        resizeIfNeeded( super.positionIndex()+array.length );

        super.writeBytes( array );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray ) {
        resizeIfNeeded( super.positionIndex()+ sourceArray.length );

        super.writeBytes( destinationIndex, sourceArray );
    }

    public int writeUTF8String( long destinationIndex, CharSequence sourceCharacters ) {
        resizeIfNeeded( super.positionIndex()+ sourceCharacters.length()*3 );

        return super.writeUTF8String( destinationIndex, sourceCharacters );
    }

    public int writeUTF8String( long destinationIndex, UTF8 sourceCharacters ) {
        resizeIfNeeded( destinationIndex+ sourceCharacters.getByteCount() );

        return super.writeUTF8String( destinationIndex, sourceCharacters );
    }

    private void resizeIfNeeded( long requiredEndExc ) {
        while ( requiredEndExc > super.getEndIndexExc() ) {
            long originalSize = super.bufferLength();
            long newLength    = originalSize * 2;

            super.resize( newLength );

            system.devAudit( "ResizableBytes '%s' has been resized from %s to %s bytes", getName(), originalSize, newLength );

            QA.argIsEqualTo( newLength, super.bufferLength(), "newLength", "super.bufferLength()" );

            if ( newLength > maxExpectedSize ) {
                system.warn( "ResizableBytes '%s' has grown beyond the max expected size of '%s' bytes", getName(), maxExpectedSize );
            }
        }
    }
}

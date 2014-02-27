package com.mosaic.io.bytes;

import com.mosaic.lang.Backdoor;
import com.mosaic.lang.SystemX;
import com.mosaic.lang.Validate;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8Tools;

import java.io.IOException;


/**
 * Helper class that provides default implementations for the methods on Bytes
 * that may be defined by reusing other methods on the Bytes interface.
 */
abstract class BaseBytes extends Bytes {

    private DecodedCharacter myDecodedCharacterBuffer;

    private long positionIndex;


    public void release() {
        this.positionIndex = 0;
    }

    public long positionIndex() {
        return positionIndex;
    }

    public void positionIndex( long newIndex ) {
        Validate.argIsBetween( startIndex(), newIndex, getEndIndexExc(), "newIndex" );

        this.positionIndex = newIndex;
    }

    public void rewindPositionIndex() {
        this.positionIndex = startIndex();
    }

    public long remaining() {
        return getEndIndexExc() - positionIndex();
    }

    public long size() {
        return getEndIndexExc() - startIndex();
    }

    public int writeUTF8( char v ) {
        int byteCount = writeUTF8( positionIndex, v );

        positionIndex += byteCount;

        return byteCount;
    }

    public void writeBytes( byte[] array ) {
        writeBytes( array, 0, array.length );
    }

    public int writeUTF8String( long index, CharSequence characters ) {
        int utf8ByteLength = UTF8Tools.countBytesFor( characters );
        int byteCount      = 2;

        if ( !SystemX.isRecklessRun() ) {
            Validate.isLTE( utf8ByteLength, remaining(), "%s bytes are required, but only %s remain", utf8ByteLength, remaining() );
        }

        writeUnsignedShort( index, utf8ByteLength );

        for ( int i=0; i<characters.length(); i++ ) {
            char c = characters.charAt(i);

            byteCount += writeUTF8( index+byteCount, c );
        }

        return byteCount;
    }

    public int writeUTF8String( CharSequence characters ) {
        int utf8ByteLength = UTF8Tools.countBytesFor( characters );
        int byteCount      = 2;

        if ( !SystemX.isRecklessRun() ) {
            Validate.isLTE( utf8ByteLength, remaining(), "%s bytes are required, but only %s remain", utf8ByteLength, remaining() );
        }

        writeUnsignedShort( utf8ByteLength );

        for ( int i=0; i<characters.length(); i++ ) {
            char c = characters.charAt(i);

            byteCount += writeUTF8( c );
        }

        return byteCount;
    }

    public void writeBytes( byte[] array, int fromInc, int toExc ) {
        int numBytes = toExc - fromInc;

        writeBytes( positionIndex, array, fromInc, toExc );

        positionIndex += numBytes;
    }

    public void writeBytes( long index, byte[] array ) {
        writeBytes( index, array, 0, array.length );
    }




    public char readSingleUTF8Character() {
        readSingleUTF8Character( positionIndex, myDecodedCharacterBuffer() );

        incrementPosition( myDecodedCharacterBuffer.numBytesConsumed );

        return myDecodedCharacterBuffer.c;
    }

    public void readUTF8String( Appendable out ) {
        int  numBytes = readUnsignedShort();
        long endExc   = positionIndex + numBytes;

        while ( positionIndex < endExc ) {
            try {
                out.append( readSingleUTF8Character() );
            } catch ( IOException ex ) {
                Backdoor.throwException( ex );
            }
        }

        if ( positionIndex != endExc ) {
            Backdoor.throwException( new IOException("The UTF8 string was prefixed as being "+numBytes+" bytes in length, but when decoded it did not end on the expected boundary") );
        }
    }

    public long readBytes( byte[] destinationArray ) {
        long numBytes = Math.min( remaining(), destinationArray.length );

        readBytes( positionIndex, destinationArray, 0, (int) numBytes );

        incrementPosition( numBytes );

        return numBytes;
    }

    public void readBytes( byte[] destinationArray, int fromInc, int toExc ) {
        long numBytes = toExc - fromInc;

        readBytes( positionIndex, destinationArray, fromInc, toExc );

        incrementPosition( numBytes );
    }

    public void readBytes( long destinationAddress, int numBytes ) {
        readBytes( positionIndex, destinationAddress, numBytes );

        incrementPosition( numBytes );
    }




    /**
     * NB index change is not checked here, so do not make public.  It is required
     * that the check has already been performed.
     */
    protected void incrementPosition( long delta ) {
        this.positionIndex += delta;
    }




    protected DecodedCharacter myDecodedCharacterBuffer() {
        if ( myDecodedCharacterBuffer == null ) {
            myDecodedCharacterBuffer = new DecodedCharacter();
        }

        return myDecodedCharacterBuffer;
    }

}

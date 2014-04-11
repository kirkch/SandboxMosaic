package com.mosaic.io.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;
import com.mosaic.lang.text.UTF8Tools;

import java.io.IOException;


/**
 * Helper class that provides default implementations for the methods on Bytes
 * that may be defined by reusing other methods on the Bytes interface.
 */
abstract class BaseBytes extends Bytes {

    private String name;
    private long positionIndex;

    private DecodedCharacter myDecodedCharacterBuffer;


    protected void throwIfReleased() {
        if ( positionIndex < 0 ) {
            throw new IllegalStateException( getName() + " has been released()" );
        }
    }

    public void release() {
        throwIfReleased();

        this.positionIndex = Integer.MIN_VALUE;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }


    public long positionIndex() {
        return positionIndex;
    }

    public void positionIndex( long newIndex ) {
        QA.argIsBetween( startIndex(), newIndex, getEndIndexExc(), "newIndex" );

        this.positionIndex = newIndex;
    }

    public void rewindPositionIndex() {
        this.positionIndex = startIndex();
    }

    public long remaining() {
        return getEndIndexExc() - positionIndex();
    }

    public long bufferLength() {
        return getEndIndexExc() - startIndex();
    }

    public int writeUTF8( char v ) {
        int byteCount = writeUTF8Character( positionIndex, v );

        positionIndex += byteCount;

        return byteCount;
    }

    public void writeBytes( byte[] array ) {
        writeBytes( array, 0, array.length );
    }

    public int writeUTF8String( long destinationIndex, CharSequence sourceCharacters ) {
        int utf8ByteLength = UTF8Tools.countBytesFor( sourceCharacters );
        int byteCount      = 2;

        if ( !SystemX.isRecklessRun() ) {
            long spaceLeft = getEndIndexExc()-destinationIndex;

            QA.isLTE( utf8ByteLength, spaceLeft, "%s bytes are required, but only %s remain", utf8ByteLength, spaceLeft );
        }

        writeUnsignedShort( destinationIndex, utf8ByteLength );

        for ( int i=0; i< sourceCharacters.length(); i++ ) {
            char c = sourceCharacters.charAt(i);

            byteCount += writeUTF8Character( destinationIndex + byteCount, c );
        }

        return byteCount;
    }

    public int writeUTF8String( long destinationIndex, UTF8 sourceCharacters ) {
        int width = sourceCharacters.getByteCount() + 2;

        if ( !SystemX.isRecklessRun() ) {
            long spaceLeft = getEndIndexExc()-destinationIndex;

            QA.isLTE( width, spaceLeft, "%s bytes are required, but only %s remain", width, spaceLeft );
        }

        writeUnsignedShort( destinationIndex, sourceCharacters.getByteCount() );
        writeBytes( destinationIndex + 2, sourceCharacters.getBytes() );

        return width;
    }

    public int writeUTF8String( CharSequence characters ) {
        int utf8ByteLength = UTF8Tools.countBytesFor( characters );
        int byteCount      = 2;

        if ( !SystemX.isRecklessRun() ) {
            QA.isLTE( utf8ByteLength, remaining(), "%s bytes are required, but only %s remain", utf8ByteLength, remaining() );
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

    public void writeBytes( long destinationIndex, byte[] sourceArray ) {
        writeBytes( destinationIndex, sourceArray, 0, sourceArray.length );
    }




    public char readSingleUTF8Character() {
        readUTF8Character( positionIndex, myDecodedCharacterBuffer() );

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


    /**
     * Returns the remaining contents of this buffer as an ascii encoded string.
     */
    public String toString() {
        byte[] bytes = new byte[(int) remaining()];

        readBytes( bytes );

        return new String( bytes, SystemX.ASCII );
    }


    protected DecodedCharacter myDecodedCharacterBuffer() {
        if ( myDecodedCharacterBuffer == null ) {
            myDecodedCharacterBuffer = new DecodedCharacter();
        }

        return myDecodedCharacterBuffer;
    }

}

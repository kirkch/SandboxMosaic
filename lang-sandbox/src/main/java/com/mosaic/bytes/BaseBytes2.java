package com.mosaic.bytes;

import com.mosaic.io.bytes.InputBytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;
import com.mosaic.lang.text.UTF8Tools;

import java.io.IOException;
import java.io.InputStream;

import static com.mosaic.lang.system.SystemX.SIZEOF_SHORT;


/**
 *
 */
public abstract class BaseBytes2 implements Bytes2 {

    protected long base;
    protected long maxExc;

    private boolean          hasBeenReleased;
    private DecodedCharacter myDecodedCharacterBuffer;


    protected BaseBytes2( long base, long maxExc ) {
        QA.argIsBetween( 0, base, maxExc, "base" );

        this.base   = base;
        this.maxExc = maxExc;
    }

    protected void throwIfReleased() {
        if ( hasBeenReleased ) {
            throw new IllegalStateException( "has been released()" );
        }
    }

    public void release() {
        throwIfReleased();

        this.hasBeenReleased = true;
    }


    public long sizeBytes() {
        return maxExc-base;
    }


    public boolean readBoolean( long offset, long maxExc ) {
        byte v = readByte( offset, maxExc );

        return v != 0;
    }

    public void writeBoolean( long offset, long maxExc, boolean v ) {
        writeByte( offset, maxExc, v ? (byte) 1 : (byte) 0 );
    }

    public int readUTF8String( long offset, long maxExc, Appendable output ) {
        int numUTF8Bytes = readUnsignedShort( offset, maxExc );


        DecodedCharacter tmp = myDecodedCharacterBuffer();

        long max = Math.min(offset + numUTF8Bytes+SIZEOF_SHORT, maxExc);
        long i   = offset + SIZEOF_SHORT;

        while ( i < max ) {
            readUTF8Character( i, maxExc, tmp );

            i += tmp.numBytesConsumed;

            try {
                output.append( tmp.c );
            } catch ( IOException ex ) {
                Backdoor.throwException( ex );
            }
        }

        assert i == max;

        return numUTF8Bytes+2;
    }

    public int writeUTF8String( long destinationIndex, long maxExc, CharSequence sourceCharacters ) {
        int utf8ByteLength = UTF8Tools.countBytesFor( sourceCharacters );

        if ( !SystemX.isRecklessRun() ) {
            long spaceLeft = this.maxExc-destinationIndex;

            QA.isLTE( utf8ByteLength, spaceLeft, "%s bytes are required, but only %s remain", utf8ByteLength, spaceLeft );
        }

        writeUnsignedShort( destinationIndex, maxExc, utf8ByteLength );

        long ptr = destinationIndex + SIZEOF_SHORT;
        for ( int i=0; i< sourceCharacters.length(); i++ ) {
            char c = sourceCharacters.charAt(i);

            ptr += writeUTF8Character( ptr, maxExc, c );
        }

        return utf8ByteLength + SIZEOF_SHORT;
    }

    public int writeUTF8String( long destinationIndex, long maxExc, UTF8 sourceCharacters ) {
        int width = sourceCharacters.getByteCount() + 2;

        if ( !SystemX.isRecklessRun() ) {
            long spaceLeft = this.maxExc-destinationIndex;

            QA.isLTE( width, spaceLeft, "%s bytes are required, but only %s remain", width, spaceLeft );
        }

        writeUnsignedShort( destinationIndex, maxExc, sourceCharacters.getByteCount() );
        writeBytes( destinationIndex + 2, maxExc, sourceCharacters.getBytes() );

        return width;
    }

    public int writeNullTerminatedUTF8String( long offset, long maxExc, CharSequence txt ) {
        long i = offset;
        for ( int n=0; n<txt.length(); n++ ) {
            char c = txt.charAt( n );

            i += writeUTF8Character( i, maxExc, c );
        }

        writeByte( i, maxExc, SystemX.NULL_BYTE );

        return Backdoor.toInt( i - offset + 1 );
    }

    public int writeUTF8StringUndemarcated( long offset, long maxExc, CharSequence txt ) {
        long i = offset;
        for ( int n=0; n<txt.length(); n++ ) {
            char c = txt.charAt( n );

            i += writeUTF8Character( i, maxExc, c );
        }

        return Backdoor.toInt( i - offset );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceBytes ) {
        return writeBytes( offset, maxExc, sourceBytes, 0, sourceBytes.length );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray ) {
        return readBytes( offset, maxExc, destinationArray, 0, destinationArray.length );
    }

    public int readBytes( long offset, long maxExc, Bytes2 destination ) {
        return readBytes( offset, maxExc, destination, 0, destination.sizeBytes() );
    }

    public int writeBytes( long offset, long maxExc, Bytes2 sourceBytes ) {
        return writeBytes( offset, maxExc, sourceBytes, 0, sourceBytes.sizeBytes() );
    }


    public InputStream toInputStream() {
        return new InputStream() {
            private long nextIndex = 0;

            public int read() throws IOException {
                if ( nextIndex == sizeBytes() ) {
                    return -1;
                }

                return readByte( nextIndex++, sizeBytes() );
            }
        };
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        long maxExc = sizeBytes();
        for (long i=0; i< maxExc; i++ ) {
            byte b = readByte( i, maxExc );

            buf.append((char) b);
        }

        return buf.toString();
    }


    protected DecodedCharacter myDecodedCharacterBuffer() {
        if ( myDecodedCharacterBuffer == null ) {
            myDecodedCharacterBuffer = new DecodedCharacter();
        }

        return myDecodedCharacterBuffer;
    }



    protected long index( long offset, long maxExc, long numBytes ) {
        long i = this.base + offset;

        throwIfInvalidIndex( i, base+maxExc, numBytes );

        return i;
    }

    protected void throwIfInvalidIndex( long offset, long maxExc, long numBytes ) {
        if ( SystemX.isDebugRun() ) {
            long max = Math.min(maxExc,this.maxExc);

            QA.argIsWithinRange( 0, offset, offset+numBytes, max, "offset", "maxExc" );
        }
    }

}

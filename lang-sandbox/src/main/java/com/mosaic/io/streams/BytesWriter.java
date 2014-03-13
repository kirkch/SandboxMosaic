package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.UTF8;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MathUtils;


/**
 * Writes UTF8 characters to an instance of Bytes.
 */
public class BytesWriter implements WriterX {

    private static final byte[] FALSE = "false".getBytes(SystemX.UTF8);
    private static final byte[] TRUE  = "true".getBytes(SystemX.UTF8);



    private Bytes bytes;

    private final byte[] formattingBuffer = new byte[40];


    public BytesWriter( Bytes bytes ) {
        this.bytes = bytes;
    }


    public void writeBoolean( boolean v ) {
        bytes.writeBytes( v ? TRUE : FALSE );
    }

    public void writeByte( final byte v ) {
        int numBytes = MathUtils.charactersLengthOf( v );

        if ( v < 0 ) {  // if statement lets us avoid using abs to handle negative numbers; thus the net effect is faster
            formattingBuffer[0] = '-';

            byte reducingNumber = v;
            for ( int i=numBytes-1; i>0; i-- ) {
                byte digit = (byte)  (reducingNumber % 10);

                formattingBuffer[i] = (byte) ('0' - digit);

                reducingNumber /= 10;
            }
        } else {
            byte reducingNumber = v;
            for ( int i=numBytes-1; i>=0; i-- ) {
                byte digit = (byte) (reducingNumber % 10);

                formattingBuffer[i] = (byte) ('0' + digit);

                reducingNumber /= 10;
            }
        }

        bytes.writeBytes(formattingBuffer, 0, numBytes );
    }

    public void writeBytes( byte[] bytes ) {
        for ( byte b : bytes ) {
            writeByte( b );
        }
    }

    public void writeBytes( byte[] bytes, int fromIndexInc, int toExc ) {
        for ( int i=fromIndexInc; i<toExc; i++ ) {
            writeByte( bytes[i] );  // NB Hotspot is optimising the bounds checking out for us just fine
        }
    }

    public void writeCharacter( char c ) {
        bytes.writeUTF8( c );
    }

    public void writeCharacters( char[] chars ) {
        for ( char c : chars ) {
            writeCharacter( c );
        }
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int toExc ) {
        for ( int i=fromIndexInc; i<toExc; i++ ) {
            writeCharacter( chars[i] );  // NB Hotspot is optimising the bounds checking out for us just fine
        }
    }

    @Override
    public void writeShort( short v ) {
        
    }

    @Override
    public void writeUnsignedShort( int v ) {
    }

    @Override
    public void writeInt( int v ) {
        byte[] bytes1 = Integer.toString(v).getBytes( SystemX.UTF8 );

        bytes.writeBytes( bytes1 );

//        int i = 3;
//        buf[0] = (byte) v;
//        buf[1] = (byte) v;
//        buf[2] = (byte) v;
//
//        bytes.writeBytes( buf, 0, i );
    }

    @Override
    public void writeUnsignedInt( long v ) {
    }

    @Override
    public void writeLong( long v ) {
        int numBytes = MathUtils.charactersLengthOf( v );

        if ( v < 0 ) {
            formattingBuffer[0] = '-';

            long reducingNumber = v;
            for ( int i=numBytes-1; i>0; i-- ) {
                long digit = reducingNumber % 10;

                formattingBuffer[i] = (byte) ('0' - digit);

                reducingNumber /= 10;
            }
        } else {
            long reducingNumber = v;
            for ( int i=numBytes-1; i>=0; i-- ) {
                long digit = reducingNumber % 10;

                formattingBuffer[i] = (byte) ('0' + digit);

                reducingNumber /= 10;
            }
        }

        bytes.writeBytes(formattingBuffer, 0, numBytes );
    }

    @Override
    public void writeFloat( float v ) {
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        int intPart = (int) v;
        int decPart = (int) ((v - intPart) * Math.pow(10,numDecimalPlaces) + 0.5f);

        this.writeInt( intPart );
        this.writeCharacter( '.' );
        this.writeInt( decPart );
    }

    @Override
    public void writeDouble( double v ) {
    }

    @Override
    public void writeDouble( double v, int numDecimalPlaces ) {
    }

    @Override
    public void writeString( String v ) {
    }

    @Override
    public void writeLine( String v ) {
    }

    @Override
    public void writeUTF8( UTF8 v ) {
    }

    @Override
    public void writeLine( UTF8 v ) {
    }

    @Override
    public void writeException( Throwable ex ) {
    }

    @Override
    public void writeException( String msg, Throwable ex ) {
    }

    @Override
    public void newLine() {
    }
}

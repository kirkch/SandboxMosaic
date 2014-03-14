package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.UTF8;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MathUtils;


/**
 * Writes UTF8 characters to an instance of Bytes.
 */
public class BytesWriter implements WriterX {

    private static final byte[] FALSE = "false".getBytes(SystemX.UTF8);
    private static final byte[] TRUE  = "true".getBytes(SystemX.UTF8);

    private static final double[] ROUNDING_OFFSETS_BY_PRECISION = precalculateRoundingOffsets();

    private static final double[] precalculateRoundingOffsets() {
        double[] roundingOffsets = new double[10];

        for ( int i=1; i<roundingOffsets.length; i++ ) {
            roundingOffsets[i] =  Math.pow(10,-i-2)*51;
        }

        return roundingOffsets;
    }



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

    public void writeShort( short v ) {
        writeInt( v );
    }

    public void writeUnsignedShort( int v ) {
        QA.isUnsignedShort( v, "v" );

        writeInt( v );
    }

    public void writeInt( int v ) {
        writeLong(v);
    }

    public void writeUnsignedInt( long v ) {
        QA.isUnsignedInt( v, "v" );

        writeLong( v );
    }

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

        bytes.writeBytes( formattingBuffer, 0, numBytes );
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        int intPart = (int) v;

        this.writeInt( intPart );

        writeFractionalPart( v, numDecimalPlaces, intPart );
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        long intPart = (long) v;

        this.writeLong( intPart );

        writeFractionalPart( v, numDecimalPlaces, intPart );
    }

    public void writeString( String v ) {
        for ( int i=0; i<v.length(); i++ ) {
            char c = v.charAt(i);

            writeCharacter( c );
        }
    }

    public void writeLine( String v ) {
        writeString( v );
        bytes.writeByte( (byte) '\n' );
    }

    public void writeUTF8( UTF8 v ) {
        bytes.writeBytes( v.asBytes() );
    }


    public void writeLine( UTF8 v ) {
        bytes.writeBytes( v.asBytes() );
        bytes.writeByte( (byte) '\n' );
    }

    public void writeException( Throwable ex ) {
        writeStackTrace( ex );

        writeLine( "." );
    }

    private void writeStackTrace( Throwable ex ) {
        StackTraceElement[] trace = ex.getStackTrace();

        writeString( ex.getClass().getName() );
        writeString( ": " );
        writeLine( ex.getMessage() );

        for ( StackTraceElement e : trace ) {
            writeString( "    " );
            writeString( e.getClassName() );
            writeString( " (" );
            writeInt( e.getLineNumber() );
            writeLine( ")" );
        }

        Throwable cause = ex.getCause();
        if ( cause != null ) {
            writeStackTrace( cause );
        }
    }

    public void writeException( String msg, Throwable ex ) {
        writeString( msg );
        writeException( ex );
    }

    public void newLine() {
        bytes.writeByte( (byte) '\n' );
    }


    private void writeFractionalPart( double v, int numDecimalPlaces, long intPart ) {
        if ( numDecimalPlaces > 0 ) {
            this.writeCharacter( '.' );

            double roundingOffset = ROUNDING_OFFSETS_BY_PRECISION[numDecimalPlaces];
            double remainder      = Math.abs(v - intPart) + roundingOffset;

            for ( int i=0; i<numDecimalPlaces; i++ ) {
                remainder *= 10;

                byte digit = (byte) remainder;
                remainder -= digit;

                this.writeByte( digit );
            }
        }
    }
}

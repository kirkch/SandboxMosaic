package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.UTF8;
import com.mosaic.utils.MathUtils;


/**
 * Writes UTF8 characters to an instance of Bytes.
 */
public class BytesCharacterStream implements CharacterStream {

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



    private Bytes destinationBytes;

    private final byte[] formattingBuffer = new byte[40];


    public BytesCharacterStream( Bytes destinationBytes ) {
        this.destinationBytes = destinationBytes;
    }


    public void writeBoolean( boolean v ) {
        destinationBytes.writeBytes( v ? TRUE : FALSE );
    }

    public void writeByteAsNumber( final byte v ) {
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

        destinationBytes.writeBytes( formattingBuffer, 0, numBytes );
    }

    public void writeUTF8Bytes( byte[] sourceBytes ) {
        this.destinationBytes.writeBytes( sourceBytes );
    }

    public void writeUTF8Bytes( byte[] sourceBytes, int fromIndexInc, int toExc ) {
        this.destinationBytes.writeBytes( sourceBytes, fromIndexInc, toExc );
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        this.destinationBytes.writeBytes( bytes );
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        this.destinationBytes.writeBytes( bytes, fromIndexInc, toExc );
    }

    public void writeCharacter( char c ) {
        destinationBytes.writeUTF8( c );
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
        writeLong( v );
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        int numBytes = MathUtils.charactersLengthOf( v );

        int prefixLength = fixedWidth-numBytes;
        for ( int i=0; i<prefixLength; i++ ) {
            formattingBuffer[i] = paddingByte;
        }

        if ( v < 0 ) {
            formattingBuffer[0] = '-';

            long reducingNumber = v;
            for ( int i=fixedWidth-1; i>prefixLength; i-- ) {
                long digit = reducingNumber % 10;

                formattingBuffer[i] = (byte) ('0' - digit);

                reducingNumber /= 10;
            }
        } else {
            long reducingNumber = v;
            for ( int i=fixedWidth-1; i>=prefixLength; i-- ) {
                long digit = reducingNumber % 10;

                formattingBuffer[i] = (byte) ('0' + digit);

                reducingNumber /= 10;
            }
        }

        destinationBytes.writeBytes( formattingBuffer, 0, fixedWidth );
    }

    public void writeSmallCashMajorUnit( int v ) {
        int i = 0;

        int major = SmallCashType.extractMajorComponent( v );
        int minor = SmallCashType.extractMinorComponent( v );

        if ( v < 0 ) {
            formattingBuffer[i++] = '-';

            major = Math.abs(major);
        }

        i = writeToBufferPositiveNumber( formattingBuffer, i, major );

        formattingBuffer[i++] = '.';

        i = writeToBufferPositiveNumber( formattingBuffer, i, minor, 2 );

        destinationBytes.writeBytes( formattingBuffer, 0, i );
    }

    public void writeSmallCashMinorUnit( int v ) {
        int i = 0;

        int major = v / 10;
        int minor = Math.abs( v % 10 );

        if ( v < 0 ) {
            formattingBuffer[i++] = '-';

            major = Math.abs(major);
        }

        i = writeToBufferPositiveNumber( formattingBuffer, i, major );

        formattingBuffer[i++] = '.';

        i = writeToBufferPositiveNumber( formattingBuffer, i, minor, 1 );

        destinationBytes.writeBytes( formattingBuffer, 0, i );
    }

    /**
     * Prints the BigCash format in the major currency, rounding down.
     * Thus a BigCash value of 1050 represents 10.5 pence, and will be printed
     * as 0.10.
     */
    public void writeBigCashMajorUnit( long v ) {
        int i = 0;

        long major = BigCashType.extractMajorComponent( v );
        long minor = BigCashType.extractMinorComponent( v );

        if ( v < 0 ) {
            formattingBuffer[i++] = '-';

            major = Math.abs(major);
        }

        i = writeToBufferPositiveNumber( formattingBuffer, i, major );

        formattingBuffer[i++] = '.';

        i = writeToBufferPositiveNumber( formattingBuffer, i, minor, 2 );

        destinationBytes.writeBytes( formattingBuffer, 0, i );
    }

    /**
     * Prints the BigCash format in the minor currency, rounding down.
     * Thus a BigCash value of 1050 represents 10.5 pence, and will be printed
     * as 10.50.
     */
    public void writeBigCashMinorUnit( long v ) {
        int i = 0;

        long major = v / 100;
        long minor = Math.abs(v % 100);

        if ( v < 0 ) {
            formattingBuffer[i++] = '-';

            major = Math.abs(major);
        }

        i = writeToBufferPositiveNumber( formattingBuffer, i, major );

        formattingBuffer[i++] = '.';

        i = writeToBufferPositiveNumber( formattingBuffer, i, minor, 2 );

        destinationBytes.writeBytes( formattingBuffer, 0, i );
    }

    private static int writeToBufferPositiveNumber( byte[] buf, int toInc, long value ) {
        int numDigits = MathUtils.charactersLengthOf( value );

        return writeToBufferPositiveNumber( buf, toInc, value, numDigits );
    }

    private static int writeToBufferPositiveNumber( byte[] buf, int toInc, long value, int numDigits ) {
        long v = value;

        for ( int i=numDigits-1; i>=0; i-- ) {
            buf[toInc+i] = (byte) ('0' + (v%10));

            v /= 10;
        }

        return toInc+numDigits;
    }


    public void writeUnsignedInt( long v ) {
        QA.isUnsignedInt( v, "v" );

        writeLong( v );
    }

    public void writeLong( long v ) {
        int numBytes = writeToBuffer( formattingBuffer, v );

        destinationBytes.writeBytes( formattingBuffer, 0, numBytes );
    }

    private static int writeToBuffer( byte[] buf, long v ) {
        int numBytes = MathUtils.charactersLengthOf( v );

        if ( v < 0 ) {
            buf[0] = '-';

            long reducingNumber = v;
            for ( int i=numBytes-1; i>0; i-- ) {
                long digit = reducingNumber % 10;

                buf[i] = (byte) ('0' - digit);

                reducingNumber /= 10;
            }
        } else {
            long reducingNumber = v;
            for ( int i=numBytes-1; i>=0; i-- ) {
                long digit = reducingNumber % 10;

                buf[i] = (byte) ('0' + digit);

                reducingNumber /= 10;
            }
        }

        return numBytes;
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
        destinationBytes.writeByte( (byte) '\n' );
    }

    public void writeUTF8( UTF8 v ) {
        destinationBytes.writeBytes( v.getBytes() );
    }


    public void writeLine( UTF8 v ) {
        destinationBytes.writeBytes( v.getBytes() );
        destinationBytes.writeByte( (byte) '\n' );
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
        destinationBytes.writeByte( (byte) '\n' );
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

                this.writeByteAsNumber( digit );
            }
        }
    }
}

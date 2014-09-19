package com.mosaic.io.streams;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.AutoResizingBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.UTF8;
import com.mosaic.lang.text.UTF8Tools;
import com.mosaic.utils.MathUtils;


/**
 * Writes UTF8 characters to an instance of Bytes.
 */
public class UTF8Builder implements CharacterStream {

    private static final byte[] FALSE_BYTES = "false".getBytes(SystemX.UTF8);
    private static final byte[] TRUE_BYTES = "true".getBytes(SystemX.UTF8);

    private static final double[] ROUNDING_OFFSETS_BY_PRECISION = precalculateRoundingOffsets();

    private static double[] precalculateRoundingOffsets() {
        double[] roundingOffsets = new double[10];

        for ( int i=1; i<roundingOffsets.length; i++ ) {
            roundingOffsets[i] =  Math.pow(10,-i-2)*51;
        }

        return roundingOffsets;
    }


    private long   pos;
    private Bytes destinationBytes;

    private final byte[] formattingBuffer = new byte[40];


    public UTF8Builder( SystemX system ) {
        this( system, new ArrayBytes(100) );
    }

    public UTF8Builder( SystemX system, Bytes destinationBytes ) {
        this.destinationBytes = new AutoResizingBytes(system, destinationBytes, "UTF8Builder", 10000);
    }

    public long positionIndex() {
        return pos;
    }

    public void clear() {
        this.pos = 0;
    }

    public UTF8 toUTF8() {
        return new UTF8( destinationBytes, 0, pos );
    }

    public String toString() {
        return toUTF8().toString();
    }

    public boolean isEnabled() {
        return true;
    }
    public void setEnabled( boolean flag ) {}

    public void writeBoolean( boolean v ) {
        byte[] sourceBytes = v ? TRUE_BYTES : FALSE_BYTES;
        int    numBytes    = sourceBytes.length;

        destinationBytes.writeBytes( pos, pos+numBytes, sourceBytes );

        pos += numBytes;
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

        destinationBytes.writeBytes( pos, pos+numBytes, formattingBuffer, 0, numBytes );
        pos += numBytes;
    }

    public void writeUTF8Bytes( byte[] sourceBytes ) {
        long numBytes = sourceBytes.length;

        this.destinationBytes.writeBytes( pos, pos+numBytes, sourceBytes );

        pos += numBytes;
    }

    public void writeUTF8Bytes( byte[] sourceBytes, int fromIndexInc, int toExc ) {
        long numBytes = toExc - fromIndexInc;

        if ( numBytes > 0 ) {
            this.destinationBytes.writeBytes( pos, pos + numBytes, sourceBytes, fromIndexInc, toExc );

            pos += numBytes;
        }
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        long numBytes = bytes.sizeBytes();

        this.destinationBytes.writeBytes( pos, numBytes, bytes );

        pos += numBytes;
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        long numBytes = toExc - fromIndexInc;

        this.destinationBytes.writeBytes( pos, pos+numBytes, bytes, fromIndexInc, toExc );

        pos += numBytes;
    }

    public void writeCharacter( char c ) {
        long numBytes = UTF8Tools.countBytesFor( c );

        destinationBytes.writeUTF8Character( pos, pos+numBytes, c );

        pos += numBytes;
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

        destinationBytes.writeBytes( pos, pos+fixedWidth, formattingBuffer, 0, fixedWidth );
        pos += fixedWidth;
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

        destinationBytes.writeBytes( pos, pos+i, formattingBuffer, 0, i );

        pos += i;
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

        destinationBytes.writeBytes( pos, pos+i, formattingBuffer, 0, i );

        pos += i;
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

        destinationBytes.writeBytes( pos, pos+i, formattingBuffer, 0, i );

        pos += i;
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

        destinationBytes.writeBytes( pos, pos+i, formattingBuffer, 0, i );

        pos += i;
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

        destinationBytes.writeBytes( pos, pos+numBytes, formattingBuffer, 0, numBytes );

        pos += numBytes;
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
        writeCharacter( '\n' );
    }

    public void writeUTF8( UTF8 v ) {
        byte[] bytes = v.getBytes();

        destinationBytes.writeBytes( pos, pos+bytes.length, bytes );

        pos += bytes.length;
    }


    public void writeLine( UTF8 v ) {
        writeUTF8( v );
        newLine();
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
        writeCharacter( '\n' );
    }

    public void flush() {}


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

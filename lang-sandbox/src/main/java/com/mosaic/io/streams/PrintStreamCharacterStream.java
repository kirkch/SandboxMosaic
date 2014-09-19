package com.mosaic.io.streams;

import com.mosaic.bytes.Bytes;
import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.text.UTF8;
import com.mosaic.utils.MathUtils;

import java.io.PrintStream;


/**
 * PrintStream adapter for the WriterX interface.
 */
public class PrintStreamCharacterStream implements CharacterStream {

    private PrintStream out;
    private boolean     isEnabled = true;

    public PrintStreamCharacterStream( PrintStream out ) {
        QA.argNotNull( out, "out" );

        this.out = out;
    }


    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnabled( boolean flag ) {
        this.isEnabled = flag;
    }

    public void writeBoolean( boolean v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeByteAsNumber( byte v ) {
        if ( isEnabled ) {
            out.write( v );
        }
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        if ( isEnabled ) {
            out.append( new UTF8(bytes, 0, bytes.sizeBytes()).toString() );
        }
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            out.append( new UTF8(bytes, fromIndexInc, toExc).toString() );
        }
    }

    public void writeUTF8Bytes( byte[] bytes ) {
        if ( isEnabled ) {
            out.write( bytes, 0, bytes.length );
        }
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int numBytes ) {
        if ( isEnabled ) {
            out.write( bytes, fromIndexInc, numBytes );
        }
    }

    public void writeCharacter( char v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeCharacters( char[] chars ) {
        if ( isEnabled ) {
            out.print( chars );
        }
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int numChars ) {
        if ( isEnabled ) {
            for ( int i = fromIndexInc; i < fromIndexInc + numChars; i++ ) {
                out.print( chars[i] );
            }
        }
    }

    public void writeShort( short v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeUnsignedShort( int v ) {
        QA.isUnsignedShort( v, "v" );

        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeInt( int v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        if ( isEnabled ) {
            int numBytes = MathUtils.charactersLengthOf( v );
            int prefixLength = fixedWidth - numBytes;

            StringBuilder buf = new StringBuilder( numBytes );
            for ( int i = 0; i < prefixLength; i++ ) {
                buf.append( (char) paddingByte );
            }

            buf.append( Integer.toString( v ) );

            writeString( buf.toString() );
        }
    }

    public void writeSmallCashMajorUnit( int v ) {
        if ( isEnabled ) {
            int major = SmallCashType.extractMajorComponent( v );
            int minor = SmallCashType.extractMinorComponent( v );

            writeInt( major );
            writeCharacter( '.' );
            writeFixedWidthInt( minor, 2, (byte) '0' );
        }
    }

    public void writeSmallCashMinorUnit( int v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBigCashMajorUnit( long v ) {
        if ( isEnabled ) {
            long major = BigCashType.extractMajorComponent( v );
            long minor = BigCashType.extractMinorComponent( v );

            writeLong( major );
            writeCharacter( '.' );
            writeFixedWidthInt( (int) minor, 2, (byte) '0' );
        }
    }

    public void writeBigCashMinorUnit( long v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedInt( long v ) {
        QA.isUnsignedInt( v, "v" );

        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeLong( long v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeFloat( float v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            // this strangeness is because 3.145 (2dp) would be 3.14 without it :(
            // that is, 3.145 becomes 3.1449 and so would round the wrong way, so we add 0.00001
            // to correct for this
            float roundingFudge = 1 / (float) Math.pow( 10, numDecimalPlaces + 2 );

            out.print( String.format( "%." + numDecimalPlaces + "f", v + roundingFudge ) );
        }
    }

    public void writeDouble( double v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            // decimals do not require rounding fudge as they are higher precision

            out.print( String.format( "%." + numDecimalPlaces + "f", v ) );
        }
    }

    public void writeString( String v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeLine( String v ) {
        if ( isEnabled ) {
            out.println( v );
        }
    }

    public void writeUTF8( UTF8 v ) {
        if ( isEnabled ) {
            out.print( v );
        }
    }

    public void writeLine( UTF8 v ) {
        if ( isEnabled ) {
            out.println( v );
        }
    }

    public void writeException( Throwable ex ) {
        if ( isEnabled ) {
            ex.printStackTrace( out );
        }
    }

    public void writeException( String msg, Throwable ex ) {
        if ( isEnabled ) {
            writeLine( msg );
            ex.printStackTrace( out );
        }
    }

    public void newLine() {
        if ( isEnabled ) {
            out.println();
        }
    }

    public void flush() {}
}

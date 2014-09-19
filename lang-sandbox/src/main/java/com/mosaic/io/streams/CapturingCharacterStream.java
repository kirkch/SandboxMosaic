package com.mosaic.io.streams;

import com.mosaic.bytes.Bytes;
import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.text.UTF8;
import com.mosaic.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Captures the information written to a List of Strings.  Useful for tests.
 */
public class CapturingCharacterStream implements CharacterStream {

    public List<String> audit = new ArrayList<>();

    private final StringBuilder buf = new StringBuilder();

    private boolean isEnabled = true;


    public CapturingCharacterStream() {
        this( new ArrayList<>() );
    }

    public CapturingCharacterStream( List<String> audit ) {
        this.audit = audit;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean flag ) {
        this.isEnabled = flag;
    }

    public void writeBoolean( boolean v ) {
        if ( isEnabled ) {
            append( Boolean.toString(v) );
        }
    }

    public void writeByteAsNumber( byte v ) {
        if ( isEnabled ) {
            append( Byte.toString( v ) );
        }
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        if ( isEnabled ) {
            append( new UTF8(bytes, 0, bytes.sizeBytes()).toString() );
        }
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            append( new UTF8( bytes, fromIndexInc, toExc ).toString() );
        }
    }

    public void writeUTF8Bytes( byte[] bytes ) {
        if ( isEnabled ) {
            for ( byte b : bytes ) {
                writeByteAsNumber( b );
            }
        }
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            for ( int i = fromIndexInc; i < toExc; i++ ) {
                writeByteAsNumber( bytes[i] );
            }
        }
    }

    public void writeCharacter( char v ) {
        if ( isEnabled ) {
            append( Character.toString( v ) );
        }
    }

    public void writeCharacters( char[] chars ) {
        if ( isEnabled ) {
            for ( char c : chars ) {
                writeCharacter( c );
            }
        }
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int numChars ) {
        if ( isEnabled ) {
            for ( int i = fromIndexInc; i < fromIndexInc + numChars; i++ ) {
                writeCharacter( chars[i] );
            }
        }
    }

    public void writeShort( short v ) {
        if ( isEnabled ) {
            append( Short.toString( v ) );
        }
    }

    public void writeUnsignedShort( int v ) {
        if ( isEnabled ) {
            QA.isUnsignedShort( v, "v" );

            append( Integer.toString( v ) );
        }
    }

    public void writeInt( int v ) {
        if ( isEnabled ) {
            append( Integer.toString( v ) );
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

            append( buf.toString() );
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
        if ( isEnabled ) {
            long major = v / 10;
            long minor = Math.abs( v % 10 );

            writeLong( major );
            writeCharacter( '.' );
            writeLong( minor );
        }
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
        if ( isEnabled ) {
            long major = v / 100;
            long minor = Math.abs( v % 100 );

            writeLong( major );
            writeCharacter( '.' );
            writeFixedWidthInt( (int) minor, 2, (byte) '0' );
        }
    }

    public void writeUnsignedInt( long v ) {
        if ( isEnabled ) {
            QA.isUnsignedInt( v, "v" );

            append( Long.toString( v ) );
        }
    }

    public void writeLong( long v ) {
        if ( isEnabled ) {
            append( Long.toString( v ) );
        }
    }

    public void writeFloat( float v ) {
        if ( isEnabled ) {
            append( Float.toString( v ) );
        }
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            // this strangeness is because 3.145 (2dp) would be 3.14 without it :(
            // that is, 3.145 becomes 3.1449 and so would round the wrong way, so we add 0.00001
            // to hack around this
            float roundingFudge = 1 / (float) Math.pow( 10, numDecimalPlaces + 2 );

            append( String.format( "%." + numDecimalPlaces + "f", v + roundingFudge ) );
        }
    }

    public void writeDouble( double v ) {
        if ( isEnabled ) {
            append( Double.toString( v ) );
        }
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            // decimals do not require rounding fudge as they are higher precision
            append( String.format( "%." + numDecimalPlaces + "f", v ) );
        }
    }

    public void writeString( String v ) {
        if ( isEnabled ) {
            append( v );
        }
    }

    public void writeLine( String v ) {
        if ( isEnabled ) {
            append( v );

            newLine();
        }
    }

    public void writeUTF8( UTF8 v ) {
        if ( isEnabled ) {
            append( v.toString() );
        }
    }

    public void writeLine( UTF8 v ) {
        if ( isEnabled ) {
            append( v.toString() );

            newLine();
        }
    }

    public void writeException( final Throwable ex ) {
        if ( isEnabled ) {
            Throwable x = ex;

            while ( x != null ) {
                if ( x != ex ) {
                    writeLine( "Caused by: " );
                }

                writeLine( x.getClass().getSimpleName() + ": " + x.getMessage() );

                for ( StackTraceElement trace : x.getStackTrace() ) {
                    writeLine( "    at " + trace.getClassName() + "." + trace.getMethodName() + "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")" );
                }


                x = x.getCause();
            }
        }
    }

    public void writeException( String msg, Throwable ex ) {
        if ( isEnabled ) {
            writeLine( msg );
            writeException( ex );
        }
    }

    public void newLine() {
        if ( isEnabled ) {
            audit.add( formatLine( buf.toString() ) );

            buf.setLength( 0 );
        }
    }

    public void flush() {
        if ( isEnabled ) {
            if ( buf.length() > 0 ) {
                audit.add( formatLine( buf.toString() ) );

                buf.setLength( 0 );
            }
        }
    }

    protected String formatLine( String s ) {
        return s;
    }


    private void append( String v ) {
        buf.append(v);
    }
}

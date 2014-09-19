package com.mosaic.lang.system;

import com.mosaic.bytes.Bytes;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.UTF8;
import com.mosaic.lang.time.SystemClock;


/**
 *
 */
public class LogWriter implements CharacterStream {

    private SystemClock     clock;
    private String          logLevel;
    private CharacterStream out;

    private boolean startOfLineFlag = true;

    private boolean isEnabled = true;


    public LogWriter( SystemClock clock, String logLevel, CharacterStream out ) {
        QA.argNotNull( clock, "clock" );
        QA.argNotNull( out,   "out"   );

        this.clock    = clock;
        this.logLevel = logLevel.toUpperCase();
        this.out      = out;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean flag ) {
        this.isEnabled = flag;
    }

    public void writeBoolean( boolean v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeBoolean( v );
        }
    }

    public void writeByteAsNumber( byte v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeByteAsNumber( v );
        }
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUTF8Bytes( bytes );
        }
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUTF8Bytes( bytes, fromIndexInc, toExc );
        }
    }

    public void writeUTF8Bytes( byte[] bytes ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUTF8Bytes( bytes );
        }
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUTF8Bytes( bytes, fromIndexInc, toExc );
        }
    }

    public void writeCharacter( char v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeCharacter( v );
        }
    }

    public void writeCharacters( char[] chars ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeCharacters( chars );
        }
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeCharacters( chars, fromIndexInc, toExc );
        }
    }

    public void writeShort( short v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeShort( v );
        }
    }

    public void writeUnsignedShort( int v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUnsignedShort( v );
        }
    }

    public void writeInt( int v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeInt( v );
        }
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeFixedWidthInt( v, fixedWidth, paddingByte );
        }
    }

    public void writeSmallCashMajorUnit( int v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeSmallCashMajorUnit( v );
        }
    }

    public void writeSmallCashMinorUnit( int v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeSmallCashMinorUnit( v );
        }
    }

    public void writeBigCashMajorUnit( long v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeBigCashMajorUnit( v );
        }
    }

    public void writeBigCashMinorUnit( long v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeBigCashMinorUnit( v );
        }
    }

    public void writeUnsignedInt( long v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUnsignedInt( v );
        }
    }

    public void writeLong( long v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeLong( v );
        }
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeFloat( v, numDecimalPlaces );
        }
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeDouble( v, numDecimalPlaces );
        }
    }

    public void writeString( String v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeString( v );
        }
    }

    public void writeLine( String v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeString( v );
            printEndOfLine();
        }
    }

    public void writeUTF8( UTF8 v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUTF8( v );
        }
    }

    public void writeLine( UTF8 v ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeUTF8( v );
            printEndOfLine();
        }
    }

    public void writeException( Throwable ex ) {
        if ( isEnabled ) {
            out.writeException( ex.getMessage(), ex );
        }
    }

    public void writeException( String msg, Throwable ex ) {
        if ( isEnabled ) {
            printLinePrefix();

            out.writeLine( msg );
            out.writeException( ex );
        }
    }

    public void newLine() {
        if ( isEnabled ) {
            printEndOfLine();
        }
    }

    public void flush() {
        if ( isEnabled ) {
            printEndOfLine();
        }
    }


    private void printLinePrefix() {
        if ( startOfLineFlag ) {
            out.writeCharacter( '[' );
            out.writeString( logLevel );
            out.writeString( "]: " );

            startOfLineFlag = false;
        }
    }

    private void printEndOfLine() {
//        out.writeString( " (" );  -- current thinking; time is not currently needed
//        out.writeString( " )" );  -- have added an audit level log message, it may make
//                                  -- sense to log time at 'audit' and 'error' but not the rest
        out.newLine();

        startOfLineFlag = true;
    }
}

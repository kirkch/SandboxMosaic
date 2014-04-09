package com.mosaic.lang.system;

import com.mosaic.io.streams.WriterX;
import com.mosaic.lang.QA;
import com.mosaic.lang.UTF8;
import com.mosaic.lang.time.SystemClock;


/**
 *
 */
public class LogWriter implements WriterX {

    private SystemClock clock;
    private String      logLevel;
    private WriterX     out;

    private boolean startOfLineFlag = true;


    public LogWriter( SystemClock clock, String logLevel, WriterX out ) {
        QA.argNotNull( clock, "clock" );
        QA.argNotNull( out,   "out"   );

        this.clock    = clock;
        this.logLevel = logLevel.toUpperCase();
        this.out      = out;
    }



    public void writeBoolean( boolean v ) {
        printLinePrefix();

        out.writeBoolean( v );
    }

    public void writeByte( byte v ) {
        printLinePrefix();

        out.writeByte( v );
    }

    public void writeBytes( byte[] bytes ) {
        printLinePrefix();

        out.writeBytes( bytes );
    }

    public void writeBytes( byte[] bytes, int fromIndexInc, int toExc ) {
        printLinePrefix();

        out.writeBytes( bytes, fromIndexInc, toExc );
    }

    public void writeCharacter( char v ) {
        printLinePrefix();

        out.writeCharacter( v );
    }

    public void writeCharacters( char[] chars ) {
        printLinePrefix();

        out.writeCharacters( chars );
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int toExc ) {
        printLinePrefix();

        out.writeCharacters( chars, fromIndexInc, toExc );
    }

    public void writeShort( short v ) {
        printLinePrefix();

        out.writeShort( v );
    }

    public void writeUnsignedShort( int v ) {
        printLinePrefix();

        out.writeUnsignedShort( v );
    }

    public void writeInt( int v ) {
        printLinePrefix();

        out.writeInt( v );
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        printLinePrefix();

        out.writeFixedWidthInt( v, fixedWidth, paddingByte );
    }

    public void writeSmallCashMajorUnit( int v ) {
        printLinePrefix();

        out.writeSmallCashMajorUnit( v );
    }

    public void writeSmallCashMinorUnit( int v ) {
        printLinePrefix();

        out.writeSmallCashMinorUnit( v );
    }

    public void writeBigCashMajorUnit( long v ) {
        printLinePrefix();

        out.writeBigCashMajorUnit( v );
    }

    public void writeBigCashMinorUnit( long v ) {
        printLinePrefix();

        out.writeBigCashMinorUnit( v );
    }

    public void writeUnsignedInt( long v ) {
        printLinePrefix();

        out.writeUnsignedInt( v );
    }

    public void writeLong( long v ) {
        printLinePrefix();

        out.writeLong( v );
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        printLinePrefix();

        out.writeFloat( v, numDecimalPlaces );
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        printLinePrefix();

        out.writeDouble( v, numDecimalPlaces );
    }

    public void writeString( String v ) {
        printLinePrefix();

        out.writeString( v );
    }

    public void writeLine( String v ) {
        printLinePrefix();

        out.writeString( v );
        printEndOfLine();
    }

    public void writeUTF8( UTF8 v ) {
        printLinePrefix();

        out.writeUTF8( v );
    }

    public void writeLine( UTF8 v ) {
        printLinePrefix();

        out.writeUTF8( v );
        printEndOfLine();
    }

    public void writeException( Throwable ex ) {
        out.writeException( ex.getMessage(), ex );
    }

    public void writeException( String msg, Throwable ex ) {
        printLinePrefix();

        out.writeLine( msg );
        out.writeException( ex );
    }

    public void newLine() {
        printEndOfLine();
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
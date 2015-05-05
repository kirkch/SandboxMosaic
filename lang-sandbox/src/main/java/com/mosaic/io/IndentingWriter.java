package com.mosaic.io;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;

import java.io.IOException;
import java.io.Writer;


/**
 * Enhances an existing Writer with automatic prefixing of each line with an indentation string.
 * The level of indentation may be varied as required.
 */
public class IndentingWriter extends Writer {

    public static IndentingWriter toIndentingWriter( Appendable buf ) {
        if ( buf instanceof IndentingWriter ) {
            return (IndentingWriter) buf;
        } else {
            return new IndentingWriter( buf );
        }
    }


    private final Appendable delegate;
    private final String indentText;

    private int     indentationLevel      = 0;
    private boolean indentBeforeNextWrite = true;


    public IndentingWriter( Appendable delegate ) {
        this( delegate, "  " );
    }

    public IndentingWriter( Appendable delegate, String indentText ) {
        this.indentText = indentText;
        QA.argNotNull(delegate, "delegate");

        this.delegate = delegate;
    }

    public int getIndentLevel() {
        return indentationLevel;
    }

    public int incIndent() {
        return ++indentationLevel;
    }

    public int decIndent() {
        indentationLevel -= 1;

        QA.isGTEZero( indentationLevel, "indentationLevel" );

        return indentationLevel;
    }

    public void print( String txt ) {
        try {
            append(txt);
        } catch ( IOException e ) {
            Backdoor.throwException( e );
        }
    }

    public void println( String txt ) {
        print( txt );
        newLine();
    }

    public void newLine() {
        print( SystemX.NEWLINE );

        indentBeforeNextWrite = true;
    }

    public void write( char[] cbuf, int off, int len ) throws IOException {
        prefixOutputIffRequired();

        for ( int i=off; i<off+len; i++ ) {
            delegate.append( cbuf[i] );
        }
    }

    public Writer append( CharSequence csq ) throws IOException {
        prefixOutputIffRequired();

        delegate.append( csq );

        return this;
    }

    public Writer append( CharSequence csq, int start, int end ) throws IOException {
        prefixOutputIffRequired();

        delegate.append( csq, start, end );

        return this;
    }

    public Writer append( char c ) throws IOException {
        prefixOutputIffRequired();

        delegate.append( c );

        return this;
    }

    public void flush() throws IOException {
        IOUtils.flush( delegate );
    }

    public void close() throws IOException {
        IOUtils.flush( delegate );
        IOUtils.close( delegate );
    }


    private void prefixOutputIffRequired() throws IOException {
        if ( indentBeforeNextWrite ) {
            for ( int i=0; i<indentationLevel; i++ ) {
                delegate.append( indentText );
            }

            indentBeforeNextWrite = false;
        }
    }

}

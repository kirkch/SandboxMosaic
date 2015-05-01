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

    private final Writer delegate;
    private final String indentText;

    private int     indentationLevel      = 0;
    private boolean indentBeforeNextWrite = true;


    public IndentingWriter( Writer delegate ) {
        this( delegate, "  " );
    }

    public IndentingWriter( Writer delegate, String indentText ) {
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
        if ( indentBeforeNextWrite ) {
            for ( int i=0; i<indentationLevel; i++ ) {
                delegate.write( indentText );
            }

            indentBeforeNextWrite = false;
        }

        delegate.write(cbuf, off, len);
    }

    public void flush() throws IOException {
        delegate.flush();
    }

    public void close() throws IOException {
        delegate.flush();
        delegate.close();
    }

}

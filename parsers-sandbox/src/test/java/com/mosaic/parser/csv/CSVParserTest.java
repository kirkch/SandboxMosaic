package com.mosaic.parser.csv;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@Ignore("work is on hold")
public class CSVParserTest {

    private CSVParser            parser = new CSVParser();
    private RecordingCSVListener l      = new RecordingCSVListener();


    @Test
    public void givenSingleUnquotedRow_expectHeaderCallback() {
        int numCharactersParsed = parser.parse( "a,b,c", l );


        assertEquals( 5, numCharactersParsed );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): headers=['a','b','c']",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenSingleQuotedRowWithSingleColumn_expectHeaderCallback() {
        int numCharactersParsed = parser.parse( "\"a,1\"", l );


        assertEquals( 5, numCharactersParsed );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): headers=['a,1']",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenSingleQuotedRow_expectHeaderCallback() {
        int numCharactersParsed = parser.parse( "\"a,1\",\"b,2\",c", l );


        assertEquals( 13, numCharactersParsed );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): headers=['a,1','b,2','c']",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }



    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public static class RecordingCSVListener implements CSVListener {
        public final List<String> audit = new ArrayList();

        public void started() {
            audit.add("started");
        }

        public void error( int line, int col, String message ) {
            audit.add( String.format("(%d,%d): %s", line, col, message) );
        }

        public void finished() {
            audit.add("finished");
        }

        public void headers( int line, int col, List<String> headers ) {
            audit.add(  String.format("(%d,%d): headers=%s", line, col, formatList(headers)) );
        }

        public void row( int line, int col, List<String> columns ) {
            audit.add(  String.format("(%d,%d): row=%s", line, col, formatList(columns)) );
        }

        private static String formatList( List<String> l ) {
            StringBuilder buf = new StringBuilder();

            buf.append('[');

            for ( String v : l ) {
                if ( buf.length() != 1 ) {
                    buf.append(',');
                }

                buf.append('\'');
                buf.append(v);
                buf.append('\'');
            }

            buf.append(']');

            return buf.toString();
        }
    }
}

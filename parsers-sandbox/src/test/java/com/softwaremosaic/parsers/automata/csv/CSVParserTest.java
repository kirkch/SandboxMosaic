package com.softwaremosaic.parsers.automata.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class CSVParserTest {

    private RecordingCSVListener l = new RecordingCSVListener();
    private CSVParser parser = new CSVParser( l );


//    @Test
    public void givenAutomataExpectingAnewlineB_parseAnewlineC_expectErrorAtLine2Col1() {
        int numCharactersConsumed = parser.consume("abc");
        parser.appendEOS();


        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): header=[abc]",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
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

        public void headers( int line, int col, List<String> headers ) {
            audit.add(  String.format("(%d,%d): header=%s", line, col, headers) );
        }

        public void row( int line, int col, List<String> columns ) {
            audit.add(  String.format("(%d,%d): row=%s", line, col, columns) );

        }

        public void finished() {
            audit.add("finished");
        }
    }
}

package com.softwaremosaic.parsers;

import com.softwaremosaic.parsers.automata.Automata;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class ParserTest {

    private Automata                automata = new Automata();
    private RecordingParserListener l        = new RecordingParserListener();


//    @Test
    public void givenBlankAutomata_matchA_expectErrorCallback() {
        Parser parser = Parser.compile( automata, l );

        parser.append("a");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'a', no further input was expected"
        );

        assertEquals( l.audit, expectedAudit );
    }



    @SuppressWarnings("unchecked")
    private static class RecordingParserListener implements ParserListener {
        public final List<String> audit = new ArrayList();

        public void started() {
            audit.add("started");
        }

        public void error(int line, int col, String message) {
            audit.add( String.format("(%d,%d): %s", line, col, message) );
        }

        public void finished() {
            audit.add("finished");
        }
    }

}

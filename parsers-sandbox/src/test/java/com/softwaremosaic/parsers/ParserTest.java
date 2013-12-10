package com.softwaremosaic.parsers;

import com.softwaremosaic.parsers.automata.Automata;
import com.softwaremosaic.parsers.automata.Node;
import org.junit.Assert;
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


    @Test
    public void givenBlankAutomata_matchA_expectErrorCallback() {
        Parser parser = Parser.compile( automata, l );

        parser.append("a");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenBlankAutomata_matchB_expectErrorCallback() {
        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("b");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'b', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenBlankAutomata_matchAB_expectErrorCallback() {
        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("ab");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingA_parseB_expectError() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantA", "a");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("b");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'b', expected 'a -> ConstantA'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingB_parseA_expectError() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantB", "b");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("a");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'a', expected 'b -> ConstantB'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAorB_parseC_expectError() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantA", "a");
        n.appendConstant("ConstantB", "b");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("c");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'c', expected 'a -> ConstantA' or 'b -> ConstantB'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingADorC_parseE_expectErrorThatCollapsesTheSharedDestinationForAB() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAD", "a");
        n.appendConstant("ConstantAD", "d");
        n.appendConstant("ConstantC", "c");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("e");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected character 'e', expected '[ad] -> ConstantAD' or 'c -> ConstantC'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAC_expectErrorAtCol2() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("ac");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,2): unexpected character 'c', expected 'b -> ConstantAB'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 1, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAnewlineB_parseAnewlineC_expectErrorAtLine2Col1() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "a\nb");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("a\nc");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(2,1): unexpected character 'c', expected 'b -> ConstantAB'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAB_expectSuccessfulParsingAndEndOfParsingEvent() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("ab");

        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAThenB_expectStartedParsingEventOnlyOnce() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("a") + parser.append("b");

        List<String> expectedAudit = Arrays.asList(
                "started"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOS_expectStartFinishEvents() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );

        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSTwice_expectException() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );

        parser.appendEOS();


        try {
            parser.appendEOS();

            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSThenA_expectException() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );

        parser.appendEOS();


        try {
            parser.append("a");

            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }



    @Test
    public void givenParserABThatHasAlreadyParsedA_resetThenAppendB_expectSuccessfulReset() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantAB", "ab");


        Parser parser = Parser.compile( automata, l );
        parser.append("a");
        parser.reset();
        l.audit.clear();


        int numCharactersConsumed = parser.append("b");

        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,1): unexpected character 'b', expected 'a -> ConstantAB'",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }


    @Test
    public void givenAutomataExpectingABorC_parseE_expectACRangeInErrorMessage() {
        Node n = automata.getStartingNode();
        n.appendConstant("ConstantABC", "a");
        n.appendConstant("ConstantABC", "b");
        n.appendConstant("ConstantABC", "c");
        n.appendConstant("ConstantD", "d");


        Parser parser = Parser.compile( automata, l );

        int numCharactersConsumed = parser.append("e");

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,1): unexpected character 'e', expected '[a-c] -> ConstantABC' or 'd -> ConstantD'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }


    // collapse ranges in default error messages
    // custom error messages
    // recoveries
    // custom events


    @SuppressWarnings("unchecked")
    private static class RecordingParserListener implements ParserListener {
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
    }

}

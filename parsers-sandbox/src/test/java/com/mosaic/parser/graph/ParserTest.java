package com.mosaic.parser.graph;

import com.mosaic.parser.ProductionRule;
import com.mosaic.parser.ProductionRuleBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 *
 */
public class ParserTest {

    private ProductionRuleBuilder b = new ProductionRuleBuilder();
    private RecordingParserListener l = new RecordingParserListener();


    @Test
    public void givenBlankAutomata_matchA_expectErrorCallback() {
        ProductionRule rule1  = b.constant( "rule1", "" );
        Parser parser = new Parser(rule1, l);


        parser.parse( 'a' );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenBlankAutomata_matchB_expectErrorCallback() {
        ProductionRule rule1  = b.constant( "rule1", "" );
        Parser parser = new Parser(rule1, l);

        int numCharactersConsumed = parser.parse("b");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenBlankAutomata_matchAB_expectErrorCallback() {
        ProductionRule rule1  = b.constant( "rule1", "" );
        Parser parser = new Parser(rule1, l);

        int numCharactersConsumed = parser.parse("ab");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingA_parseB_expectError() {
        ProductionRule rule1  = b.constant( "rule1", "a", CaseSensitive );
        Parser parser = new Parser(rule1, l);



        int numCharactersConsumed = parser.parse( "b" );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', expected 'a'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingB_parseA_expectError() {
        ProductionRule rule1  = b.constant( "rule1", "b", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        int numCharactersConsumed = parser.parse("a");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAorB_parseC_expectError() {
        ProductionRule rule1  = b.terminal( "rule1", "a|b", Void.class );
        Parser parser = new Parser(rule1, l);


        int numCharactersConsumed = parser.parse("c");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'c', expected 'a|b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAC_expectErrorAtCol2() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        int numCharactersConsumed = parser.parse("ac");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,2): unexpected input 'c', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 1, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAnewlineB_parseAnewlineC_expectErrorAtLine2Col1() {
        ProductionRule rule1  = b.constant( "rule1", "a\nb" );
        Parser parser = new Parser(rule1, l);


        int numCharactersConsumed = parser.parse("a\nc");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(2,1): unexpected input 'c', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAB_expectSuccessfulParsingAndEndOfParsingEvent() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        int numCharactersConsumed = parser.parse("ab");
        parser.endOfStream();

        List<String> expectedAudit = Arrays.asList(
            "started",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAThenB_expectStartedParsingEventOnlyOnce() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        int numCharactersConsumed = parser.parse("a") + parser.parse("b");

        List<String> expectedAudit = Arrays.asList(
            "started"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOS_expectError() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);



        parser.endOfStream();

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): end of stream reached when expecting 'a'"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSTwice_expectException() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        parser.parse( "ab" );
        parser.endOfStream();


        try {
            parser.endOfStream();

            fail( "expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "the parser has already been notified of EOS", e.getMessage() );
        }
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSThenA_expectException() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        parser.endOfStream();


        try {
            parser.parse("a");

            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }

    @Test
    public void givenParserABThatHasAlreadyParsedA_resetThenAppendB_expectSuccessfulReset() {
        ProductionRule rule1  = b.constant( "rule1", "ab", CaseSensitive );
        Parser parser = new Parser(rule1, l);


        parser.parse("a");
        parser.reset();
        l.audit.clear();


        int numCharactersConsumed = parser.parse("b");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', expected 'a'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenSingleConstantGrammar_parseValue_expectParseWithNoValue() {
        ProductionRule rootRule = b.constant( "rule1", "hello" );
        Parser         parser   = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "hello" );


        assertEquals( 5, numCharactersParsed );
        assertEquals( 0, parser.getParsedValue().size() );
    }

    @Test
    public void givenSingleConstantGrammarCI_parseValue_expectParseWithNoValue() {
        ProductionRule rootRule = b.constant( "rule1", "bob", CaseInsensitive );
        Parser         parser   = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "bOb" );


        assertEquals( 3, numCharactersParsed );
        assertEquals( 0, parser.getParsedValue().size() );
    }

    @Test
    public void givenSingleConstantCapturingGrammarCI_parseValue_expectParseWithValue() {
        ProductionRule rootRule = b.terminal( "rule1", "bob", CaseInsensitive );
        Parser         parser   = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "bOb" );
        parser.endOfStream();

        assertEquals( 3, numCharactersParsed );
        assertEquals( Arrays.asList("bOb"), parser.getParsedValue() );
    }

    @Test
    public void parseValueButNotEOS_expectCharactersToBeConsumedButParseValueIsYetToBeTurnedIntoAString() {
        ProductionRule rootRule = b.terminal( "rule1", "bob", CaseInsensitive );
        Parser         parser   = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "bOb" );

        assertEquals( 3, numCharactersParsed );
        assertEquals( Arrays.asList("bOb"), parser.getParsedValue() );
    }

    @Test
    public void givenRuleWithCallback_parseRuleSuccessfully_expectCallback() {
        ProductionRule rootRule = b.terminal( "rule1", "[a-zA-Z]+", String.class )
            .withCallback(RecordingParserListener.class, "hello");

        Parser parser = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "Bob" );
        parser.endOfStream();

        assertEquals( 3, numCharactersParsed );
        assertEquals( Arrays.asList("Bob"), parser.getParsedValue() );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): Welcome 'Bob'",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }



// NON TERMINALS

    @Test
    public void givenNonTerminalFollowedByAnotherRule_parseMatchingText_expectSuccess() {
        b.constant( "HelloRule", "Hello" );
        b.terminal( "NameRule", "[a-zA-Z]+", Void.class );

        ProductionRule rootRule = b.nonTerminal( "RootRule", "$HelloRule $NameRule" );

        Parser parser = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "Hello Bob" );
        parser.endOfStream();

        assertEquals( 9, numCharactersParsed );
        assertEquals( Arrays.asList(), parser.getParsedValue() );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenNonTerminalFollowedByAnotherRule_parseCapturingMatchingText_expectValueBack() {
        b.constant( "HelloRule", "Hello" );
        b.terminal( "NameRule", "[a-zA-Z]+", String.class );

        ProductionRule rootRule = b.nonTerminal( "RootRule", "$HelloRule $NameRule" );

        Parser parser = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "Hello Bob" );
        parser.endOfStream();

        assertEquals( 9, numCharactersParsed );
        assertEquals( Arrays.asList("Bob"), parser.getParsedValue() );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenNonTerminalFollowedByAnotherRule_parseMatchingTextWithCallback_expectErrorAsNoValueWIllHaveBeenCaptured() {
        b.constant( "HelloRule", "Hello" );

        b.terminal( "WhitespaceRule", "[ \t]+", Void.class );


        try {
            b.terminal( "NameRule", "[a-zA-Z]+", Void.class ).withCallback( RecordingParserListener.class, "hello" );

            Assert.fail( "expected IllegalStateException" );
        } catch ( IllegalStateException e ) {
            Assert.assertEquals( "Unable to append action 'hello' as the return type of the production rule 'NameRule' is 'Void'", e.getMessage() );
        }
    }

    @Test
    public void givenNonTerminalFollowedByAnotherRule_parseMatchingTextWithCallbackAndCaptureValue_expectCallback() {
        b.constant( "HelloRule", "Hello" );
        b.terminal( "NameRule", "[a-zA-Z]+", String.class ).withCallback(RecordingParserListener.class,"hello");

        ProductionRule rootRule = b.nonTerminal( "RootRule", "$HelloRule $NameRule" );

        Parser parser = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "Hello Bob" );
        parser.endOfStream();

        assertEquals( 9, numCharactersParsed );
        assertEquals( Arrays.asList("Bob"), parser.getParsedValue() );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,6): Welcome 'Bob'",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

//    @Test
    public void givenComplexNonTerminalFollowedByAnotherRule_parseMatchingTextWithCallbackAndCaptureValue_expectCallback() {
        b.constant( "HelloRule", "Hello" );
        ProductionRule nameRule = b.terminal( "NameRule", "[a-zA-Z]+|\"[^\"]+\"", String.class ).withCallback(RecordingParserListener.class,"hello");
        ProductionRule rootRule = b.nonTerminal( "RootRule", "$HelloRule $NameRule" );

        Parser parser = new Parser( rootRule, l );

        int numCharactersParsed = parser.parse( "Hello Bob" );
        parser.endOfStream();

        assertEquals( 9, numCharactersParsed );
        assertEquals( Arrays.asList("Bob"), parser.getParsedValue() );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,6): Welcome 'Bob'",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }



    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public static class RecordingParserListener implements ParserListener {
        public final List<String> audit = new ArrayList();

        public void started() {
            audit.add("started");
        }

        public void error( int line, int col, String message ) {
            audit.add( String.format("(%d,%d): %s", line, col, message) );
        }

        public void hello( int line, int col, String name ) {
            audit.add(  String.format("(%d,%d): Welcome '%s'", line, col, name) );
        }

        public void list( int line, int col, List<String> l ) {
            audit.add(  String.format("(%d,%d): list=%s", line, col, l) );
        }

        public void finished() {
            audit.add("finished");
        }
    }

}

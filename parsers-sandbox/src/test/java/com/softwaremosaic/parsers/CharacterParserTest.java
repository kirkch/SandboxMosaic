package com.softwaremosaic.parsers;

import com.mosaic.lang.CaseSensitivity;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.ProductionRule;
import com.softwaremosaic.parsers.automata.regexp.StringOp;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class CharacterParserTest {

    private RecordingParserListener l = new RecordingParserListener();


    @Test
    public void givenBlankAutomata_matchA_expectErrorCallback() {
        ProductionRule  rule1  = ProductionRule.terminal( new LabelNode() );
        CharacterParser parser = new CharacterParser(rule1, l);


        parser.consume( 'a' );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenBlankAutomata_matchB_expectErrorCallback() {
        ProductionRule  rule1  = ProductionRule.terminal( new LabelNode() );
        CharacterParser parser = new CharacterParser(rule1, l);

        int numCharactersConsumed = parser.consume("b");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenBlankAutomata_matchAB_expectErrorCallback() {
        ProductionRule  rule1  = ProductionRule.terminal( new LabelNode() );
        CharacterParser parser = new CharacterParser(rule1, l);

        int numCharactersConsumed = parser.consume("ab");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingA_parseB_expectError() {
        LabelNode<Character> n = new LabelNode();
        n.append( 'a' );


        ProductionRule  rule1  = ProductionRule.terminal( n );
        CharacterParser parser = new CharacterParser(rule1, l);



        int numCharactersConsumed = parser.consume( "b" );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', expected 'a'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingB_parseA_expectError() {
        LabelNode<Character> n = new LabelNode();
        n.append( 'b' );


        ProductionRule  rule1  = ProductionRule.terminal( n );
        CharacterParser parser = new CharacterParser(rule1, l);



        int numCharactersConsumed = parser.consume("a");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAorB_parseC_expectError() {
        LabelNode<Character> n = new LabelNode();
        n.append( 'a' );
        n.append( 'b' );


        ProductionRule  rule1  = ProductionRule.terminal( n );
        CharacterParser parser = new CharacterParser(rule1, l);


        int numCharactersConsumed = parser.consume("c");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'c', expected 'a|b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAC_expectErrorAtCol2() {
        LabelNode<Character> n1 = new LabelNode();
        Nodes n2 = n1.append( 'a' );
        n2.append('b');


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        int numCharactersConsumed = parser.consume("ac");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,2): unexpected input 'c', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 1, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAnewlineB_parseAnewlineC_expectErrorAtLine2Col1() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "a\nb", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        int numCharactersConsumed = parser.consume("a\nc");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(2,1): unexpected input 'c', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAB_expectSuccessfulParsingAndEndOfParsingEvent() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        int numCharactersConsumed = parser.consume("ab");

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
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        int numCharactersConsumed = parser.consume("a") + parser.consume("b");

        List<String> expectedAudit = Arrays.asList(
                "started"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOS_expectStartFinishEvents() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);



        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSTwice_expectException() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        parser.appendEOS();


        try {
            parser.appendEOS();

            Assert.fail( "expected IllegalStateException" );
        } catch (IllegalStateException e) {
            Assert.assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSThenA_expectException() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "a\nb", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        parser.appendEOS();


        try {
            parser.consume("a");

            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }

    @Test
    public void givenParserABThatHasAlreadyParsedA_resetThenAppendB_expectSuccessfulReset() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);


        parser.consume("a");
        parser.reset();
        l.audit.clear();


        int numCharactersConsumed = parser.consume("b");

        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,1): unexpected input 'b', expected 'a'",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingABCorD_parseE_expectADRangeInErrorMessage() {
        LabelNode<Character> n1 = new LabelNode();
        Label<Character> label = Labels.orLabels( Labels.characterRange( 'a', 'd' ), Labels.singleValue('f') );

        n1.append( label );


        ProductionRule  rule1  = ProductionRule.terminal( n1 );
        CharacterParser parser = new CharacterParser(rule1, l);



        int numCharactersConsumed = parser.consume("e");

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,1): unexpected input 'e', expected '[a-d]|f'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }


// Multiple production rules

    // SENTANCE   = $LIFE $WHITESPACE $ROCKS
    // LIVE       = life
    // WHITESPACE =  +
    // ROCKS      = rocks
    @Test
    public void givenThreeTerminals_parseLifeRocks_expectSuccess() {
        ProductionRule rule1 = ProductionRuleBuilder.terminalConstant( "life" );
        ProductionRule rule2 = ProductionRuleBuilder.terminalRegexp( " +" );
        ProductionRule rule3 = ProductionRuleBuilder.terminalConstant( "rocks" );

        ProductionRule rootRule = ProductionRule.nonTerminal( rule1, rule2, rule3 );


        CharacterParser parser = new CharacterParser( rootRule, l );




        int numCharactersConsumed = parser.consume("life rocks");
        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
            "started",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 10, numCharactersConsumed );
    }



// custom actions

    // hello = hello
    // name = [a-zA-Z]+
    // helloName = $hello $NAME

    @Test
    public void givenHelloParser_parseHelloJim_expectHelloCallback() {
        ProductionRule rule1 = ProductionRuleBuilder.terminalConstant( "Hello" ).withLabel( "HelloRule" );
        ProductionRule rule2 = ProductionRuleBuilder.terminalRegexp( " +" ).withLabel( "WhiteSpaceRule" );
        ProductionRule rule3 = ProductionRuleBuilder.terminalRegexp( "[a-zA-Z]+" ).withLabel( "NameRule" )
            .withCapture( true )
            .withCallback( RecordingParserListener.class, "hello", String.class );

        ProductionRule rootRule = ProductionRule.nonTerminal( rule1, rule2, rule3 ).withLabel( "RootLabel" );


        CharacterParser parser = new CharacterParser(rootRule, l);


        int numCharactersConsumed = parser.consume("Hello Jim");
        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,7): Welcome 'Jim'",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 9, numCharactersConsumed );
    }


//    @Test
    public void givenNestedCapturingRules_expectResultToFilterUp() {
        ProductionRule nameRule = ProductionRuleBuilder.terminalRegexp( "[a-zA-Z]+" ).withLabel( "NameRule" )
            .withCapture( true );


        ProductionRule rootRule = ProductionRule.nonTerminal( nameRule ).withLabel( "RootRule" )
            .withCapture( true )
            .withCallback( RecordingParserListener.class, "list", List.class );


        CharacterParser parser = new CharacterParser( rootRule, l );




        int numCharactersConsumed = parser.consume("Jim");
        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): list=[Jim]",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 10, numCharactersConsumed );
    }


    // custom error messages
    // recoveries

    

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
